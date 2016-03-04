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

package com.networknt.light.rule.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

/**
 * Created by steve on 23/09/14.
 */
public class MenuRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String getMenuEdibleForestGarden = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getMenu\", \"data\": {\"host\":\"www.edibleforestgarden.ca\"}}";
    String getAllMenu = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getAllMenu\"}";
    String getMenuItemMap = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getMenuItemMap\"}";

    String addMenu = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenu\",\"data\":{\"host\":\"www.example.com\"}}";
    String addMenuItem1 = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"menuItemId\":\"MenuItem1\",\"label\":\"MenuItem1\",\"host\":\"www.example.com\",\"path\":\"/menuItem1\",\"ctrl\":\"MenuItem1Ctrl\",\"left\":true,\"roles\":[\"user\"]}}";
    String addMenuItem2 = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"menuItemId\":\"MenuItem2\",\"label\":\"MenuItem2\",\"host\":\"www.example.com\",\"path\":\"/menuItem2\",\"ctrl\":\"MenuItem2Ctrl\",\"left\":false,\"roles\":[\"user\"]}}";
    String addMenuItem3 = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"menuItemId\":\"MenuItem3\",\"label\":\"MenuItem3\",\"host\":\"www.example.com\",\"path\":\"/menuItem3\",\"ctrl\":\"MenuItem3Ctrl\",\"left\":false,\"roles\":[\"user\"]}}";
    String addMenuItemCommon = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"menuItemId\":\"MenuItemCommon\",\"label\":\"MenuItemCommon\",\"path\":\"/menuItemCommon\",\"ctrl\":\"MenuItemCommonCtrl\",\"left\":false,\"roles\":[\"user\"]}}";

    String updMenu = "{\"readOnly\": false, \"category\": \"menu\", \"name\": \"updMenu\"}";
    String updMenuItem = "{\"readOnly\": false, \"category\": \"menu\", \"name\": \"updMenuItem\"}";
    String delMenu = "{\"readOnly\": false, \"category\": \"menu\", \"name\": \"delMenu\"}";
    String delMenuItem = "{\"readOnly\": false, \"category\": \"menu\", \"name\": \"delMenuItem\"}";

    public MenuRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(MenuRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception { super.setUp(); }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVoid() throws Exception {

    }
    /*
    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            JsonToken ownerToken = null;
            JsonToken userToken = null;
            // signIn owner by userId
            {
                jsonMap = mapper.readValue(signInOwner,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String)jsonMap.get("accessToken");
                ownerToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // signIn test by userId
            {
                jsonMap = mapper.readValue(signInUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String)jsonMap.get("accessToken");
                userToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // get menu for www.edibleforestgarden.ca
            {
                jsonMap = mapper.readValue(getMenuEdibleForestGarden,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                GetMenuRule rule = new GetMenuRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }

            // get all menus by owner
            {
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }

            // get all menuItemMap by owner
            {
                jsonMap = mapper.readValue(getMenuItemMap,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                GetMenuItemMapRule rule = new GetMenuItemMapRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }

            // del menu www.example.com if it is there, this is to prepare test re-run if failed.
            {
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    // there should be only one menu in the list if there are any
                    Map<String, Object> data = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    List<Map> menus = (List<Map>)data.get("menus");
                    if(menus != null) {
                        // find www.example.com menu and delete it.
                        for(Map menu: menus) {
                            String host = (String)menu.get("host");
                            if("www.example.com".equals(host)) {
                                jsonMap = mapper.readValue(delMenu,
                                        new TypeReference<HashMap<String, Object>>() {
                                        });
                                jsonMap.put("payload", ownerToken.getPayload());
                                jsonMap.put("data", menu);
                                DelMenuRule valRule = new DelMenuRule();
                                ruleResult = valRule.execute(jsonMap);
                                assertTrue(ruleResult);
                                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                                DelMenuEvRule delRule = new DelMenuEvRule();
                                ruleResult = delRule.execute(eventMap);
                                assertTrue(ruleResult);
                            }
                        }
                    }
                    // delete menuItems here. 1,2,3 won't be deleted along with menu if they are not linked together.
                    String json = rule.getMenuItem(graph, "MenuItem1");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        if(ruleResult) {
                            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                            DelMenuItemEvRule delMenuItemEvRule = new DelMenuItemEvRule();
                            ruleResult = delMenuItemEvRule.execute(eventMap);
                            assertTrue(ruleResult);
                        }
                    }
                    json = rule.getMenuItem(graph, "MenuItem2");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        if(ruleResult) {
                            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                            DelMenuItemEvRule delMenuItemEvRule = new DelMenuItemEvRule();
                            ruleResult = delMenuItemEvRule.execute(eventMap);
                            assertTrue(ruleResult);
                        }
                    }
                    json = rule.getMenuItem(graph, "MenuItem3");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        if(ruleResult) {
                            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                            DelMenuItemEvRule delMenuItemEvRule = new DelMenuItemEvRule();
                            ruleResult = delMenuItemEvRule.execute(eventMap);
                            assertTrue(ruleResult);
                        }
                    }

                    json = rule.getMenuItem(graph, "MenuItemCommon");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        if(ruleResult) {
                            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                            DelMenuItemEvRule delMenuItemEvRule = new DelMenuItemEvRule();
                            ruleResult = delMenuItemEvRule.execute(eventMap);
                            assertTrue(ruleResult);
                        }
                    }
                }
            }

            // add menu and menuItems
            {
                // add menu www.example.com as host by owner
                jsonMap = mapper.readValue(addMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                AddMenuRule addMenuRule = new AddMenuRule();
                ruleResult = addMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddMenuEvRule addMenuEvRule = new AddMenuEvRule();
                ruleResult = addMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);

                // get the menu for future update with menuItems.
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                String result = (String) jsonMap.get("result");
                Map<String, Object> data = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List<Map> menus = (List<Map>)data.get("menus");
                // menu map for www.example.com
                Map menu = null;
                if(menus != null) {
                    // find www.example.com menu and delete it.
                    for(Map map: menus) {
                        String host = (String)map.get("host");
                        if("www.example.com".equals(host)) {
                            menu = map;
                        }
                    }
                }

                // add menuItem1
                jsonMap = mapper.readValue(addMenuItem1,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                AddMenuItemRule addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddMenuItemEvRule addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                // get menuItem for future update
                result = addMenuItemEvRule.getMenuItem(graph, "MenuItem1");
                Map<String, Object> menuItem1 = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                // add menuItem2
                jsonMap = mapper.readValue(addMenuItem2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = addMenuItemEvRule.getMenuItem(graph, "MenuItem2");
                Map<String, Object> menuItem2 = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                //  add menuItem3
                jsonMap = mapper.readValue(addMenuItem3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = addMenuItemEvRule.getMenuItem(graph, "MenuItem3");
                Map<String, Object> menuItem3 = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                // add menuItemCommon
                jsonMap = mapper.readValue(addMenuItemCommon,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = addMenuItemEvRule.getMenuItem(graph, "MenuItemCommon");
                Map<String, Object> menuItemCommon = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                // update menu with above for menuItems
                List out_Own = new ArrayList();
                out_Own.add(menuItem1.get("@rid"));
                out_Own.add(menuItem2.get("@rid"));
                menu.put("out_Own", out_Own);

                jsonMap = mapper.readValue(updMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("data", menu);
                jsonMap.put("payload", ownerToken.getPayload());
                UpdMenuRule updMenuRule = new UpdMenuRule();
                ruleResult = updMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdMenuEvRule updMenuEvRule = new UpdMenuEvRule();
                ruleResult = updMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = updMenuEvRule.getMenu(graph, "www.example.com");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List list = (List)jsonMap.get("out_Own");
                // there should be two menuItems
                assertEquals(2, list.size());

                // get the menu for future update with menuItems.
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                result = (String) jsonMap.get("result");
                // there should be only one menu in the list.
                data = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                menus = (List)data.get("menus");
                if(menus != null) {
                    // find www.example.com menu and delete it.
                    for(Map map: menus) {
                        String host = (String)map.get("host");
                        if("www.example.com".equals(host)) {
                            menu = map;
                        }
                    }
                }

                out_Own.add(menuItem3.get("@rid"));
                out_Own.add(menuItemCommon.get("@rid"));
                menu.put("out_Own", out_Own);


                jsonMap = mapper.readValue(updMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("data", menu);
                jsonMap.put("payload", ownerToken.getPayload());
                updMenuRule = new UpdMenuRule();
                ruleResult = updMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                updMenuEvRule = new UpdMenuEvRule();
                ruleResult = updMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = updMenuEvRule.getMenu(graph, "www.example.com");

                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                list = (List)jsonMap.get("out_Own");
                // there should be four menuItems
                assertEquals(4, list.size());

                out_Own.clear();

                // get the menu for future update with menuItems.
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                result = (String) jsonMap.get("result");
                // there should be only one menu in the list.
                data = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                menus = (List)data.get("menus");
                if(menus != null) {
                    // find www.example.com menu and delete it.
                    for(Map map: menus) {
                        String host = (String)map.get("host");
                        if("www.example.com".equals(host)) {
                            menu = map;
                        }
                    }
                }

                // Update menu to remove all the out_Own edge.
                jsonMap = mapper.readValue(updMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                menu.put("out_Own", out_Own);
                jsonMap.put("data", menu);
                jsonMap.put("payload", ownerToken.getPayload());
                updMenuRule = new UpdMenuRule();
                ruleResult = updMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                updMenuEvRule = new UpdMenuEvRule();
                ruleResult = updMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = updMenuEvRule.getMenu(graph, "www.example.com");

                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                list = (List)jsonMap.get("out_Own");
                // there should be four menuItems
                assertEquals(0, list.size());


                // update menuItem1
                {
                    result = addMenuItemEvRule.getMenuItem(graph, "MenuItem1");
                    menuItem1 = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });

                    menuItem1.put("path", "/newPath");
                    List l = new ArrayList();
                    l.add(menuItem2.get("@rid"));
                    l.add(menuItem3.get("@rid"));
                    menuItem1.put("out_Own",l);
                    jsonMap = mapper.readValue(updMenuItem,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menuItem1);
                    jsonMap.put("payload", ownerToken.getPayload());
                    UpdMenuItemRule updMenuItemRule = new UpdMenuItemRule();
                    ruleResult = updMenuItemRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    UpdMenuItemEvRule updMenuItemEvRule = new UpdMenuItemEvRule();
                    ruleResult = updMenuItemEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }

                // update menuItemCommon by menuAdmin and it failed
                // update it with owner
                {
                    result = addMenuItemEvRule.getMenuItem(graph, "MenuItemCommon");
                    menuItemCommon = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });

                    menuItemCommon.put("path", "/newPathForCommon");
                    jsonMap = mapper.readValue(updMenuItem,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menuItemCommon);
                    jsonMap.put("payload", ownerToken.getPayload());
                    UpdMenuItemRule updMenuItemRule = new UpdMenuItemRule();
                    ruleResult = updMenuItemRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    UpdMenuItemEvRule updMenuItemEvRule = new UpdMenuItemEvRule();
                    ruleResult = updMenuItemEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
                // update menu to link menuItem1 and menuItemCommon
                // get menu again to check if changes are there.
                {

                    // get the menu for future update with menuItems.
                    jsonMap = mapper.readValue(getAllMenu,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    rule = new GetAllMenuRule();
                    ruleResult = rule.execute(jsonMap);
                    result = (String) jsonMap.get("result");
                    // there should be only one menu in the list.
                    data = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    menus = (List)data.get("menus");
                    if(menus != null) {
                        // find www.example.com menu and delete it.
                        for(Map map: menus) {
                            String host = (String)map.get("host");
                            if("www.example.com".equals(host)) {
                                menu = map;
                            }
                        }
                    }

                    out_Own.add(menuItem1.get("@rid"));
                    out_Own.add(menuItemCommon.get("@rid"));
                    menu.put("out_Own", out_Own);

                    jsonMap = mapper.readValue(updMenu,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menu);
                    jsonMap.put("payload", ownerToken.getPayload());
                    updMenuRule = new UpdMenuRule();
                    ruleResult = updMenuRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    updMenuEvRule = new UpdMenuEvRule();
                    ruleResult = updMenuEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                    result = updMenuEvRule.getMenu(graph, "www.example.com");
                    System.out.println("result = " + result);
                    jsonMap = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    list = (List)jsonMap.get("out_Own");
                    assertEquals(2, list.size());
                    Map<String, Object> item1 = (Map<String, Object>)list.get(0);
                    list = (List)item1.get("out_Own");
                    assertEquals(2, list.size());
                }

                // delete menuItemCommon and menuItem2
                // first try failed as there are reference to them.
                {
                    String json = rule.getMenuItem(graph, "MenuItemCommon");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        assertFalse(ruleResult);
                    }

                    json = rule.getMenuItem(graph, "MenuItem2");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        assertFalse(ruleResult);
                    }

                    // now update menu to remove menuItem1, then remove menuItem1 from db
                    // when remove menuItem1, menuItem2 and menuItem3 should be removed at
                    // the same time as they have the same host.

                    // get the menu for future update with menuItems.
                    jsonMap = mapper.readValue(getAllMenu,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    rule = new GetAllMenuRule();
                    ruleResult = rule.execute(jsonMap);
                    result = (String) jsonMap.get("result");
                    // there should be only one menu in the list.
                    data = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    menus = (List)data.get("menus");
                    if(menus != null) {
                        // find www.example.com menu and delete it.
                        for(Map map: menus) {
                            String host = (String)map.get("host");
                            if("www.example.com".equals(host)) {
                                menu = map;
                            }
                        }
                    }

                    out_Own.remove(menuItem1.get("@rid"));
                    menu.put("out_Own", out_Own);

                    jsonMap = mapper.readValue(updMenu,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menu);
                    jsonMap.put("payload", ownerToken.getPayload());
                    updMenuRule = new UpdMenuRule();
                    ruleResult = updMenuRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    updMenuEvRule = new UpdMenuEvRule();
                    ruleResult = updMenuEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                    result = updMenuEvRule.getMenu(graph, "www.example.com");
                    jsonMap = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    list = (List)jsonMap.get("out_Own");
                    assertEquals(1, list.size());

                    json = rule.getMenuItem(graph, "MenuItem1");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", ownerToken.getPayload());
                        jsonMap.put("data", menuItem);
                        DelMenuItemRule delMenuItemRule = new DelMenuItemRule();
                        ruleResult = delMenuItemRule.execute(jsonMap);
                        assertTrue(ruleResult);
                        eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                        DelMenuItemEvRule delMenuItemEvRule = new DelMenuItemEvRule();
                        ruleResult = delMenuItemEvRule.execute(eventMap);
                        assertTrue(ruleResult);

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
