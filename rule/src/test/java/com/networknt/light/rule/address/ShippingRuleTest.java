package com.networknt.light.rule.address;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by steve on 21/12/15.
 */
public class ShippingRuleTest extends TestCase {
    public ShippingRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ShippingRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVoid() throws Exception {

    }
    /*
    public void testCalculateShipping() throws Exception {
        BigDecimal subTotal = new BigDecimal(10.00);
        BigDecimal shipping = AbstractAddressRule.calculateShipping("ON", subTotal);
        Assert.assertEquals(shipping, new BigDecimal("30.50"));

        subTotal = new BigDecimal(2378343.99);
        shipping = AbstractAddressRule.calculateShipping("ON", subTotal);
        Assert.assertEquals(shipping, new BigDecimal("118947.20"));
    }

    public void testCalculateTaxON() throws Exception {
        BigDecimal subTotal = new BigDecimal(10.00);
        Map<String, BigDecimal> taxes = AbstractAddressRule.calculateTax("ON", subTotal);
        Assert.assertEquals(new BigDecimal("1.30"), taxes.get("HST"));

        subTotal = new BigDecimal(2378343.99);
        taxes = AbstractAddressRule.calculateTax("QC", subTotal);
        Assert.assertEquals(new BigDecimal("237239.81"), taxes.get("QST"));
    }
    */
}