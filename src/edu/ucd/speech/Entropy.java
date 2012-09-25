package edu.ucd.speech;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import edu.cmu.sphinx.linguist.WordSequence;
import edu.cmu.sphinx.linguist.acoustic.UnitManager;
import edu.cmu.sphinx.linguist.dictionary.Dictionary;
import edu.cmu.sphinx.linguist.dictionary.FastDictionary;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.linguist.language.ngram.SimpleNGramModel;
import edu.cmu.sphinx.util.LogMath;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.apache.commons.io.FileUtils;

import edu.ucd.speech.ProcessFile;

/**
 * Look at the n-grams created by splitting completely. 
 * Determine which n-grams came from split identifiers and see which ones gives you a lower entropy.
 * @author mingxiao10016
 *
 */
public class Entropy {

	/**
	 * Given the probability in log_10 value, we compute the entropy in bits 
	 * @param logValue
	 * @return
	 */
	public static float entropyInBits(float logValue){
		float result;
		//convert value to log base 2
		float log2Value = LogMath.logToLog(logValue, 10, 2);
		
		result = (float) Math.pow(2.0, log2Value) * log2Value;
		return -result; //negate result to get positive value
	}
	
	public static FastDictionary getDictionary(String dictPath) throws MalformedURLException, ClassNotFoundException{
//		String dictPath = "/Users/mingxiao10016/Documents/speech_sphinx/test/tmp.dict";
//		String dictPath = "/Users/mingxiao10016/Documents/speech_sphinx/lib/dictionaries/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz/dict/cmudict.0.6d";
		String fillerPath = "/Users/mingxiao10016/Documents/speech_sphinx/lib/dictionaries/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz/noisedict";
		boolean addSil = false;
		String wordReplacement = "<sil>";
		UnitManager manager = new UnitManager();
		return new FastDictionary(dictPath,fillerPath,null, addSil,wordReplacement,addSil, addSil, manager);
	}
	
	public static SimpleNGramModel getNGramModel(Dictionary fastd, String location) throws MalformedURLException, ClassNotFoundException{
		LogMath logmath = new LogMath((float) 10.0,false);
//		String location ="/Users/mingxiao10016/Documents/speech_sphinx/test/tmp-lm.arpa";
//		String location = "/Users/mingxiao10016/Documents/speech_sphinx/cross-validation/split3-NUM/train-0-lm/train-0-processed.arpa";
		float weight = (float) 0.7;
		return new SimpleNGramModel(location,fastd,weight,logmath, 3);
	}
	
//	public static void stuff(){
//		String dictPath = "/Users/mingxiao10016/Documents/speech_sphinx/lib/dictionaries/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz/dict/cmudict.0.6d";
		
//		String location = "/Users/mingxiao10016/Documents/speech_sphinx/cross-validation/split3-NUM/train-0-lm/train-0-processed.arpa";
//		try {
//			long startTime = System.currentTimeMillis();
//			FastDictionary fastDict = getDictionary();
//			fastDict.allocate();
//			long endTime = System.currentTimeMillis();
//			System.out.println("dictionary execution time: " + (endTime-startTime));
//			System.out.println("fast dictionary load successful");
//			
//			startTime = System.currentTimeMillis();
//			SimpleNGramModel ngram = getNGramModel(fastDict);
//			ngram.allocate();
//			endTime = System.currentTimeMillis();
//			System.out.println("ngram execution time: " + (endTime-startTime));
//			System.out.println("ngram allocation successful");
			
//			double sum = 0.0;
//			for(String s:ngram.getVocabulary()){
//				Word word = new Word(s,null,false);
//				Word[] wList = {word};
//				WordSequence ws = new WordSequence(wList);
//				float p = ngram.getProbability(ws);
//				System.out.println(ws.toString()+" "+p);
//				float f = entropyInBits(p);
//				System.out.println(f);
//				sum += f;
//			}
//			System.out.println(sum);
//			Word tmp = new Word("error",null,false);
//			Word tmp2 = new Word("ldsfd", null,false);
//			Word[] bar = {tmp};
//			Word[] wl = {tmp,tmp2};
//			
//			WordSequence barws = new WordSequence(bar);
//			WordSequence ws = new WordSequence(wl);
////			Word[] none = {Word.UNKNOWN};
////			WordSequence nws = new WordSequence(none);
////			float n =ngram.getProbability(nws);
//			float t = ngram.getProbability(ws);
//			System.out.println(ws.toString()+" "+t);
//			System.out.println(barws.toString()+" "+ngram.getProbability(barws));
////			System.out.println(t == n); 
//			float k = entropyInBits(t);
//			System.out.println(k);
			
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 
	 * @param ngram
	 * @param word1
	 * @param word2
	 * @param word3
	 */
	private static float getEntropy(SimpleNGramModel ngram,String word1,String word2, String word3){
		Word[] wList = {new Word(word1,null,false),
				new Word(word2,null,false),
				new Word(word3,null,false)};
		
		WordSequence ws = new WordSequence(wList);
		float prob = ngram.getProbability(ws);
		return entropyInBits(prob);
		
	}
	
