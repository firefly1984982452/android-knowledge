package style.app.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by huanganwei on 2016-05-19.
 */
public class HttpsUtil {

    public static  final  Integer HTTP_OK = 200;
    public static  final  Integer HTTP_ERROR = 100;



    public interface CallBack{
        public void onSuccess(String str);
        public void onError(Throwable e);
    }


    public static  void post(String url,CallBack callBack){
        post(url,null,callBack);
    }


    public static void post(String url,String data,CallBack callBack){
        final String xmlData = data;
        final String _url = url;
        final CallBack _callBack = callBack;
        new AsyncTask<Void, Void, Integer>() {
            String str = null;
            Throwable throwable = null;
            @Override
            //连接网络，获取数据
            protected Integer doInBackground(Void... params) {

                try {
                    Log.e("TAG",_url);
                    URL theUrl = new URL(_url);
                    HttpsURLConnection conn =  (HttpsURLConnection)theUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(30000);
                    conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");


                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    conn.setRequestProperty("Content-Type","text/xml; charset=UTF-8");
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                    osw.write(xmlData);
                    osw.flush();
                    osw.close();

                    if(conn.getResponseCode() == 200){
                        InputStream in = conn.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int len =0;
                        while((len = in.read(buf)) != -1 ){
                            bos.write(buf,0,len);
                        }
                        str = new String(bos.toByteArray(),"utf-8");
                        in.close();
                        bos.close();
                        conn.disconnect();
                        return HTTP_OK;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throwable = e;
                    return HTTP_ERROR;
                }
                return HTTP_ERROR;
            }

            @Override
            //将数据交给回调的接口
            protected void onPostExecute(Integer result) {
                if(result == HTTP_OK){
                    _callBack.onSuccess(str);
                }else{
                    if(throwable == null){
                        throwable = new RuntimeException("网络获取异常");
                    }
                    _callBack.onError(throwable);
                }
            }
        }.execute();
    }


}
