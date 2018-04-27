package style.app.fish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import style.app.common.Constant;
import style.app.common.HttpUtil;

public class MainActivity extends AppCompatActivity {
    Button appayBtn, appayBtn2;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        init();
    }

    private void init() {
        api = WXAPIFactory.createWXAPI(this, Constant.OPEN_ID);
        appayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*//                String url = "http://139.196.196.64:8080/PayServer/mypay.jsp";
                String name = null;
                try {
                    name = URLEncoder.encode("你好","utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = "http://172.16.46.114:8080/Pay/pay.jsp?goodsName="+name+"&price=11";
                */
                Toast.makeText(MainActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
                WeiXinPay.getInstance(getApplicationContext()).getWXPay("hello", 20, new WeiXinPay.CallBack() {
                    @Override
                    public void onSuccess(String content) {
                        try {
                            Log.e("get server pay params:", content);
                            if (content != null) {
                                JSONObject json = new JSONObject(content);
                                if (null != json && !json.has("retcode")) {
                                    PayReq req = new PayReq();
                                    req.appId = json.getString("appid");
                                    req.partnerId = json.getString("partnerid");
                                    req.prepayId = json.getString("prepayid");
                                    req.nonceStr = json.getString("noncestr");
                                    req.timeStamp = json.getString("timestamp");
                                    req.packageValue = json.getString("package");
                                    req.sign = json.getString("sign");
                                    req.extData = "app data"; // optional
                                    Toast.makeText(MainActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                                    api.sendReq(req);
                                } else {
                                    Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                                    Toast.makeText(MainActivity.this, "返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("PAY_GET", "服务器请求错误");
                                Toast.makeText(MainActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("PAY_GET", "异常：" + e.getMessage());
                            Toast.makeText(MainActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        appayBtn.setEnabled(true);
                    }

                    @Override
                    public void onFail(Throwable e) {

                        Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        appayBtn.setEnabled(true);
                    }


                });


            }
        });
    }

    private void initView() {
        appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn2 = (Button) findViewById(R.id.appay_btn2);
        appayBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PayDemoActivity.class));
            }
        });
    }
}