	private static float getEntropy(SimpleNGramModel ngram,String word1){
		Word[] wList = {new Word(word1,null,false),};
		
		WordSequence ws = new WordSequence(wList);
		float prob = ngram.getProbability(ws);
		return entropyInBits(prob);
	}
	
	private static float getEntropy(SimpleNGramModel ngram,String word1,String word2){
		Word[] wList = {new Word(word1,null,false),
				new Word(word2,null,false),};
		
		WordSequence ws = new WordSequence(wList);
		float prob = ngram.getProbability(ws);
		return entropyInBits(prob);
		
	}
	/**
	 * This is just a script for getting the entropy of split identifiers
	 * ISSUE: This requires contact with the dictionary. Apparently if the dicitonary does not
	 * contain a pronunciation for a particular word, then it'll get mapped to the UNK token,
	 * which has a probability of virutally 0.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void splitIdentEntropy() throws ClassNotFoundException, IOException{
		String dictPath = "/Users/mingxiao10016/Documents/speech_sphinx/lib/dictionaries/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz/dict/cmudict.0.6d";
		Dictionary dict = getDictionary(dictPath);
		dict.allocate();
		String location = "/Users/mingxiao10016/Documents/speech_sphinx/cross-validation/split3-NUM/train-0-lm/train-0-processed.arpa";
		SimpleNGramModel ngram = getNGramModel(dict,location);
		ngram.allocate();
		String filename = "test-0-unique-identifiers";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		FileWriter fw = new FileWriter(filename+"-entropy");
//		String line = br.readLine();
		String line;
//		System.out.println(br.readLine());
		while((line = br.readLine())!=null ){
			line = line.trim();
			String[] words = ProcessFile.splitIdentifier(line, false);
			for(int i=0; i< words.length; i++)
				words[i] = words[i].toLowerCase();
			float f;
			switch(words.length){
			case 1:
				//getEntropy of one
				f = getEntropy(ngram,words[0]);
				System.out.println(words[0]+","+f);
				fw.write(words[0]+","+f);
				fw.write("\n");
				break;
			case 2:
				//getEntropy of two
				f = getEntropy(ngram, words[0], words[1]);
				System.out.println(words[0]+" "+words[1]+","+f);
				fw.write(words[0]+" "+words[1]+","+f);
				fw.write("\n");
				break;
			default:
				//getEntropy of every triple
				System.out.println(line);
//				fw.write(line);
//				fw.write("\n");
				for(int i = 0;i<words.length-2;i++){
					f = getEntropy(ngram,words[i],words[i+1],words[i+2]);
					System.out.println("\t"+words[i]+" "+words[i+1]+" "+words[i+2]+","+f);
					fw.write("\t"+words[i]+" "+words[i+1]+" "+words[i+2]+","+f);
					fw.write("\n");
				}
				break;
			}//switch
		}//while
		fw.flush();
		fw.close();
	}
	
	/**
	 * Script for getting the entropy values of non-split identifiers.
	 * Requires use of dictionary, see the notes on splitIdentEntropy()
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void nosplitIdentEntropy() throws ClassNotFoundException, IOException{
		String dictPath="/Users/mingxiao10016/Documents/speech_sphinx/lib/dictionaries/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz/dict/cmudict.0.6d";
		String location="/Users/mingxiao10016/Documents/speech_sphinx/cross-validation/nosplit/train-0-lm/train-0-processed.arpa";
		
		FastDictionary dict = getDictionary(dictPath);
		dict.allocate();
		SimpleNGramModel ngram = getNGramModel(dict,location);
		ngram.allocate();
		
		String filename = "test-0-unique-identifiers";
		String line;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		FileWriter fw = new FileWriter(filename+"-nosplit-entropy");
		while((line=br.readLine())!=null){
			line = line.trim();
			
			float entropy = getEntropy(ngram,line);
			fw.write(line+","+entropy);
			fw.write("\n");
			
			line=br.readLine();
		}
	}
	
	/**
	 * Given a file containing unigrams and their respective entropies, and a list of token to filter
	 * through, we output the entropies of those tokens in base 2
	 * @param unigramFile - .arpa file containing unigram tokens
	 * @param tokenList
	 * @param outEntropies
	 * @throws IOException 
	 */
	public static void unigramEntropy(File unigramFile, File tokenList, File outEntropies) throws IOException{
		HashMap<String, Float> unigramMap = unigramFileToHashMap(unigramFile);
		int numUNKS=0;
		//Read in entropyFile and populate entropyMap
		BufferedReader br;
		String line;
		assert unigramMap.size() >0;
		for(String s:unigramMap.keySet())
			System.out.println(s +","+unigramMap.get(s));
		//read in tokenList and use that as a filter, write out entopy values to outEntropies
		br = new BufferedReader(new FileReader(tokenList));
		FileWriter fw = new FileWriter(outEntropies);
		while ((line=br.readLine())!=null){
			line = line.trim(); //get rid of ending newline
			if(unigramMap.containsKey(line)){
				fw.write(unigramMap.get(line)+" "+line);
				fw.write("\n");
			}else{
				numUNKS++;
			}
		}
		
		fw.flush();
		fw.close();
		br.close();
		System.out.println("number of unknowns: "+numUNKS);
	}
	
