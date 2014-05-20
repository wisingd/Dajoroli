package com.example.splitit.test;

import junit.framework.TestCase;
import com.example.splitit.Operations;
public class testOperations extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testDebt10() {
		assertEquals(10, Operations.myDebt(5, 5));
	}
	
	public void testDebt50() {
		assertEquals(50, Operations.myDebt(-5, 55));
	}
	
	public void testDebt100() {
		assertEquals(100, Operations.myDebt(5, 95));
	}

}
