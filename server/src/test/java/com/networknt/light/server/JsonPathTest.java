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
                "    \"@rid\": \"#36:8\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"dSuRPJT9Sxq2JZs4zmOJQw\",\n" +
                "    \"content\": \"This is the comment 1\",\n" +
                "    \"createDate\": \"2016-03-25T18:26:56.583\",\n" +
                "    \"in_UpVote\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#15:0\",\n" +
                "        \"userId\": \"stevehu\",\n" +
                "        \"email\": \"stevehu@gmail.com\",\n" +
                "        \"roles\": [\n" +
                "          \"owner\",\n" +
                "          \"user\"\n" +
                "        ],\n" +
                "        \"credential\": \"#16:0\",\n" +
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ],\n" +
                "    \"in_Create\": [\n" +
                "      \"#15:0\"\n" +
                "    ],\n" +
                "    \"out_HasComment\": [\n" +
                "      {\n" +
                "        \"@rid\": \"#36:19\",\n" +
                "        \"host\": \"www.networknt.com\",\n" +
                "        \"rank\": 0,\n" +
                "        \"commentId\": \"CHQSq7PrSMiZiOK-4Pas-Q\",\n" +
                "        \"content\": \"Reply to the comment 1\",\n" +
                "        \"createDate\": \"2016-03-25T18:33:19.492\",\n" +
                "        \"in_Create\": [\n" +
                "          \"#15:0\"\n" +
                "        ],\n" +
                "        \"out_HasComment\": [\n" +
                "          {\n" +
                "            \"@rid\": \"#36:20\",\n" +
                "            \"host\": \"www.networknt.com\",\n" +
                "            \"rank\": 0,\n" +
                "            \"commentId\": \"BmnvEiXzTDOtCm2jVYj_4w\",\n" +
                "            \"content\": \"Reply to the reply to the comment 1\",\n" +
                "            \"createDate\": \"2016-03-25T18:33:45.650\",\n" +
                "            \"in_Create\": [\n" +
                "              \"#15:0\"\n" +
                "            ],\n" +
                "            \"out_HasComment\": [\n" +
                "              {\n" +
                "                \"@rid\": \"#36:21\",\n" +
                "                \"host\": \"www.networknt.com\",\n" +
                "                \"rank\": 0,\n" +
                "                \"commentId\": \"45ephwwwQMCQnzxyj1emMA\",\n" +
                "                \"content\": \"Reply to the reply to the reply to the comment 1\",\n" +
                "                \"createDate\": \"2016-03-25T18:34:17.610\",\n" +
                "                \"in_Create\": [\n" +
                "                  \"#15:0\"\n" +
                "                ],\n" +
                "                \"out_HasComment\": [\n" +
                "                  {\n" +
                "                    \"@rid\": \"#36:22\",\n" +
                "                    \"host\": \"www.networknt.com\",\n" +
                "                    \"rank\": 0,\n" +
                "                    \"commentId\": \"2MZEWwqbRaiQuvGOxf-sag\",\n" +
                "                    \"content\": \"One thing worries me about your Angular code samples: you have your ng-repeat/ngFor iterating over a data structure, but the ng-class=\\\\\\\"getClassName(query)\\\\\\\" will need to be checked on every digest cycle. If instead the code that created the topFiveQueries object set the desired class as an attribute on each query object you can avoid constantly re-calculating the value. Of course that may be a drop in the ocean as far as performance is concerned.\",\n" +
                "                    \"createDate\": \"2016-03-26T02:55:39.156\",\n" +
                "                    \"in_Create\": [\n" +
                "                      \"#15:0\"\n" +
                "                    ],\n" +
                "                    \"out_HasComment\": [\n" +
                "                      {\n" +
                "                        \"@rid\": \"#36:23\",\n" +
                "                        \"host\": \"www.networknt.com\",\n" +
                "                        \"rank\": 0,\n" +
                "                        \"commentId\": \"Cq_5ZS68Rsye6guO6htz5w\",\n" +
                "                        \"content\": \"Hello world\",\n" +
                "                        \"createDate\": \"2016-03-26T02:56:06.428\",\n" +
                "                        \"in_Create\": [\n" +
                "                          \"#15:0\"\n" +
                "                        ]\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"in_ReportSpam\": [\n" +
                "      \"#15:0\"\n" +
                "    ],\n" +
                "    \"in_DownVote\": []\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:9\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"Puut89ZTRii4TFfXcVqxqA\",\n" +
                "    \"content\": \"This is the comment 2\",\n" +
                "    \"createDate\": \"2016-03-25T18:31:58.322\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:10\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"oa0SmklARH6cPQnRIQCIFA\",\n" +
                "    \"content\": \"This is the comment 3\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:03.495\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:11\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"SafmN_dhSGCyzFN2j3MLDA\",\n" +
                "    \"content\": \"This is the comment 4\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:07.204\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:12\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"61gsOlsjTA2od3u_-q8EEg\",\n" +
                "    \"content\": \"This is the comment 5\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:11.890\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:13\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"WRV6pr_ORnWIBQT2rgj6qw\",\n" +
                "    \"content\": \"This is the comment 6\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:16.475\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:14\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"76eYIYUfSE2UkpyYObHEuw\",\n" +
                "    \"content\": \"This is the comment 7\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:20.620\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:15\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"kJEbmj4OSkKQM9hTrtHJ_Q\",\n" +
                "    \"content\": \"This is the comment 8\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:25.216\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:16\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"9UZJifQ6QgK52W7nopQtLQ\",\n" +
                "    \"content\": \"This is the comment 9\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:29.621\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:17\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"FPESqMiMTsuS-HhEx28jlw\",\n" +
                "    \"content\": \"This is the comment 10\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:33.890\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"@rid\": \"#36:18\",\n" +
                "    \"host\": \"www.networknt.com\",\n" +
                "    \"rank\": 0,\n" +
                "    \"commentId\": \"IWOTmQuIQDSF5O5BCwNY3w\",\n" +
                "    \"content\": \"This is the comment 11\",\n" +
                "    \"createDate\": \"2016-03-25T18:32:39.049\",\n" +
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
                "        \"createDate\": \"2016-03-20T11:52:18.806\",\n" +
                "        \"gravatar\": \"417bed6d9644f12d8bc709059c225c27\",\n" +
                "        \"out_\": [],\n" +
                "        \"out_ReportSpam\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_UpVote\": [\n" +
                "          \"#36:8\"\n" +
                "        ],\n" +
                "        \"out_DownVote\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";

        LightServer.configJsonPath();
        DocumentContext dc = JsonPath.parse(json);
        // first need to strip down the un-used properties for in_Create.
        MapFunction mapFunction = new StripInCreateMapFunction();

        List list = dc.map("$..in_Create[0]", mapFunction).read("$..in_Create[0]");

        MapFunction upVoteFunction = new StripInUpVoteMapFunction();
        list = dc.map("$..in_UpVote[*]", upVoteFunction).read("$..in_UpVote[*]");

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

    private class StripInReportSpamMapFunction implements MapFunction {
        @Override
        public Object map(Object currentValue, Configuration configuration) {
            String value = null;
            if(currentValue instanceof Map) {
                value = (String) ((Map) currentValue).get("@rid");
            }
            return value;
        }
    }

    private class StripInUpVoteMapFunction implements MapFunction {
        @Override
        public Object map(Object currentValue, Configuration configuration) {
            String value = null;
            if(currentValue instanceof Map) {
                value = (String) ((Map) currentValue).get("@rid");
            }
            return value;
        }
    }

    private class StripInDownVoteMapFunction implements MapFunction {
        @Override
        public Object map(Object currentValue, Configuration configuration) {
            String value = null;
            if(currentValue instanceof Map) {
                value = (String) ((Map) currentValue).get("@rid");
            }
            return value;
        }
    }

}