	/**
	 * Takes a unigram file, in ARPA format, and returns a hashmap that maps from unigram --> log prob.
	 * Each line should be of the form <log prob> <unigram> <backoff prob>
	 * @param unigramFile
	 * @return
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	private static HashMap<String,Float> unigramFileToHashMap(File unigramFile) throws NumberFormatException, IOException{
		assert unigramFile.exists();
		HashMap<String,Float> unigramMap = new HashMap<String,Float>();
		BufferedReader unigramReader = new BufferedReader(new FileReader(unigramFile));
		String line;
		while ((line=unigramReader.readLine())!=null){
			String[] items = line.split(" |\t");
			assert items.length ==3;
			unigramMap.put(items[1], Float.parseFloat(items[0]));
		}
		assert unigramMap.size() > 0; 
		return unigramMap;
	}
	/**
	 * Takes a bigram File, in ARPA format, and returns a hashmap that maps from bigram --> log prob
	 * Each line of the bigram file should be of the form: <log prob> <token1> <token2> <backoff prob>
	 * NOTE: the hashmap will map from "<token1> <token2>" (as ONE string) to the log prob.
	 * @param bigramFile
	 * @return
	 * @throws IOException 
	 */
	private static HashMap<String, Float> bigramFileToHashMap(File bigramFile) throws IOException{
		assert bigramFile.exists();
		HashMap<String, Float> bigramMap = new HashMap<String,Float>();
		
		BufferedReader bigramReader = new BufferedReader(new FileReader(bigramFile));
		String line;
		while ((line=bigramReader.readLine()) != null){
			String[] lineItems = line.split(" |\t");
			assert lineItems.length == 4;
			bigramMap.put(lineItems[1]+" "+lineItems[2], Float.parseFloat(lineItems[0]));
			
		}
		assert bigramMap.size() > 0;
		return bigramMap;
	}
	/**
	 * Takes a trigram file and returns a hash map (trigram --> log prob)
	 * NOTE: the hashmap will map from "<token1> <token2> <token3>" (as ONE string) to the log prob. 
	 * @param trigramFile - arpa format trigram file
	 * @return
	 * @throws IOException 
	 */
	private static HashMap<String, Float> trigramFileToHashMap(File trigramFile) throws IOException{
		assert trigramFile.exists();
		HashMap<String,Float> trigramMap = new HashMap<String, Float>();
		
		BufferedReader trigramReader = new BufferedReader(new FileReader(trigramFile));
		String line;
		
		while((line=trigramReader.readLine()) != null){
			String[] tokens = line.split(" |\t");
			assert tokens.length == 4;
			trigramMap.put(tokens[1]+" "+tokens[2]+" "+tokens[3], Float.parseFloat(tokens[0]));
		}
		
		assert trigramMap.size() > 0;
		return trigramMap;
	}
	
