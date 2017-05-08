package xunqaing.bwie.com.photographtosdcard;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by : Xunqiang
 * 2017/4/18 18:34
 */

public class FileUtils {

    // 设置缓存目录的位置

    /**
     * itools --- getExternalCacheDir().getPath()
     *
     * /storage/emulated/0/Android/data/xunqaing.bwie.com.photographtosdcard/cache
     *
     *
     * itools --- getCacheDir().getPath()
     *
     * /data/data/xunqaing.bwie.com.photographtosdcard/cache
     *
     *
     * 手机 --- getExternalCacheDir().getPath()
     *
//     /storage/emulated/0/Android/data/xunqaing.bwie.com.photographtosdcard/cache
     *
     *
     *手机 --- getCacheDir().getPath()
     *
     * /data/data/xunqaing.bwie.com.photographtosdcard/cache
     *
     *
     */

    /**
     *
     * @param context
     * @param uniqueName 缓存的目录名称
     * @return
     */

    public static File getDiskCacheDir(Context context, String uniqueName) {

        String cachePath;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            //sdcard
            cachePath = context.getExternalCacheDir().getPath();

        } else {
            //内存
            cachePath = context.getCacheDir().getPath();
        }

        System.out.println("cachePath = " + cachePath);

        //File.separator --- / 文件分隔符
        return new File(cachePath + File.separator + uniqueName);
    }


    //获取应用程序的版本号
    public static int getAppVersion(Context context) {
        try {
            //获取到包管理器，很多的app都能读取到
            PackageManager packageManager =  context.getPackageManager() ;
            //获取当前 应用程序的包名
            String packageName =  context.getPackageName() ;

            // 获取apk 包的信息
            PackageInfo info =  packageManager.getPackageInfo(packageName,0);


//            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }





    // md5 算法
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }




}
