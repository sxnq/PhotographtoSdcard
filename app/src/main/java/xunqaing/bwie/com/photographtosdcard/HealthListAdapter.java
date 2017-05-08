package xunqaing.bwie.com.photographtosdcard;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

/**
 * Created by muhanxi on 17/4/11.
 */

public class HealthListAdapter extends BaseAdapter {

    private List<Bean.ResultBean.DataBean> lists;
    private MainActivity context;
    private LruCache<String,Bitmap> lruCache;
    public HealthListAdapter(MainActivity context, List list, LruCache<String,Bitmap> lruCache){
        this.context = context;
        this.lists = list;
        this.lruCache = lruCache;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null ;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.test_health_adapter,null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.left_image);
            viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.test_healthtv);
            viewHolder.textViewTime = (TextView) convertView.findViewById(R.id.test_healthtv_timer);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewDescription.setText(lists.get(position).getTitle());
        viewHolder.textViewTime.setText(lists.get(position).getAuthor_name());

        String path =  lists.get(position).getThumbnail_pic_s() ;

        //如果第一次没有，去下载
        if(lruCache.get(path) == null){
            context.downloadImage(path);
        }else{
            viewHolder.imageView.setImageBitmap(lruCache.get(path));
        }



//        ImageLoader.getInstance().displayImage(lists.get(position).getImg(),viewHolder.imageView);

//        context.loadImage(lists.get(position).getImg(),viewHolder.imageView);


        return convertView;
    }


    static class ViewHolder {

        ImageView imageView;
        TextView textViewDescription;
        TextView textViewTime;

    }

}
