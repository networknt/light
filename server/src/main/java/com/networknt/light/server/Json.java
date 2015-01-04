package com.networknt.light.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
	public static void main(String[] args) { 
		ObjectMapper mapper = new ObjectMapper();
	    Map<String, String> parsedMap = null;
		try {
            InputStream is = Json.class.getResourceAsStream("/form/com.networknt.light.class.feed.json");
            System.out.println("is = " + is);
            parsedMap = mapper.readValue(is,new TypeReference<HashMap<String, Object>>(){});
            System.out.println("parsedMap = " + parsedMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
