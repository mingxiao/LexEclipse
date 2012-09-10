package edu.ucd.speech;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.cmu.sphinx.linguist.dictionary.Dictionary;
import edu.cmu.sphinx.linguist.language.ngram.SimpleNGramModel;

public class EntropyTest {
	
	Dictionary dict;
	SimpleNGramModel model;
	
	@Before
	public void setUp() throws Exception {
//		dict = Entropy.getDictionary();
//		dict.allocate();
//		model = Entropy.getNGramModel(dict);
//		model.allocate();
	}

	@Test
	public void getBigramEntropy(){
		String unigramFileName = "train-0-split3-NUM-unigrams";
		String bigramFileName = "train-0-split3-NUM-bigrams";
		String tokenFileName = "test-0-bigram-identifiers";
		String outFileName = tokenFileName+"-entropy";
		
		File unigramFile = new File(unigramFileName);
		File bigramFile = new File(bigramFileName);
		File tokenFile = new File(tokenFileName);
		File outFile = new File(outFileName);
		
		assert unigramFile.exists();
		assert bigramFile.exists();
		assert tokenFile.exists();
		
		try {
			Entropy.bigramEntropy(unigramFile, bigramFile, tokenFile, outFile);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void unigramToBigram(){
//		String unigramFileName = "test-0-unique-identifiers";
//		String bigramFileName = "test-0-bigram-identifiers";
//		
//		File unigramFile = new File(unigramFileName);
//		assert unigramFile.exists();
//		
//		File bigramFile = new File(bigramFileName);
//		try {
//			Entropy.unigramToBigram(unigramFile, bigramFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	@Test
//	public void getUnigramEntropy(){
//		File inFile = new File("train-0-nosplit-unigrams");
//		File filter = new File("test-0-unique-identifiers");
//		File outFile = new File("test-0-unique-identifiers-unigram-entropy");
//		assert inFile.exists();
//		assert filter.exists();
//		
//		try {
//			Entropy.unigramEntropy(inFile, filter, outFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	@Test
//	public void test() {
//		Word w = new Word("an",null,false);
//		Word[] wl = {w};
//		WordSequence ws = new WordSequence(wl);
//		float f = model.getProbability(ws);
//		System.out.println(f);
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("tmp-identifers"));
//			String line = br.readLine();
//			assertEquals("an",line);
//			Word w = new Word(line,null,false);
//			Word[] wl = {w};
//			WordSequence ws = new WordSequence(wl);
//			float f = model.getProbability(ws);
//			System.out.println(f);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
