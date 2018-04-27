<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.firefly.common.Utils"%>
<%@page import="com.firefly.common.Constant"%>
<%@page import="java.util.TreeMap"%>
<%@page import="com.firefly.pay.getHttpData"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%
	//得到输入的商品名称和价格
	String goodsName = request.getParameter("goodsName");
	String price = request.getParameter("price");

	if (goodsName == null || goodsName.trim().equals("")) {
		out.print("商品名不能为空");
	}
	if (price == null || price.trim().equals("")) {
		out.print("商品价格不能为空");
	}
	int goodsPrice = Integer.parseInt(price);

	//把相关参数传给微信支付
	TreeMap<String, Object> map = new TreeMap<String, Object>();
	map.put("appid", Constant.OPEN_ID);
	map.put("mch_id", Constant.SW_ID);
	map.put("nonce_str", Utils.getRandomStr());
	map.put("body", goodsName);
	map.put("out_trade_no", Utils.getRandomStr());
	map.put("total_fee", goodsPrice);
	map.put("spbill_create_ip", request.getLocalAddr());
	map.put("notify_url",
			"http://139.196.196.64:8080/PayServer/mypay.jsp");
	map.put("trade_type", "APP");
	map.put("sign", Utils.getSignStr(map));
	
	//把map转换成xml，并发送到微信支付接口
	String info = Utils.map2xml(map);
	String i = getHttpData.sendMsg(Constant.ORDER, info);

	//接收它的返回信息
	Map<String, Object> returnMap = Utils.xml2Map(i);
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
	out.print(inf);
%>