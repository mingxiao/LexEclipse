package edu.ucd.speech;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.CrossValidationPartitioner;
import opennlp.tools.util.eval.CrossValidationPartitioner.TrainingSampleStream;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.apache.commons.io.FileUtils;
public class Main {	
	
//	private static JavaLexer lexer;
	private static Pattern nonAlphaOrNum = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
	private static Pattern isInteger = Pattern.compile("\\d+");
	private static String NUM= "NUMBER";
	
	/** Returns true if text contains punctuation
	 * 
	 */
	public static boolean containsPunct(String text){
		Matcher m = nonAlphaOrNum.matcher(text);
		return m.find();
	}
		
	/**
	 * Processes a java file by lexing through each token and doing the following:
	 * -spelling out all puncutations
	 * -splitting identifiers
	 * -ignoring numbers (float,double, int ...)
	 * 
	 * overwrites the file in dest
	 * @param file the source file
	 * @param outfile - the destination file
	 * @param splitID - true if you want to split identifiers, false otherwise
	 * @param tol - minimum number of components to split on. 3 means split identifers with >= 3 components
	 * @throws IOException
	 */
	public static void processFile(File file,File outfile,boolean splitID, int tol) throws IOException{
		String filePath = file.getAbsolutePath();
		CharStream cs = new ANTLRFileStream(filePath);
		JavaLexer lexer = new JavaLexer(cs);
		
		if (!outfile.exists()){
			boolean result = outfile.createNewFile();
			assert result;
		}
		assert outfile.isFile();
		
		FileWriter fw = new FileWriter(outfile);
		Token token = lexer.nextToken();
		while(token.getType() != Token.EOF) {
//			System.out.println(token.toString());
			String text = token.getText();
			int tType = token.getType();
			if(text.equals(System.getProperty("line.separator"))){
				fw.write(text);
			}
			else if (tType == JavaLexer.WS){
				//if it is white space print it
				fw.write(text);
			}else if(tType == JavaLexer.COMMENT){
				//skip the comments
			}
			else if(tType==JavaLexer.INTLITERAL| 
					tType==JavaLexer.DOUBLELITERAL | 
					tType==JavaLexer.LONGLITERAL| 
					tType==JavaLexer.FLOATLITERAL){
				//if it is a number, skip it, I think it is throwing off our pronunciations
				fw.write(NUM);
			}
			else if(tType == JavaLexer.IDENTIFIER){
				/*
				 * If its an identifier and splitID == true, then we split into individual components.
				 * Otherwise we just write it as is.
				 */
				Matcher m;
				if(splitID){
					String[] words = ProcessFile.splitIdentifier(text,false);
					if (words.length >= tol){
						for(String s : words){
							m = isInteger.matcher(s);
							if(m.matches())
								fw.write(NUM);
							else
								fw.write(s);
							fw.write(' ');
						}
					}
					else
						fw.write(text);
				}
				else
					fw.write(text);
			}
			else if(tType == JavaLexer.STRINGLITERAL){
				//process the string literal
				String line = ProcessFile.process_line(text);
				fw.write(line);
			}
			else if(containsPunct(text)){
				fw.write(ProcessFile.process_punct(text));
			}
			else{
				fw.write(text);
			}
            token = lexer.nextToken();
        }
		fw.flush();
		fw.close();
	}
	
	/*
	 * Processes all files in the given src directory and moves then to the given dest directory
	 * Also changes the name, ie. /src/SomeClass.java --> /dest/SomeClass.txt
	 */
	public static void processDirectory(File f,File dest, boolean splitID, int tol) throws IOException{
		if(f.isFile()){
			String outputFilePath = dest+File.separator+f.getName().substring(0, f.getName().length()-5)+".txt";
//			System.out.println("outfile path: "+outputFilePath);
			processFile(f,dest,splitID,tol);
		}
		else{
			// dest folder does not exist, then create it
			dest.mkdir();
			for(File file :f.listFiles()){
				if(!file.isHidden()){
					String outname = file.getName().substring(0, file.getName().length()-5)+".txt";
					File destFile = new File(dest,outname);
					System.out.println(destFile.getAbsolutePath());
					processFile(file,destFile,splitID,tol);
				}
			}//end for
		}//end if
	}
	/**
	 * The given file should be a java source file. Return the number of identifiers in the file.
	 * @param f file name
	 * @param unique flag, true we only count unique identifiers
	 * @throws IOException 
	 */
	public static int countIdent(File file, boolean unique, HashSet<String> set, FileWriter fw, boolean split) throws IOException{
		//setup
		int numIdent = 0;
		String filePath = file.getAbsolutePath();
		CharStream cs = new ANTLRFileStream(filePath);
		JavaLexer lexer = new JavaLexer(cs);
//		HashSet<String> set = new HashSet<String>();
		
		Token token = lexer.nextToken();
		while (token.getType() != Token.EOF){
			if (token.getType() == JavaLexer.IDENTIFIER){
				if (unique){
					if(split){
						String[] words = ProcessFile.splitIdentifier(token.getText(),false);
						for(String s: words){
							if(!set.contains(s)){
								set.add(s);
								fw.write(s+"\n");
								numIdent++;
							}
						}
					}
					else{
						if(!set.contains(token.getText())){
							//new token
							set.add(token.getText());
							fw.write(token.getText()+"\n");
						}
					}
				}//if
				else
					numIdent++;
			}//if
			token = lexer.nextToken();
		}//while
		return numIdent;
	}
	
