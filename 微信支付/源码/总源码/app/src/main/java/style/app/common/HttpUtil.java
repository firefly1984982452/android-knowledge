package style.app.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by huanganwei on 2016-05-19.
 */
public class HttpUtil {

    public static  final  Integer HTTP_OK = 200;
    public static  final  Integer HTTP_ERROR = 100;



    public interface CallBack{
        public void onSuccess(String str);
        public void onError(Throwable e);
    }


    public static byte[] get(String url, CallBack callBack){
        get(url,null,callBack);
        return new byte[0];
    }



    private static String mapToString(Map<String,Object> map){
        StringBuffer sb = null;
        if(map != null && map.size()>0){
            sb = new StringBuffer();
            Set<String> set = map.keySet();
            for (String s:set) {
                sb.append(s+"="+map.get(s));
                sb.append("&");
            }
        }
        return sb == null ? null :sb.toString().substring(0,sb.length()-1);
    }


    public static void get(String url,Map<String,Object> map,CallBack callBack){
        String pramas  =  mapToString(map);
        if(pramas != null){
            //地址自身没有带参数
            if(url.lastIndexOf("?") == -1){
                url = url + "?" + pramas;
            }else{//地址自身带有参数
                url = url + "&" + pramas;
            }
        }

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
                   HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();
                   conn.setRequestMethod("GET");
                   conn.setReadTimeout(3000);
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
