package edu.ucd.speech;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProcessFileTest {

	@Test
	public void test() {
		String[] foo= {"A","test080"};
		String[] actual = ProcessFile.splitIdentifier("A_test080_", false);
		for (String s: actual)
			System.out.println(s);
		assertArrayEquals(foo,actual);
		
	}

}