	/**
	 * Given a directory of java source code, return the number of identifiers.
	 * Can specify whether we want it to be unique.
	 * @param directory
	 * @param unique
	 * @return
	 * @throws IOException 
	 */
	public static int countIdentDirectory(File directory, boolean unique, String filename,boolean split) throws IOException{
		int numIdent = 0;
		HashSet<String> set = new HashSet<String>();
		FileWriter fw = new FileWriter(filename);
		
		for (File file: directory.listFiles()){
//			String filePath = file.getAbsolutePath();
//			cs = new ANTLRFileStream(filePath);
//			lexer = new JavaLexer(cs);
			numIdent += countIdent(file,unique,set,fw,split);
			System.out.println(set.size());
		}
		fw.flush();
		fw.close();
		return numIdent;
	}
	
	/**
	 * Paritions the files in source into 10 separate chunks, for cross validation use
	 * @param source
	 * @throws IOException 
	 */
	public static void crossValParition(File source, String baseDir, int partition) throws IOException{
		System.out.println("Creating cross validation paritions");
		assert baseDir.length() > 0;
		//setup, create the baseDir
		File base = new File(baseDir);
		if(base.getParent()!=null)
			base.mkdirs();
		
		//create collection of files from the source
		List<File> l = new ArrayList<File>();
		for(File f: source.listFiles()){
			l.add(f);
		}
		System.out.println("collection size: "+l.size());
		//instantiate the Partitioner
		CrossValidationPartitioner<File> parts = new CrossValidationPartitioner<File>(l,partition);
		TrainingSampleStream<File> trainStream;
		ObjectStream<File> testStream;
		File trainFile,testFile;
		
		int partition_num = 0;
		while(parts.hasNext()){
			trainStream = parts.next();
			File trainDir = new File(base,"train-"+partition_num);
			File testDir = new File(base,"test-"+partition_num);
			
			trainDir.mkdir();
			testDir.mkdir();
			
//			System.out.println("=============");
			// training set
			while ((trainFile = trainStream.read())!= null){
				FileUtils.copyFileToDirectory(trainFile, trainDir);
//				System.out.println(trainFile.getAbsolutePath());
			}
			//test set
			testStream = trainStream.getTestSampleStream();
			while ((testFile = testStream.read()) != null  ){
//				System.out.println(testFile.getAbsolutePath());
				FileUtils.copyFileToDirectory(testFile, testDir);
			}
			partition_num++;
			System.out.println("Completed one partition");
		}
		System.out.println("Finished Paritioning");
	}
	
	public static void processScript(){
//		String src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-20";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-20-nosplit";
//		processDirectory(src,dest,false);
//		src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-20-rest";
//		dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-20-rest-nosplit";
//		processDirectory(src,dest,false);
//		
//		
//		src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-60";
//		dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-60-nosplit";
//		processDirectory(src,dest,false);
//		src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-60-rest";
//		dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-60-rest-nosplit";
//		processDirectory(src,dest,false);
//		
//		src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90";
//		dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90-nosplit";
//		processDirectory(src,dest,false);
//		src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90-rest";
//		dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90-rest-nosplit";
//		processDirectory(src,dest,false);
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		run();
//		String src = "/Users/mingxiao10016/Documents/workspace/LexEclipse/src/edu/ucd/speech/testInput.java";
//		String dest =  "/Users/mingxiao10016/Documents/workspace/LexEclipse/edu/ucd/speech/output";
//		String src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_90";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_90_lexed";
//		String src = "/Users/mingxiao10016/Documents/speech_sphinx/test/small";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/test/small-nosplit";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/test/small_lexed";
		
	}//end main
	
}//end class
