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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.*;

/**
 * Created by steve on 23/09/14.
 */
public class MenuRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String getMenuInjector = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getMenu\", \"data\": {\"host\":\"injector\"}}";
    String getMenuEdibleForestGarden = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getMenu\", \"data\": {\"host\":\"www.edibleforestgarden.ca\"}}";
    String getAllMenu = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getAllMenu\"}";

    String addMenu = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenu\",\"data\":{\"host\":\"www.example.com\"}}";
    String addMenuItem1 = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"id\":\"MenuItem1\",\"label\":\"MenuItem1\",\"host\":\"www.example.com\",\"path\":\"/menuItem1\",\"ctrl\":\"MenuItem1Ctrl\",\"left\":true,\"roles\":\"user\"}}";
    String addMenuItem2 = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"id\":\"MenuItem2\",\"label\":\"MenuItem2\",\"host\":\"www.example.com\",\"path\":\"/menuItem2\",\"ctrl\":\"MenuItem2Ctrl\",\"left\":false,\"roles\":\"user\"}}";
    String addMenuItem3 = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"id\":\"MenuItem3\",\"label\":\"MenuItem3\",\"host\":\"www.example.com\",\"path\":\"/menuItem3\",\"ctrl\":\"MenuItem3Ctrl\",\"left\":false,\"roles\":\"user\"}}";
    String addMenuItemCommon = "{\"readOnly\":false,\"category\":\"menu\",\"name\":\"addMenuItem\",\"data\":{\"id\":\"MenuItemCommon\",\"label\":\"MenuItemCommon\",\"path\":\"/menuItemCommon\",\"ctrl\":\"MenuItemCommonCtrl\",\"left\":false,\"roles\":\"user\"}}";

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

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        try {
            JsonToken ownerToken = null;
            JsonToken userToken = null;
            Map<String, Object> menuAdminPayload = null;
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

            // get menu for injector
            {
                jsonMap = mapper.readValue(getMenuInjector,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                GetMenuRule rule = new GetMenuRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                //System.out.println("result = " + result);
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
                //System.out.println("result = " + result);
            }

            // get menus by owner
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

            // get menus by test.
            // for unit test, the rule is called without access control.
            {
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());

                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
            }

            // get menus by menuAdmin
            {
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                menuAdminPayload = new HashMap<String, Object>();
                Map<String, Object> menuAdminUser = new LinkedHashMap<String, Object>((Map)userToken.getPayload().get("user"));
                List roles = new ArrayList();;
                roles.add("menuAdmin");
                menuAdminUser.put("roles", roles);
                menuAdminPayload.put("user", menuAdminUser);
                jsonMap.put("payload", menuAdminPayload);

                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                //System.out.println("result = " + result);

            }
            // del menu www.example.com if it is there, this is to prepare test re-run if failed.
            {
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", menuAdminPayload);
                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    // there should be only one menu in the list if there are any
                    Map<String, Object> data = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    List menus = (List)data.get("menus");
                    if(menus != null) {
                        assertEquals(1, menus.size());
                        Map<String, Object> menu = (Map<String, Object>)menus.get(0);
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
                    // delete menuItems here. 1,2,3 won't be deleted along with menu if they are not linked together.
                    String json = rule.getMenuItem("MenuItem1");
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
                    json = rule.getMenuItem("MenuItem2");
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
                    json = rule.getMenuItem("MenuItem3");
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

                    json = rule.getMenuItem("MenuItemCommon");
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

            // add menu with www.example.com as host by test and it failed
            // and then add the menu with menuAdmin.
            //
            // add menuItem1, 2, 3 and common with menuAdmin and common failed
            // then add common with owner.

            {
                jsonMap = mapper.readValue(addMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                AddMenuRule addMenuRule = new AddMenuRule();
                ruleResult = addMenuRule.execute(jsonMap);
                assertFalse(ruleResult);

                jsonMap.put("payload", ownerToken.getPayload());
                addMenuRule = new AddMenuRule();
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
                jsonMap.put("payload", menuAdminPayload);
                GetAllMenuRule rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                String result = (String) jsonMap.get("result");
                // there should be only one menu in the list.
                Map<String, Object> data = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List menus = (List)data.get("menus");
                assertEquals(1, menus.size());
                Map<String, Object> menu = (Map<String, Object>)menus.get(0);

                jsonMap = mapper.readValue(addMenuItem1,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", menuAdminPayload);
                AddMenuItemRule addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddMenuItemEvRule addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                // get menuItem for future update
                result = addMenuItemEvRule.getMenuItem("MenuItem1");
                Map<String, Object> menuItem1 = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                jsonMap = mapper.readValue(addMenuItem2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", menuAdminPayload);
                addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = addMenuItemEvRule.getMenuItem("MenuItem2");
                Map<String, Object> menuItem2 = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                jsonMap = mapper.readValue(addMenuItem3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", menuAdminPayload);
                addMenuItemRule = new AddMenuItemRule();
                ruleResult = addMenuItemRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                addMenuItemEvRule = new AddMenuItemEvRule();
                ruleResult = addMenuItemEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = addMenuItemEvRule.getMenuItem("MenuItem3");
                Map<String, Object> menuItem3 = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

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
                result = addMenuItemEvRule.getMenuItem("MenuItemCommon");
                Map<String, Object> menuItemCommon = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                // update menu with above for menuItems
                List menuItems = new ArrayList();
                menuItems.add(menuItem1.get("@rid"));
                menuItems.add(menuItem2.get("@rid"));
                menu.put("menuItems", menuItems);

                jsonMap = mapper.readValue(updMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("data", menu);
                jsonMap.put("payload", menuAdminPayload);
                UpdMenuRule updMenuRule = new UpdMenuRule();
                ruleResult = updMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdMenuEvRule updMenuEvRule = new UpdMenuEvRule();
                ruleResult = updMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = updMenuEvRule.getMenu("www.example.com");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List list = (List)jsonMap.get("menuItems");
                // there should be two menuItems
                assertEquals(2, list.size());

                // get the menu for future update with menuItems.
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", menuAdminPayload);
                rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                result = (String) jsonMap.get("result");
                // there should be only one menu in the list.
                data = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                menus = (List)data.get("menus");
                assertEquals(1, menus.size());
                menu = (Map<String, Object>)menus.get(0);


                menuItems.add(menuItem3.get("@rid"));
                menuItems.add(menuItemCommon.get("@rid"));
                menu.put("menuItems", menuItems);


                jsonMap = mapper.readValue(updMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("data", menu);
                jsonMap.put("payload", menuAdminPayload);
                updMenuRule = new UpdMenuRule();
                ruleResult = updMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                updMenuEvRule = new UpdMenuEvRule();
                ruleResult = updMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = updMenuEvRule.getMenu("www.example.com");

                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                list = (List)jsonMap.get("menuItems");
                // there should be four menuItems
                assertEquals(4, list.size());

                menuItems.clear();

                // get the menu for future update with menuItems.
                jsonMap = mapper.readValue(getAllMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", menuAdminPayload);
                rule = new GetAllMenuRule();
                ruleResult = rule.execute(jsonMap);
                result = (String) jsonMap.get("result");
                // there should be only one menu in the list.
                data = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                menus = (List)data.get("menus");
                assertEquals(1, menus.size());
                menu = (Map<String, Object>)menus.get(0);


                jsonMap = mapper.readValue(updMenu,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("data", menu);
                jsonMap.put("payload", menuAdminPayload);
                updMenuRule = new UpdMenuRule();
                ruleResult = updMenuRule.execute(jsonMap);
                assertTrue(ruleResult);
                eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                updMenuEvRule = new UpdMenuEvRule();
                ruleResult = updMenuEvRule.execute(eventMap);
                assertTrue(ruleResult);
                result = updMenuEvRule.getMenu("www.example.com");

                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                list = (List)jsonMap.get("menuItems");
                // there should be four menuItems
                assertEquals(4, list.size());


                // update menuItem1 by menuAdmin
                {
                    menuItem1.put("path", "/newPath");
                    List l = new ArrayList();
                    l.add(menuItem2.get("@rid"));
                    l.add(menuItem3.get("@rid"));
                    menuItem1.put("menuItems",l);
                    jsonMap = mapper.readValue(updMenuItem,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menuItem1);
                    jsonMap.put("payload", menuAdminPayload);
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
                    menuItemCommon.put("path", "/newPathForCommon");
                    jsonMap = mapper.readValue(updMenuItem,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menuItemCommon);
                    jsonMap.put("payload", menuAdminPayload);
                    UpdMenuItemRule updMenuItemRule = new UpdMenuItemRule();
                    ruleResult = updMenuItemRule.execute(jsonMap);
                    assertFalse(ruleResult);

                    jsonMap.put("data", menuItem1);
                    jsonMap.put("payload", ownerToken.getPayload());
                    updMenuItemRule = new UpdMenuItemRule();
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
                    jsonMap.put("payload", menuAdminPayload);
                    rule = new GetAllMenuRule();
                    ruleResult = rule.execute(jsonMap);
                    result = (String) jsonMap.get("result");
                    // there should be only one menu in the list.
                    data = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    menus = (List)data.get("menus");
                    assertEquals(1, menus.size());
                    menu = (Map<String, Object>)menus.get(0);


                    menuItems.add(menuItem1.get("@rid"));
                    menuItems.add(menuItemCommon.get("@rid"));
                    menu.put("menuItems", menuItems);

                    jsonMap = mapper.readValue(updMenu,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menu);
                    jsonMap.put("payload", menuAdminPayload);
                    updMenuRule = new UpdMenuRule();
                    ruleResult = updMenuRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    updMenuEvRule = new UpdMenuEvRule();
                    ruleResult = updMenuEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                    result = updMenuEvRule.getMenu("www.example.com");
                    System.out.println("result = " + result);
                    jsonMap = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    list = (List)jsonMap.get("menuItems");
                    assertEquals(2, list.size());
                    Map<String, Object> item1 = (Map<String, Object>)list.get(0);
                    list = (List)item1.get("menuItems");
                    assertEquals(2, list.size());
                }

                // delete menuItemCommon and menuItem2
                // first try failed as there are reference to them.
                {
                    String json = rule.getMenuItem("MenuItemCommon");
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

                    json = rule.getMenuItem("MenuItem2");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", menuAdminPayload);
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
                    jsonMap.put("payload", menuAdminPayload);
                    rule = new GetAllMenuRule();
                    ruleResult = rule.execute(jsonMap);
                    result = (String) jsonMap.get("result");
                    // there should be only one menu in the list.
                    data = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    menus = (List)data.get("menus");
                    assertEquals(1, menus.size());
                    menu = (Map<String, Object>)menus.get(0);

                    menuItems.remove(menuItem1.get("@rid"));
                    menu.put("menuItems", menuItems);

                    jsonMap = mapper.readValue(updMenu,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("data", menu);
                    jsonMap.put("payload", menuAdminPayload);
                    updMenuRule = new UpdMenuRule();
                    ruleResult = updMenuRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    updMenuEvRule = new UpdMenuEvRule();
                    ruleResult = updMenuEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                    result = updMenuEvRule.getMenu("www.example.com");
                    jsonMap = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    list = (List)jsonMap.get("menuItems");
                    // there should be four menuItems
                    assertEquals(1, list.size());

                    json = rule.getMenuItem("MenuItem1");
                    if(json != null) {
                        Map<String, Object> menuItem = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap = mapper.readValue(delMenuItem,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        jsonMap.put("payload", menuAdminPayload);
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
}
