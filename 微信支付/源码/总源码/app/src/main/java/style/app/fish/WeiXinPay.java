package style.app.fish;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.TreeMap;

import style.app.common.Constant;
import style.app.common.HttpsUtil;
import style.app.common.Utils;
import style.app.common.getHttpData;

/**
 * Created by pengdan on 2016/6/30.
 */
public class WeiXinPay {
    private static WeiXinPay ourInstance;
    private Context context;

    //单例
    public synchronized static WeiXinPay getInstance(Context context) {
        if (ourInstance==null){
            ourInstance = new WeiXinPay(context);
        }
        return ourInstance;
    }

    //提供接口
    public interface CallBack{
        public void onSuccess(String str);
        public void onFail(Throwable e);
    }

    //构造方法
    public WeiXinPay(Context context) {
        this.context = context;
    }

    public void getWXPay(String goodsName, int price, final WeiXinPay.CallBack callBack){

        //把相关参数传给微信支付
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("appid", Constant.OPEN_ID);
        map.put("mch_id", Constant.SW_ID);
        map.put("nonce_str", Utils.getRandomStr());
        map.put("body", goodsName);
        map.put("out_trade_no", Utils.getRandomStr());
        map.put("total_fee", price);
        map.put("spbill_create_ip",Constant.IP);
        map.put("notify_url",
                "http://139.196.196.64:8080/PayServer/result.jsp");
        map.put("trade_type", "APP");
        map.put("sign", Utils.getSignStr(map));

        //把map转换成xml，并发送到微信支付接口
        String info = Utils.map2xml(map);


        HttpsUtil.post(Constant.ORDER, info, new HttpsUtil.CallBack() {
            @Override
            public void onSuccess(String str) {
                //接收它的返回信息
                Map<String, Object> returnMap = Utils.xml2Map(str);
                if (returnMap.get("result_code") == null || !returnMap.get("result_code").equals("SUCCESS")){
                    Toast.makeText(context, returnMap.get("return_msg").toString(), Toast.LENGTH_SHORT).show();
                }else{
                    //返回的键要相对应，所以要改过来
                    TreeMap<String, Object> resultmap = new TreeMap<String, Object>();
                    resultmap.put("appid", Constant.OPEN_ID);
                    resultmap.put("partnerid", Constant.SW_ID);
                    resultmap.put("prepayid",returnMap.get("prepay_id"));
                    resultmap.put("noncestr",returnMap.get("nonce_str"));
                    resultmap.put("timestamp",Utils.getDataStr("yyyyMMddHH"));
                    resultmap.put("package","WXPay");
                    resultmap.put("sign",Utils.getSignStr(resultmap));

                    String inf = JSON.toJSONString(resultmap);
                    callBack.onSuccess(inf);
                    Log.e("TAG",inf);
                }
            }

            @Override
            public void onError(Throwable e) {
                callBack.onFail(e);
            }
        });

    }
}
