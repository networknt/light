/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.server;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Json {
	public static void main(String[] args) { 
		String injected = "[{\"value\": \"value1\", \"label\": \"label1\"},\n" +
				"          {\"value\": \"value2\", \"label\": \"label2\"},\n" +
				"          {\"value\": \"value3\", \"label\": \"label3\"}]\n";
		String injectedEmpty = "[]";
		try {
            InputStream is = Json.class.getResourceAsStream("/form/com.networknt.light.access.add_d.json");
            String formStr = convertStreamToString(is);
			//System.out.println("is = " + formStr);

			Pattern pattern = Pattern.compile("\\[\\{\"label\":\"dynamic\",([^]]+)\\}\\]");

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
