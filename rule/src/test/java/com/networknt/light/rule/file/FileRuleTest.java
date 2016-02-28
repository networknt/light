package com.networknt.light.rule.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by steve on 27/02/16.
 */
public class FileRuleTest  extends TestCase {
    ObjectMapper mapper = new ObjectMapper();


    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String addForm = "{\"readOnly\":false,\"category\":\"form\",\"name\":\"addForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\",\"schema\":{\"type\":\"object\",\"title\":\"Comment\",\"properties\":{\"name\":{\"title\":\"Name\",\"type\":\"string\"},\"email\":{\"title\":\"Email\",\"type\":\"string\",\"pattern\":\"^\\\\S+@\\\\S+$\",\"description\":\"Email will be used for evil.\"},\"comment\":{\"title\":\"Comment\",\"type\":\"string\",\"maxLength\":20,\"validationMessage\":\"Don't be greedy!\"}},\"required\":[\"name\",\"email\",\"comment\"]},\"form\":[\"name\",\"email\",{\"key\":\"comment\",\"type\":\"textarea\"},{\"type\":\"submit\",\"style\":\"btn-info\",\"title\":\"OK\"}]}}";
    String getForm = "{\"readOnly\":true,\"category\":\"form\",\"name\":\"getForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\"}}";
    String updForm = "{\"readOnly\":false,\"category\":\"form\",\"name\":\"addForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\",\"schema\":{\"type\":\"object\",\"title\":\"Test\",\"properties\":{\"name\":{\"title\":\"Name\",\"type\":\"string\"},\"email\":{\"title\":\"Email Address\",\"type\":\"string\",\"pattern\":\"^\\\\S+@\\\\S+$\",\"description\":\"Email will be used for evil.\"},\"comment\":{\"title\":\"Comment\",\"type\":\"string\",\"maxLength\":20,\"validationMessage\":\"Don't be greedy!\"}},\"required\":[\"name\",\"email\",\"comment\"]},\"form\":[\"name\",\"email\",{\"key\":\"comment\",\"type\":\"textarea\"},{\"type\":\"submit\",\"style\":\"btn-info\",\"title\":\"OK\"}]}}";
    String getAllForm = "{\"readOnly\": true, \"category\": \"form\", \"name\": \"getAllForm\"}";
    String delForm = "{\"readOnly\":false,\"category\":\"form\",\"name\":\"delForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\"}}";

    String getDynaForm = "{\"readOnly\":true,\"category\":\"form\",\"name\":\"getForm\",\"data\":{\"host\":\"example\",\"id\":\"com.networknt.light.demo.uiselect_d\"}}";

    String enrichDynamicForm = "{\"id\":\"com.networknt.light.access.add_d\",\"version\":1,\"action\":[{\"category\":\"access\",\"name\":\"addAccess\",\"readOnly\":false,\"title\":\"Submit\",\"success\":\"/page/com-networknt-light-v-access-admin-home\"}],\"schema\":{\"type\":\"object\",\"title\":\"Add Access Control\",\"required\":[\"ruleClass\",\"accessLevel\"],\"properties\":{\"ruleClass\":{\"title\":\"Rule Class\",\"type\":\"string\",\"format\":\"uiselect\",\"items\":[{\"label\":\"dynamic\",\"value\":{\"category\":\"rule\",\"name\":\"getRuleDropdown\"}}]},\"accessLevel\":{\"title\":\"Access Level\",\"type\":\"string\",\"format\":\"uiselect\",\"items\":[{\"value\":\"C\",\"label\":\"Client Based\"},{\"value\":\"R\",\"label\":\"Role Based\"},{\"value\":\"U\",\"label\":\"User Based\"},{\"value\":\"CR\",\"label\":\"Client and Role Based\"},{\"value\":\"CU\",\"label\":\"Client and User Based\"},{\"value\":\"RU\",\"label\":\"Role and User Based\"},{\"value\":\"CRU\",\"label\":\"Client, Role and User Based\"}]},\"clients\":{\"title\":\"Clients\",\"type\":\"array\",\"format\":\"uiselect\",\"items\":[{\"label\":\"dynamic\",\"value\":{\"category\":\"client\",\"name\":\"getClientDropdown\"}}]},\"roles\":{\"title\":\"Roles\",\"type\":\"array\",\"format\":\"uiselect\",\"items\":[{\"value\":{\"category\":\"role\",\"name\":\"getRoleDropdown\"},\"label\":\"dynamic\"}]},\"users\":{\"title\":\"Users [Separate by comma if multiple]\",\"type\":\"string\"}}},\"form\":[\"ruleClass\",\"accessLevel\",\"clients\",\"roles\",{\"key\":\"users\",\"type\":\"textarea\"}]}";

    public FileRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FileRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVoid() throws Exception {
        return;
    }

    public void testGetRelPath() throws Exception {
        GetFileRule rule = new GetFileRule();
        String root = "/home/steve/networknt/light/demo/build";
        String absPath = "/home/steve/networknt/light/demo/build/index.html";
        String relPath = rule.getRelPath(root, absPath);
        Assert.assertEquals("index.html", relPath);

        absPath = "/home/steve/networknt/light/demo/build/images/1.png";
        relPath = rule.getRelPath(root, absPath);
        Assert.assertEquals("images/1.png", relPath);
    }

}
