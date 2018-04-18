<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" href="css/toast.css" />
  </head>
  
  <body>
  	<script type="text/javascript" src="js/jquery-2.1.0.js" ></script>
	<script type="text/javascript" src="js/jquery.toast.js" ></script>
	
	<form style="width:400px;margin:auto" action="testSMS.jsp">
		<input type="text" id="phone" name="phone" placeholder="请输入手机号码"><br/><br/>
		<input type="text" name="code" placeholder="请输入验证码"><br/><br/>
		<button id="btn" >获取验证码</button>	<br/><br/>
		<input type="submit" value="提交测试" />
	</form>
	<script>
			$(function(){
				$("#btn").timeBtn({time:60,onClick:function(){
					$.getJSON("sendSMS.jsp?mobile="+$("#phone").val(),function(data, type){
						$("body").toast({msg:data.msg,top:200});
    				});
				}});
			})
	</script>
  </body>
</html>
