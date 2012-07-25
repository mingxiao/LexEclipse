package edu.ucd.speech;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.*;
import org.apache.commons.lang3.StringUtils;
public class Main {	
	
	static JavaLexer lexer;
	static Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
//	static String START = "<s>";
//	static String END = "</s>";
	
	/** Returns true if text contains punctuation
	 * 
	 */
	public static boolean containsPunct(String text){
		Matcher m = p.matcher(text);
		return m.find();
	}
		
	public static void processFile(File file,String dest) throws IOException{
		String filePath = file.getAbsolutePath();
		String outputFilePath = dest+File.separator+file.getName().substring(0, file.getName().length()-5)+".txt";
		CharStream cs = new ANTLRFileStream(filePath);
		lexer = new JavaLexer(cs);
		
		File outfile = new File(outputFilePath);
		//creates the output directory if it doesn't exist	
		if(outfile.getParentFile() != null){
			System.out.println(outfile.getParent());
			outfile.getParentFile().mkdirs();
		}
		FileWriter fw = new FileWriter(outfile);
		Token token = lexer.nextToken();
		while(token.getType() != Token.EOF) {
			System.out.println(token.toString());
			String text = token.getText();
			if(text.equals(System.getProperty("line.separator"))){
				System.out.println("here1");
				fw.write(text);
			}
			else if (token.getType() == 118){
				System.out.println("here2");
				//if it is white space print it
				fw.write(text);
			}else if(token.getType() == 26){
				System.out.println("here3");
				//skip the comments
			}
			else if(token.getType() == JavaLexer.IDENTIFIER){
				//If its an identifier we want to split it
				//need to test this
				System.out.println("here5");
				String[] words = ProcessFile.splitIdentifier(text);
				for(String s : words){
					fw.write(s);
					fw.write(' ');
				}
			}
			else if(containsPunct(text)){
				System.out.println("here4");
				fw.write(ProcessFile.process_punct(text));
			}
			else{
				fw.write(text);
				System.out.println("here6");
			}
            token = lexer.nextToken();
        }
		fw.flush();
		fw.close();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		run();
//		String src = "/Users/mingxiao10016/Documents/workspace/LexEclipse/src/edu/ucd/speech/testInput.java";
//		String dest =  "/Users/mingxiao10016/Documents/workspace/LexEclipse/edu/ucd/speech/output";
//		String src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_90";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_90_lexed";
		String src = "/Users/mingxiao10016/Documents/speech_sphinx/test/small";
		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/test/small_split";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/test/small_lexed";
//		String src = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_20";
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_20_lexed";
		File f = new File(src);
		if(f.isFile()){
			String outputFilePath = dest+File.separator+f.getName().substring(0, f.getName().length()-5)+".txt";
			System.out.println("outfile path: "+outputFilePath);
			processFile(f,dest);
		}
		else{
			for(File file :f.listFiles()){
				if(!file.isHidden()){
					System.out.println(file.getName());
					String outputFilePath = dest+File.separator+file.getName().substring(0, file.getName().length()-5)+".txt";
					System.out.println(outputFilePath);
					processFile(file,dest);
				}
			}//end for
		}
	}
	
}
