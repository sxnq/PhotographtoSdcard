package xunqaing.bwie.com.photographtosdcard;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
//先粘贴一个类  --- DiskLruCache.java
//    磁盘缓存主要涉及到DiskLruCache这个类

    MyHandler handler = new MyHandler(this);
    private HealthListAdapter adapter;
    public List<Bean.ResultBean.DataBean> list = new ArrayList<Bean.ResultBean.DataBean>();
    private ListView listView;
    private LruCache<String, Bitmap> lruCache;
    private DiskLruCache diskLruCache;

    static  class  MyHandler extends Handler {

        WeakReference<MainActivity> weakReference = null ;

        public MyHandler(MainActivity mainActivity){
            // mainactivity 放入 WeakReference（弱引用）
            weakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //weakReference.get() 获取 MainActivity
            MainActivity activity = weakReference.get() ;
            if (activity == null){
                System.out.println("msg = handleMessage weakReference.get()" );
                return;
            }

            switch (msg.what) {
                case 1:
                    List<Bean.ResultBean.DataBean> temp = (List<Bean.ResultBean.DataBean>) msg.obj;
                    activity.list.addAll(temp);
                    activity.adapter.notifyDataSetChanged();

                    break;
                case 2:
                    activity.adapter.notifyDataSetChanged();
                    break;
                case 3:

                    activity.updateUI();
                    break;
            }

        }
    }


    //
    //
    //    Handler handler = new Handler() {
    //
    //        @Override
    //        public void handleMessage(Message msg) {
    //            super.handleMessage(msg);
    //
    //            switch (msg.what) {
    //                case 1:
    //
    //                    System.out.println("msg = handleMessage 1" );
    //                    break;
    //            }
    //        }
    //    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //获取到当前手机 为当前app 所开辟的内存
        int memory = manager.getMemoryClass();




        // DiskLrucache

        /**
         * directory  缓存文件目录
         * appVersion 版本信息
         *  都写1 一个路径 缓存一个文件
         *  maxSize 缓存的大小
         */
        try {
            diskLruCache = DiskLruCache.open(FileUtils.getDiskCacheDir(this,"/image"),FileUtils.getAppVersion(this),1,10*1000*100);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //手机为其分配多少缓存空间（当前手机为当前应用所分配内存空间的1/8）
        int cacheSize = memory * 1024 * 1024  / 8 ;

        lruCache = new LruCache<String, Bitmap>(cacheSize){

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return  value.getByteCount();
            }
        };

        listView = (ListView) findViewById(R.id.httpurl_listview);
        adapter = new HealthListAdapter(this, list,lruCache);
        listView.setAdapter(adapter);

        getData();

    }




    //    聚盒子 医药健康健康资讯接口测试 (返回旧版本)
    private void getData() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                String path = "http://v.juhe.cn/toutiao/index?type=top&key=75b5a3f1d5a54167236873b0370c4684";

                //String path = "http://qhb.2dyt.com/Bwei/login?username=11&password=12&postkey=bwei";
                URL geturl = null;
                InputStream inputStream = null;
                HttpURLConnection httpURLConnection = null;
                try {
                    geturl = new URL(path);
                    httpURLConnection = (HttpURLConnection) geturl.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(20000);
                    httpURLConnection.setReadTimeout(20000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    System.out.println(httpURLConnection.getResponseCode());
                    if (httpURLConnection.getResponseCode() == 200) {
                        inputStream = httpURLConnection.getInputStream();
                        String result = StringUtils.inputStreamToString(inputStream);
                        System.out.println("result = " + result);

                        try {


                            Gson gson = new Gson();
                            Bean bean =  gson.fromJson(result,Bean.class);

                            JSONObject jsonObject = (JSONObject) JSON.parseObject(result);
                            String resultString = jsonObject.getString("result");
                            JSONObject objectResult =  JSON.parseObject(resultString);
                            String listString = objectResult.getString("list");

                            List<HealthList> resultList =  JSON.parseArray(listString,HealthList.class);
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = bean.getResult().getData() ;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有handler 消息
        handler.removeCallbacksAndMessages(null);

    }

    //刷新界面
    private void updateUI(){

        adapter.notifyDataSetChanged();
    }


    // 下载图片并且显示

    public void downloadImage(final String path){


        new  Thread(new Runnable() {
            @Override
            public void run() {


                HttpURLConnection connection = null ;
                InputStream inputStream = null ;
                try {
                    URL url = new URL(path);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    if(connection.getResponseCode() == 200){
                        inputStream =   connection.getInputStream() ;

                        String key =  FileUtils.hashKeyForDisk(path);
                        //把输入流存入本地文件
                        DiskLruCache.Editor editor =  diskLruCache.edit(key);
                        if(editor != null){
                            OutputStream outputStream =  editor.newOutputStream(0);

                            byte [] buffer = new byte[1024];
                            int length = 0 ;
                            while ((length = inputStream.read(buffer)) != -1){
                                outputStream.write(buffer,0,length);
                            }
                            outputStream.flush();
                            editor.commit();
                            diskLruCache.flush();
                            //获取图片快照
                            DiskLruCache.Snapshot snapshot = (DiskLruCache.Snapshot) diskLruCache.get(key) ;
                            if(snapshot != null){

                                //先在内存中找
                                lruCache.put(key,BitmapFactory.decodeStream(snapshot.getInputStream(0)));
                            }
                        }else{

                            lruCache.put(path,BitmapFactory.decodeStream(inputStream));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {

                    if(connection != null){
                        connection.disconnect();
                    }
                    try {
                        if(inputStream != null){
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(3);
                }

            }
        }).start();



    }







}