	/**
	 * Given a file of bigram tokens (tokenFile) we look through our bigram file to find that token.
	 * tokenFile should have one bigram or unigram per line.
	 * If the bigram is not in bigramFile (and it is not a unigram) we map to the <UNK>,<UNK> bigram
	 * If the unigram is not in unigramFile we map it to the <UNK> unigram
	 * @param unigramFile
	 * @param bigramFile
	 * @param tokenFile
	 * @param outFile
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void bigramEntropy(File unigramFile, File bigramFile, File tokenFile, File outFile) throws NumberFormatException, IOException{
		assert unigramFile.exists();
		assert bigramFile.exists();
		assert tokenFile.exists();
		
		HashMap<String,Float> unigramMap = unigramFileToHashMap(unigramFile);
		HashMap<String,Float> bigramMap = bigramFileToHashMap(bigramFile);
		
		BufferedReader tokenReader = new BufferedReader(new FileReader(tokenFile));
		FileWriter fw = new FileWriter(outFile);
		String line;
		
		int unknownUnigram = 0, unknownBigram = 0;
		int totalUnigram = 0, totalBigram = 0;
		
		while((line = tokenReader.readLine()) != null ){
			line = line.trim();
			assert line.length() > 0;
			
			String[] tokens = line.split(" |\t");
			if(tokens.length == 2){
				//bigram
				String bigram = tokens[0]+" "+tokens[1];
				if(bigramMap.containsKey(bigram)){
					fw.write(bigramMap.get(bigram)+" "+bigram);
					fw.write("\n");
				}else{
					unknownBigram++;
				}
				totalBigram++;
			}else{
				assert tokens.length == 1; //unigram
				if(unigramMap.containsKey(tokens[0])){
					fw.write(unigramMap.get(tokens[0])+" "+tokens[0]);
					fw.write("\n");
				}else{
					unknownUnigram++;
				}
				totalUnigram++;
			}
		}
		
		System.out.println("unknown unigrams: "+unknownUnigram+"/"+totalUnigram);
		System.out.println("unknown bigrams: "+unknownBigram+"/"+totalBigram);
		tokenReader.close();
		fw.flush();
		fw.close();
	}
	/**
	 * Given a token file (one trigram,bigram, or unigram per line) we output that n-gram along with 
	 * its log probability in the outFile
	 * @param unigramFile - arpa format unigram file
	 * @param bigramFile - arpa format bigram file
	 * @param trigramFile - arpa format trigram file
	 * @param tokenFile - tokens we are interested int
	 * @param outFile
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void trigramEntropy(File unigramFile, File bigramFile, File trigramFile,
			File tokenFile, File outFile) throws NumberFormatException, IOException{
		assert unigramFile.exists();
		assert bigramFile.exists();
		assert trigramFile.exists();
		assert tokenFile.exists();
		
		HashMap<String,Float> unigramMap = unigramFileToHashMap(unigramFile);
		HashMap<String,Float> bigramMap = bigramFileToHashMap(bigramFile);
		HashMap<String,Float> trigramMap = trigramFileToHashMap(trigramFile);
		
		BufferedReader tokenReader = new BufferedReader(new FileReader(tokenFile));
		BufferedWriter trigramWriter = new BufferedWriter(new FileWriter(outFile));
		String line;
		
		//record some stats
		int unknownUnigram = 0, unknownBigram = 0, unknownTrigram = 0;
		int totalUnigram = 0, totalBigram = 0, totalTrigram = 0;
		
		while((line = tokenReader.readLine()) != null){
			line = line.trim();
			assert line.length() > 0;
			
			String[] tokens = line.split(" |\t");
			assert tokens.length > 0;
			
			if(tokens.length == 3){
				//trigrams
				String trigram = tokens[0]+" "+tokens[1]+" "+tokens[2];
				if(trigramMap.containsKey(trigram)){
					trigramWriter.write(trigramMap.get(trigram)+" "+trigram);
					trigramWriter.newLine();
				}else{
					unknownTrigram++;
				}
				totalTrigram++;
			}else if(tokens.length == 2){
				//bigrams
				String bigram = tokens[0]+" "+tokens[1];
				if(bigramMap.containsKey(bigram)){
					trigramWriter.write(bigramMap.get(bigram)+" "+bigram);
					trigramWriter.newLine();
				}else{
					unknownBigram++;
				}
				totalBigram++;
			}else{
				//unigrams
				assert tokens.length == 1;
				if(unigramMap.containsKey(tokens[0])){
					trigramWriter.write(unigramMap.get(tokens[0])+" "+tokens[0]);
					trigramWriter.newLine();
				}else{
					unknownUnigram++;
				}
				totalUnigram++;
			}//if
		}//while
		System.out.println("Unknown unigrams: "+unknownUnigram+"/"+totalUnigram);
		System.out.println("Unknown bigrams: "+unknownBigram+"/"+totalBigram);
		System.out.println("Unknown trigrams: "+unknownTrigram+"/"+totalTrigram);
		trigramWriter.flush();
		trigramWriter.close();
		tokenReader.close();
	}
	/**
	 * Takes a file of identifers (one per line) and splits them into bigram (one per line).
	 * Suppose in the identFile we have:
	 * 		upArrow
	 * 		closeOnExit
	 * Then in the bigram file, we should have:
	 * 		up Arrow
	 * 		close On
	 * 		On Exit
	 * @param unigramFile
	 * @param bigramFile
	 * @throws IOException 
	 */
	public static void unigramToBigram(File identFile, File bigramFile) throws IOException{
		assert identFile.exists();
		
		BufferedReader identReader = new BufferedReader(new FileReader(identFile));
		BufferedWriter bigramWriter = new BufferedWriter(new FileWriter(bigramFile));
		String line;
		
		while((line=identReader.readLine()) != null){
			line = line.trim();
			String[] lineItems = ProcessFile.splitIdentifier(line, false); //split the identifer
			//replace all interger occurences with <NUM>
			for(int i=0; i<lineItems.length; i++){
				if(isInteger(lineItems[i])){
					lineItems[i] = "NUM";
				}
			}
			//print out each pair of tokens to bigramFile, one per line
			if(lineItems.length < 2){
				assert lineItems.length > 0;
				bigramWriter.write(lineItems[0]);
				bigramWriter.newLine();
			}else{
				for(int i=0; i<lineItems.length-1; i++){
					bigramWriter.write(lineItems[i]+" "+lineItems[i+1]);
					bigramWriter.newLine();
				}//for
			}//if
		}//while
		
		bigramWriter.flush();
		bigramWriter.close();
		identReader.close();
	}
	
