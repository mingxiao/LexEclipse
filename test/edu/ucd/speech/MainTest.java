package edu.ucd.speech;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.CrossValidationPartitioner;
import opennlp.tools.util.eval.CrossValidationPartitioner.TrainingSampleStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class MainTest {

	@Test
	public void testStringUtils(){
		String[] res = ProcessFile.splitIdentifier("_hello__word_IgnoreResult");
		String[] expected = {"hello","word","Ignore","Result"};
		assertArrayEquals(res, expected);
//		for(String s: res)
//			System.out.println(s);
		
		res = ProcessFile.splitIdentifier("_arrow_button__down");
		String[] expected2 = {"arrow","button","down"};
		assertArrayEquals(res, expected2);
//		for(String s: res)
//			System.out.println(s);
	}
	
	@Test
	public void testProcessQuote(){
		String test = "\"1234567890123456789012345678901234567890\"";
		String expected = " QUOTE 1234567890123456789012345678901234567890 QUOTE ";
		assertEquals(ProcessFile.process_quote(test), expected);
	}
	
	
	
	@Test
	public void testProcessLine(){
		String test = "    /* Comment 2 */ { \"1234567890123456789012345678901234567890\", \"1234567890123456789012345678901234567890\" },\n";
		test = "\"1234567890123456789012345678901234567890\"";
		String expected = "QUOTE 1234567890123456789012345678901234567890 QUOTE";
//		String s = ProcessFile.process_line(test);
		String s = ProcessFile.process_quote(test);
		assertEquals(" QUOTE 1234567890123456789012345678901234567890 QUOTE ",s);
		s = ProcessFile.process_snake_case(s);
		assertEquals("QUOTE 1234567890123456789012345678901234567890 QUOTE",s);
		s = ProcessFile.process_camel_case2(s);
		assertEquals("QUOTE  1234567890123456789012345678901234567890  QUOTE",s);
		s = ProcessFile.process_numbers(s);
		assertEquals("QUOTE 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 QUOTE ",s);
//		System.out.println(s);		
	}
	@Test
	public void testSplitIdentifier(){
		String test = "testUnusedCodeBug189394";
		String[] actual = ProcessFile.splitIdentifier(test);
		String[] expected = {"test","Unused","Code","Bug","1 8 9 3 9 4"};
		assertArrayEquals(expected,actual);
	}
	
//	@Test
//	public void testCountIdent(){
//		File f = new File("src/edu/ucd/speech/testInput.java");
////		System.out.println(f.getAbsolutePath());
//		int x;
//		
//		try {
//			FileWriter fw = new FileWriter("unique-nosplit");
//			x = Main.countIdent(f,true, new HashSet<String>(),fw);
////			System.out.println(x);
//			assertEquals(19,x);
//			fw.flush();
//			fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	@Test
//	public void testCountIdentDirectory(){
//		String dir = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90";
////		String dir = "/Users/mingxiao10016/Documents/speech_sphinx/test/small";
//		File d = new File(dir);
//		try {
//			int x = Main.countIdentDirectory(d, true, "eclipse-90-unique-split",true);
//			System.out.println(x);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	
//	@Test
//	public void testCrossVal() throws IOException{
//		String source ="/Users/mingxiao10016/Documents/speech_sphinx/eclipse_java_source";
//		File s = new File(source);
//		String base = "/Users/mingxiao10016/Documents/speech_sphinx/cross-validation";
//		Main.crossValParition(s, base,10);
//	}
	
//	@Test
//	public void testProcessDir(){
//		String source ="/Users/mingxiao10016/Documents/speech_sphinx/test/small";
////		File f = new File(source);
////		f.mkdir();
//		String dest ="/Users/mingxiao10016/Documents/speech_sphinx/test/small-lexed-3";
//		File s = new File(source);
//		File t = new File(dest);
//		
//		try {
//			Main.processDirectory(s, t, true,3);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@Test
	public void testSplit(){
//		String source ="/Users/mingxiao10016/Documents/speech_sphinx/test/10-fold";
//		String dest ="/Users/mingxiao10016/Documents/speech_sphinx/test/split3";
		String source ="/Users/mingxiao10016/Documents/speech_sphinx/cross-validation/partitions";
		String dest ="/Users/mingxiao10016/Documents/speech_sphinx/cross-validation/split3";
		File src = new File(source);
		File dst = new File(dest);
		for(File f: src.listFiles()){
			assert f.isDirectory(); //should be a folder
			File tmpfile = new File(dst,f.getName());
			tmpfile.mkdirs();
			System.out.println(tmpfile.getAbsolutePath());
			try {
				Main.processDirectory(f, tmpfile, true, 3);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
