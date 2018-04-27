package com.firefly.common;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {

	// 得到32位随机数
	public static String getRandomStr() {
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		String str = "QWERTYUIOPASDFGHJKLZXCVBNM0123456789";
		for (int i = 0; i < 32; i++) {
			sb.append(str.charAt(r.nextInt(str.length())));
		}
		return sb.toString();
	}

	// 得到sign
	public static String getSignStr(TreeMap<String, Object> map) {
		String info = "";
		Set<String> keySet = map.keySet();
		for (String string : keySet) {
			info += string + "=" + map.get(string) + "&";
		}
		info += "key=" + Constant.API_KEY;
		return MD5.MD5Encode(info).toUpperCase();
	}

	// 得到日期
	public static String getDataStr(String type) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(type);
		return format.format(date);
	}

	// 转换为xml
	public static String map2xml(TreeMap<String, Object> map) {
		String info = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		info += "<xml>";
		Set<String> keySet = map.keySet();
		for (String string : keySet) {
			info += "<" + string + ">" + map.get(string) + "</" + string + ">";
		}
		info += "</xml>";
		return info;
	}

	public static Map<String, Object> xml2Map(String xmlStr) {
		Map<String, Object> map = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 将字符串转换成流
			ByteArrayInputStream bis = new ByteArrayInputStream(xmlStr
					.getBytes());
			Document doc = builder.parse(bis);
			Node root = doc.getFirstChild();// 根节点
			NodeList nodeList = root.getChildNodes();
			map = new HashMap<String, Object>();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element e = (Element) child;
					map.put(e.getNodeName(), e.getTextContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	// 测试
	public static void main(String[] args) {

		// System.out.println("随机："+Utils.getRandomStr());
		TreeMap<String, Object> ma = new TreeMap<String, Object>();
		ma.put("appid", "wefoi");
		ma.put("eei", "20934jf");
		System.out.println(Utils.map2xml(ma));
		// System.out.println(getDataStr("yyyyMMddHH"));

	}
}
