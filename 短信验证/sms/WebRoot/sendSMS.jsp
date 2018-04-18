<%@page import="com.style.util.SMSUtil"%>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";


String mobile = request.getParameter("mobile");

if(mobile == null || "".equals(mobile.trim())){
	out.print("{\"msg\":\"号码不能为空\"}");
	return;
}

StringBuffer sb = new StringBuffer();

Random rd = new Random();
for(int i=0;i<6;i++){
	sb.append(rd.nextInt(10));
}
session.setAttribute("code", sb.toString());//把随机数保存到了session中

String rs = SMSUtil.sendSms("【思泰旭嘉】您的验证码是"+sb.toString(), mobile);
System.out.println(rs);
out.print(rs);



%>
