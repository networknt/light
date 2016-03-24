package com.networknt.light.server;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.MapFunction;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Steve Hu on 3/24/2016.
 */
public class JsonPathTest {
    @Test
    public void testJsonPath() {
        String json = "[\n" +
                "  {\n" +
                "    \"@rid\": \"#36:1\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"commentId\": \"BauZB8vOQT2yUUTN618kWw\",\n" +
                "    \"createDate\": \"2016-03-23T12:41:47.532\",\n" +
                "    \"comment\": \"This is the first comment\",\n" +
                "    \"in_Create\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#15:0\",\n" +
                "        \"userId\": \"stevehu\",\n" +
                "        \"email\": \"stevehu@gmail.com\",\n" +
                "        \"roles\": [\n" +
                "          \"owner\",\n" +
                "          \"user\"\n" +
                "        ],\n" +
                "        \"credential\": \"#16:0\",\n" +
                "        \"createDate\": \"2016-03-14T14:13:17.821\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"out_HasComment\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#36:6\",\n" +
                "        \"host\": \"www.networknt.com\",\n" +
                "        \"commentId\": \"99tFpCBTSpuuw45W4BZU1Q\",\n" +
                "        \"createDate\": \"2016-03-23T17:23:04.185\",\n" +
                "        \"comment\": \"this is the first reply for the first comment.\",\n" +
                "        \"in_Create\": [\n" +
                "          \"#15:0\"\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:2\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"commentId\": \"EYDef3GYTBGexpHPtF-YXg\",\n" +
                "    \"createDate\": \"2016-03-23T12:43:39.239\",\n" +
                "    \"comment\": \"This is the second comment.\",\n" +
                "    \"in_Create\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#15:0\",\n" +
                "        \"userId\": \"stevehu\",\n" +
                "        \"email\": \"stevehu@gmail.com\",\n" +
                "        \"roles\": [\n" +
                "          \"owner\",\n" +
                "          \"user\"\n" +
                "        ],\n" +
                "        \"credential\": \"#16:0\",\n" +
                "        \"createDate\": \"2016-03-14T14:13:17.821\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:3\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"commentId\": \"DARXUkVURsiDKgzOpWuMxQ\",\n" +
                "    \"createDate\": \"2016-03-23T12:46:35.504\",\n" +
                "    \"comment\": \"Hello this is the third comment that need to test\",\n" +
                "    \"in_Create\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#15:0\",\n" +
                "        \"userId\": \"stevehu\",\n" +
                "        \"email\": \"stevehu@gmail.com\",\n" +
                "        \"roles\": [\n" +
                "          \"owner\",\n" +
                "          \"user\"\n" +
                "        ],\n" +
                "        \"credential\": \"#16:0\",\n" +
                "        \"createDate\": \"2016-03-14T14:13:17.821\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:4\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"commentId\": \"GuUirImmTyOAI1gf7GtR6w\",\n" +
                "    \"createDate\": \"2016-03-23T13:13:43.052\",\n" +
                "    \"comment\": \"Hello this is the first comment that using tabl\",\n" +
                "    \"in_Create\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#15:0\",\n" +
                "        \"userId\": \"stevehu\",\n" +
                "        \"email\": \"stevehu@gmail.com\",\n" +
                "        \"roles\": [\n" +
                "          \"owner\",\n" +
                "          \"user\"\n" +
                "        ],\n" +
                "        \"credential\": \"#16:0\",\n" +
                "        \"createDate\": \"2016-03-14T14:13:17.821\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"out_HasComment\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#36:8\",\n" +
                "        \"host\": \"www.networknt.com\",\n" +
                "        \"commentId\": \"aVbmzLgiScybaQFOdWDFYw\",\n" +
                "        \"createDate\": \"2016-03-24T13:26:55.474\",\n" +
                "        \"comment\": \"reply of 4\",\n" +
                "        \"in_Create\": [\n" +
                "          {\n" +
                "            \"@rid\": \"#15:1\",\n" +
                "            \"userId\": \"test\",\n" +
                "            \"host\": \"example\",\n" +
                "            \"email\": \"test@example.com\",\n" +
                "            \"roles\": [\n" +
                "              \"user\"\n" +
                "            ],\n" +
                "            \"credential\": {\n" +
                "              \"@rid\": \"#16:1\",\n" +
                "              \"password\": \"1000:abc\",\n" +
                "              \"clientRefreshTokens\": {\n" +
                "                \"networknt.com@Browser\": [\n" +
                "                  \"ea9d0c8a93d9729df690327a07d63f2\",\n" +
                "                  \"572048c9e70eb74b6b3345757b5c3ba7\"\n" +
                "                ]\n" +
                "              }\n" +
                "            },\n" +
                "            \"createDate\": \"2016-03-14T14:13:17.839\",\n" +
                "            \"gravatar\": \"55502f40dc8b7c769880b10874abc9d0\",\n" +
                "            \"out_Create\": [\n" +
                "              \"#36:8\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:5\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"commentId\": \"FEZg0rXtSHiiIMgx9WanxQ\",\n" +
                "    \"createDate\": \"2016-03-23T13:21:12.187\",\n" +
                "    \"comment\": \"Hello this just to test it if it works.\",\n" +
                "    \"in_Create\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#15:0\",\n" +
                "        \"userId\": \"stevehu\",\n" +
                "        \"email\": \"stevehu@gmail.com\",\n" +
                "        \"roles\": [\n" +
                "          \"owner\",\n" +
                "          \"user\"\n" +
                "        ],\n" +
                "        \"credential\": \"#16:0\",\n" +
                "        \"createDate\": \"2016-03-14T14:13:17.821\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"out_HasComment\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#36:7\",\n" +
                "        \"host\": \"www.networknt.com\",\n" +
                "        \"commentId\": \"niQuRCQVSK2wavTb_OMsLg\",\n" +
                "        \"createDate\": \"2016-03-24T13:25:01.908\",\n" +
                "        \"comment\": \"reply\",\n" +
                "        \"in_Create\": [\n" +
                "          \"#15:0\"\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";

        LightServer.configJsonPath();
        DocumentContext dc = JsonPath.parse(json);
        // first need to strip down the un-used properties for in_Create.
        MapFunction mapFunction = new StripInCreateMapFunction();

        List list = dc.map("$..in_Create[0]", mapFunction).read("$..in_Create[0]");
        System.out.println("list = " + list);

    }


    private class StripInCreateMapFunction implements MapFunction {
        Map<String, Object> userMap = new HashMap<String, Object>();
        @Override
        public Object map(Object currentValue, Configuration configuration) {
            if(currentValue instanceof Map) {
                ((Map) currentValue).remove("roles");
                ((Map) currentValue).remove("credential");
                ((Map) currentValue).remove("createDate");
                ((Map) currentValue).remove("host");
                ((Map) currentValue).remove("out_Create");
                String rid = (String)((Map) currentValue).get("@rid");
                if(userMap.get(rid) == null) {
                    userMap.put(rid, currentValue);
                }
            } else if(currentValue instanceof String) {
                currentValue = userMap.get((String)currentValue);
            }
            return currentValue;
        }
    }
}
