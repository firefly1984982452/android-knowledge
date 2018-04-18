<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";


String code = request.getParameter("code");


if(code != null && code.equals(session.getAttribute("code"))){
	out.print("success");
}else{
	out.print("error");
}

%>
