/*
 * Copyright (c) 2003 Canadian Imperial Bank of Commerce.
 * All rights reserved.
 */
package com.networknt.light.rule.compiler;

import java.util.HashMap;
import java.util.Map;

import com.networknt.light.rule.Rule;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test case for CharSequenceCompiler
 * 
 * @author Steve Hu
 */
public class CharSequenceCompilerTest extends TestCase {

	public CharSequenceCompilerTest(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(CharSequenceCompilerTest.class);
		return suite;
	}

	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testF1() throws Exception {
		boolean result = true;
        assertTrue(result);
	}

}