	/**
	 * unigramFile have one identifier per line. If we can derive trigrams from the splitting the 
	 * identifer, then do so. Otherwise revert to bigrams, then unigrams
	 * Suppose in the identFile we have:
	 * 		closeButtonOnExit
	 * 		nextReady
	 * Then in the bigram file, we should have:
	 * 		close Button On
	 * 		Button on Exit
	 * 		next Ready
	 * @param identFile
	 * @param trigramFile
	 * @throws IOException 
	 */
	public static void unigramToTrigram(File identFile, File trigramFile) throws IOException{
		assert identFile.exists();
		
		BufferedReader identReader = new BufferedReader(new FileReader(identFile));
		BufferedWriter trigramWriter = new BufferedWriter(new FileWriter(trigramFile));
		String line;
		
		while((line = identReader.readLine()) != null ){
			line = line.trim();
			assert line.length() > 0;
			
			String[] token = ProcessFile.splitIdentifier(line, false);
			for(int i=0; i<token.length; i++){
				if(isInteger(token[i])){
					token[i] = "NUM";
				}
			}//for
			
			assert token.length > 0;
			if(token.length == 1){
				//unigram
				trigramWriter.write(token[0]);
				trigramWriter.newLine();
			}else if(token.length ==2){
				trigramWriter.write(token[0]+" "+token[1]);
				trigramWriter.newLine();
				//bigram
			}else{
				//trigram(s)
				for(int i =0; i< token.length-2; i++){
					trigramWriter.write(token[i]+" "+token[i+1]+" "+token[i+2]);
					trigramWriter.newLine();
				}
			}
		}//while
		//clean-up
		trigramWriter.flush();
		trigramWriter.close();
		identReader.close();
	}
	
