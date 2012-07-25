package edu.ucd.speech;

import static org.junit.Assert.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class MainTest {

	@Test
	public void testStringUtils(){
		String[] res = ProcessFile.splitIdentifier("_hello__word_IgnoreResult");
		for(String s: res)
			System.out.println(s);
		
		res = ProcessFile.splitIdentifier("_arrow_button__down");
		for(String s: res)
			System.out.println(s);
	}

}
