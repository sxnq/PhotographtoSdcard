package xunqaing.bwie.com.photographtosdcard;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by muhanxi on 17/4/3.
 */

public class StringUtils {


    public static String inputStreamToString(InputStream inputStream) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];

            int length = 0;

            while ((length = inputStream.read(buf)) != -1) {

                byteArrayOutputStream.write(buf, 0, length);

            }

            return new String(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String inputStraemToStringBuffer(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return stringBuilder.toString();
        }
    }


    public static String millisecondToDate(long time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(time);
        return formatter.format(date) ;
    }


}
