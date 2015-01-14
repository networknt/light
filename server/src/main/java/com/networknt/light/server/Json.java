package com.networknt.light.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
	public static void main(String[] args) { 
		String injected = "{\"value\": \"value1\", \"label\": \"label1\"},\n" +
				"          {\"value\": \"value2\", \"label\": \"label2\"},\n" +
				"          {\"value\": \"value3\", \"label\": \"label3\"}\n";
		try {
            InputStream is = Json.class.getResourceAsStream("/form/com.networknt.light.demo.uiselect_d.json");
            String formStr = convertStreamToString(is);
			//System.out.println("is = " + formStr);

			Pattern pattern = Pattern.compile("\\[\"@\",([^]]+)\\]");

			Matcher m = pattern.matcher(formStr);
			StringBuffer sb = new StringBuffer(formStr.length());
			while (m.find()) {
				String text = m.group(1);
				System.out.println("text = " + text);
				// ... possibly process 'text' ...
				m.appendReplacement(sb, Matcher.quoteReplacement(injected));
			}
			m.appendTail(sb);
			System.out.println("form = " + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