	private static boolean isInteger(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Go through the file and output the trigram of consisting of <token> <identifier> <token>.
	 * Print the before and after token. 
	 * First without splitting the tokens
	 * @param file
	 * @throws IOException 
	 */
	public static void includeBeforeAndAfterToken(File file) throws IOException{
		assert file.exists();
		
		String filePath = file.getAbsolutePath();
		
		CharStream cs = new ANTLRFileStream(filePath);
		JavaLexer lexer = new JavaLexer(cs);

		Token previousPrevious = lexer.nextToken();
		Token previous = lexer.nextToken();
		Token current = lexer.nextToken();
		Token next = lexer.nextToken();
		Token nextNext = lexer.nextToken();
		
//		System.out.println(previous.getText()+":"+previous.getType());
//		System.out.println(current.getText()+":"+current.getType());
//		System.out.println(next.getText()+":"+next.getType());
		
		while(nextNext.getType() != Token.EOF){
			if(current.getType() == JavaLexer.IDENTIFIER){
				System.out.println(
						previousPrevious.getText()+":"+previousPrevious.getType()+","+
						previous.getText()+":"+previous.getType()+","+
						current.getText()+":"+current.getType()+","+
						next.getText()+":"+next.getType()+","+
						nextNext.getText()+":"+nextNext.getType());
				
				String toOutput = current.getText();
				
				//We don't want to include the whitespace token. If previous token is whitespace, then
				//prepend the previousPrevious token. Otherwise prepend the previous token
				if(previous.getType() != JavaLexer.WS)
					toOutput = previous.getText()+" "+toOutput;
				else
					toOutput = previousPrevious.getText()+" "+toOutput;
				
				//If the next token is whitespace, then append the nextNext token. Otherwise append
				//the next token
				if(next.getType() != JavaLexer.WS)
					toOutput = toOutput +" "+next.getText();
				else
					toOutput = toOutput +" "+ nextNext.getText();
				
				System.out.println("OUTPUT:"+toOutput);
				System.out.println("PROCESSED:"+ProcessFile.process_line(toOutput));
			}
			
			
			
			previousPrevious = previous;
			previous = current;
			current = next;
			next = nextNext;
			nextNext = lexer.nextToken();
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException{
//		nosplitIdentEntropy();
	}
}
