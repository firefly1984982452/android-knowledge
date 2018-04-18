package style.app.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Administrator on 2016/6/30.
 */
public class WeixinPayUtil {
    private static WeixinPayUtil ourInstance;
    private Context context;

    public synchronized static WeixinPayUtil getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new WeixinPayUtil(context);
        }
        return ourInstance;
    }

    private WeixinPayUtil(Context context) {
        this.context = context;
    }

    public interface CallBack{
        public void onSuccess(String str);
        public void onFail(Throwable e);
    }



    public void pay(String goodsName,int godsPrice, final WeixinPayUtil.CallBack callBack){
        //2.准备相关的参数
        TreeMap<String,Object> map = new TreeMap<String,Object>();
        map.put("appid",Constants.APP_ID);
        map.put("mch_id",Constants.SW_ID);
        map.put("nonce_str",getRandomNum());
        map.put("body",goodsName);
        map.put("out_trade_no",getRandomNum());
        map.put("total_fee",godsPrice);
        map.put("spbill_create_ip",Constants.IP);
        map.put("notify_url",Constants.WEIXIN_NOTIFY_URL);
        map.put("trade_type","APP");
        map.put("sign",getSign(map));


        //3.得到xml的参数字符串
        String xmlData = map2Xml(map);

        //4.发送给微信系统,获取数据
        HttpsUtil.post(Constants.ORDER_URL, xmlData, new HttpsUtil.CallBack() {
            @Override
            public void onSuccess(String str) {
                //5.解析微信系统发送过来的信息
                Map<String,Object> resultMap = xml2Map(str);
                if(resultMap.get("result_code") == null || !resultMap.get("result_code").equals("SUCCESS")) {
                    Toast.makeText(context, resultMap.get("return_msg").toString(), Toast.LENGTH_SHORT).show();
                }else{
                    //6.返回给客户端用的map
                    TreeMap<String,Object> returnMap = new TreeMap<String,Object>();
                    returnMap.put("appid",Constants.APP_ID);
                    returnMap.put("partnerid",Constants.SW_ID);
                    returnMap.put("prepayid",resultMap.get("prepay_id"));
                    returnMap.put("package","Sign=WXPay");
                    returnMap.put("noncestr",resultMap.get("nonce_str"));
                    returnMap.put("timestamp",getDateStr("yyyyMMddHH"));
                    returnMap.put("sign", getSign(returnMap));
                    //7.给客户端返回json字符串
                    Log.e("TAG", returnMap.toString());
                    Log.e("TAG", JSON.toJSONString(returnMap));
                    callBack.onSuccess(JSON.toJSONString(returnMap));
                }
            }

            @Override
            public void onError(Throwable e) {
                callBack.onFail(e);
            }
        });
    }







    public  String getRandomNum(){
        StringBuffer rdNum = new StringBuffer();
        Random rd = new Random();
        String s = "QWERTYUIOPLKJHGFDSAZXCVBNM1234567890";

        for (int i = 0; i < 32; i++) {
            rdNum.append(s.charAt(rd.nextInt(s.length()))+"");
        }

        return rdNum.toString();
    }


    public  String getDateStr(String pattern){
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(d);
    }


    public  String getSign(TreeMap<String,Object> map){
        String strA  = "";
        Set<String> keys =  map.keySet();
        for (String key : keys) {
            strA += key+"="+map.get(key)+"&";
        }
        strA +="key="+ Constants.KEY;
        return MD5.MD5Encode(strA).toUpperCase();
    }


    public  String  map2Xml(Map<String,Object> map){
        StringBuffer strXml = new StringBuffer();
        strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        strXml.append("<xml>");
        Set<String> keys =  map.keySet();
        for (String key : keys) {
            strXml.append("<"+key+">"+map.get(key)+"</"+key+">" );
        }
        strXml.append("</xml>");
        return strXml.toString();
    }



    public  Map<String,Object> xml2Map(String xmlStr){
        Map<String,Object> map = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //将字符串转换成流
            ByteArrayInputStream bis = new ByteArrayInputStream(xmlStr.getBytes());
            Document doc =  builder.parse(bis);
            Node root = doc.getFirstChild();//根节点
            NodeList nodeList = root.getChildNodes();
            map = new HashMap<String,Object>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if(child instanceof Element){
                    Element e = (Element) child;
                    map.put(e.getNodeName(), e.getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public  String sendMsg(String url,String xmlData){
        String s = null;
        try {
            URL u  = new URL(url);
            HttpsURLConnection conn =  (HttpsURLConnection) u.openConnection();
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


            if (conn.getResponseCode() == 200) {


                InputStream in = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                if (in != null) {
                    while ((len = in.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    in.close();
                    bos.close();
                }
                s = new String(bos.toByteArray());
                System.out.println("s:"+s);
            }else{
                System.out.println(conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


}
