package com.style.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class getHttpData {
	public static String sendMsg(String url,String xmlData){
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
				
				System.out.println("进来了................");
				
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

