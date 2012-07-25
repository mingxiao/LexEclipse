package edu.ucd.speech;
/**
 * Process source code so we spell out punctuations and separate agglutinations
 * @author Ming
 *
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

//import com.ibm.icu.text.*;
public class ProcessFile {

	//public  String FILE = "Beam.java";
	//public  String OUTFILE = FILE+"_OUT.txt";
	public String FILE;
	public String OUTFILE;
	/*
	 * These are the string replacements for the respective punctuation
	 */
	private static String _SEMICOLON = " SEMICOLON "; //need the space in case there are other words
	private static String _DOT = " DOT ";
	private static String _COMMA = " COMMA ";
	private static String _OPEN_PAREN = " OPENPAREN ";
	private static String _CLOSE_PAREN = " CLOSEPAREN ";
	private static String _OPEN_BRACKET = " OPENBRACKET ";
	private static String _CLOSE_BRACKET = " CLOSEBRACKET ";
	public static  String _AT = " AT ";
	private static String _QUOTE = " QUOTE ";
	private static String _PLUS = " PLUS ";
	private static String _EQUALS = " EQUALS ";
	private static String _COLON = " COLON ";
	private static String _FORWARD_SLASH = " FORWARDSLASH ";
	private static String _BACKSLASH = " BACKSLASH ";
	private static String _MINUS = " MINUS ";
	private static String _OPEN_CURLY_BRACE = " OPENCURLY ";
	private static String _CLOSE_CURLY_BRACE = " CLOSECURLY ";
	private static String _ASTERISK = " ASTERISK ";
	private static String _PERCENT = " PERCENT ";
	
	//Added after initial run
	private static String _BANG = " BANG ";
	private static String _LESS_THAN = " LESS THAN ";
	private static String _GREATER_THAN = " GREATER THAN ";
	private static String _HASHTAG = " HASHTAG ";
	private static String _AND = " AND ";
	private static String _OR = " OR ";
	private static String _QUESTION_MARK = " QUESTION MARK ";
	private static String _CARET = " CARET ";
	private static String _DOLLAR_SIGN = " DOLLAR SIGN ";
	private static String _TILDE = " TILDE ";
	
	
	
	/**
	 * Replaces all occurence of ";" with " SEMICOLON "
	 * @param line
	 * @return
	 */
	private static String process_semicolon(String line){
		return line.replaceAll(";", _SEMICOLON);
	}
	private static String process_and(String line){
		return line.replaceAll("&", _AND);
	}
	private static String process_tilde(String line){
		return line.replaceAll("~", _TILDE);
	}
	private static String process_less_than(String line){
		return line.replaceAll("<", _LESS_THAN);
	}
	
	private static String process_greater_than(String line){
		return line.replaceAll(">", _GREATER_THAN);
	}
	private static String process_hashtag(String line){
		return line .replaceAll("#", _HASHTAG);
	}
	private static String process_bang(String line){
		return line.replaceAll("!", _BANG);
	}
	private static String process_question_mark(String line){
		return line.replaceAll("\\?", _QUESTION_MARK);
	}
	private static  String process_dot(String line){
		return line.replaceAll("\\.", _DOT); //needed to escape . (wildcard)
	}
	private static String process_caret(String line){
		return line.replaceAll("\\^", _CARET);
	}
	private static String process_dollar_sign(String line){
		return line.replaceAll("\\$", _DOLLAR_SIGN);
	}
	private static String process_comma(String line){
		return line.replaceAll(",", _COMMA);
	}
	private static String process_open_paren(String line){
		return line.replaceAll("\\(", _OPEN_PAREN); //need to escape (
	}
	private static String process_close_paren(String line){
		return line.replaceAll("\\)", _CLOSE_PAREN); //need to escape (
	}
	private static String process_open_bracket(String line){
		return line.replaceAll("\\[", _OPEN_BRACKET); //need to escape (
	}
	private static String process_close_bracket(String line){
		return line.replaceAll("\\]", _CLOSE_BRACKET); //need to escape (
	}
	private static String process_at(String line){
		return line.replaceAll("@", _AT); 
	}
	private static String process_quote(String line){
		return line.replaceAll("\"|\'", _QUOTE); //need to escape "
	}
	private static String process_plus(String line){
		return line.replaceAll("\\+", _PLUS); //need to escape +
	}
	private static String process_minus(String line){
		return line.replaceAll("\\-", _MINUS); //need to escape +
	}
	private static String process_equals(String line){
		return line.replaceAll("=", _EQUALS); 
	}
	private static String process_colon(String line){
		return line.replaceAll(":", _COLON); 
	}
	private static String process_forward_slash(String line){
		return line.replaceAll("\\/", _FORWARD_SLASH); //need to escape /
	}
	private static String process_backslash(String line){
		return line.replaceAll("\\\\", _BACKSLASH); //need to escape \
	}
	private static String process_open_curly_brace(String line){
		return line.replaceAll("\\{", _OPEN_CURLY_BRACE); 
	}
	private static String process_close_curly_brace(String line){
		return line.replaceAll("\\}", _CLOSE_CURLY_BRACE); 
	}
	private static String process_percent(String line){
		return line.replaceAll("%", _PERCENT );
	}
	private static String process_asterisk(String line){
		return line.replaceAll("\\*", _ASTERISK); 
	}
	private static String process_or(String line){
		return line.replaceAll("\\|", _OR);
	}
	
	public static boolean hasCapsOrNum(String s){
		int caps = 0;
		boolean hasNum = false;
		for(int i = 0; i<s.length();i++){
			if (Character.isUpperCase(s.charAt(i)))
				caps++;
			else if(Character.isDigit(s.charAt(i)))
				hasNum=true;
		}
		return caps>0 | hasNum;
	}
	
	public static String process_camel_case2(String line){
		StringBuilder sb = new StringBuilder();
		for(String s:line.split(" +|\t")){
			if(hasCapsOrNum(s)){
				//System.out.println(s);
				for(String str: StringUtils.splitByCharacterTypeCamelCase(s)){
					sb.append(str);
					sb.append(" ");
				}
			}else
				sb.append(s);
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	
	/**
	 * numStr should be a string representation of a number
	 * Returns a string where the numStr is split into its individual digits
	 * "12345" -> "1 2 3 4 5"
	 * 
	 * There are some test string which are extremely long. Numbers that cannot cast to an
	 * INTEGER are assumed to be test values and are simply returned as is.
	 * @param numStr
	 */
	public static String split_digits(String numStr){
		try{
			int n = Integer.parseInt(numStr);
			if (n/10 == 0){
				//n is one or more 0's
				return n+" ";
			}
			//Get each digit of number. Use stack to preverse order
			Stack<Integer> numStack = new Stack<Integer>();
			while(n > 0){
				//System.out.println(n%10);
				numStack.push(n%10);
				n=n/10;
			}
			//Convert number to string
			StringBuilder sb = new StringBuilder();
			while(!numStack.isEmpty()){
				sb.append(numStack.pop()+" ");
			}
			return sb.toString();
		}catch(Exception e){
			System.err.println("Error parsing number string:"+numStr);
			return numStr;
		}
		
	}
	
	/**
	 * Given a string with multiple numbers (separated by spaces or a tab) 
	 * splits the numbers into their individual digits
	 * @param line
	 * @return
	 */
	public static String process_numbers(String line){
		StringBuilder sb = new StringBuilder();
		for(String s: line.split(" +|\t")){
			if(s.matches("\\d+")){
				//split the number
				sb.append(split_digits(s));
			}
			else{
				sb.append(s);
				sb.append(" "); //separate token with a space
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Given a line of code, splits all the snake case variables
	 * @param line
	 * @return
	 */
	public static  String process_snake_case(String line){
		StringBuilder sb = new StringBuilder();
		for(String s : line.split(" +|\t")){
			//System.out.println(s);
			if(is_snake_case(s.trim())){
				sb.append(splitSnakeCase(s.trim()));
			}
			else
				sb.append(s);
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	
	public static  String splitSnakeCase(String s) {
		return s.replaceAll("_+", " ");
	}

	/**
	 * Process each line based on the punctuations we want to spell out
	 */
	public static  String process_punct(String line){
		line = process_at(line);
		line = process_asterisk(line);
		line = process_backslash(line);
		line = process_colon(line);
		line = process_comma(line);
		line = process_dot(line);
		line = process_equals(line);
		line = process_minus(line);
		//===========
		line = process_forward_slash(line);
		line = process_close_bracket(line);
		line = process_close_curly_brace(line);
		line = process_close_paren(line);
		line = process_open_bracket(line);
		line = process_open_curly_brace(line);
		line = process_open_paren(line);
		line = process_percent(line);
		line = process_plus(line);
		line = process_quote(line);
		line = process_semicolon(line);
		
		//added after initial run
		line = process_bang(line);
		line = process_less_than(line);
		line = process_greater_than(line);
		line = process_hashtag(line);
		line = process_and(line);
		line = process_or(line);
		line = process_question_mark(line);
		line = process_caret(line);
		line = process_dollar_sign(line);
		line = process_tilde(line);
		return line;
	}
	
	/**
	 * For each line:
	 * 1.Spells out punctuations (process_punct)
	 * 2.Humanize camel cases
	 * @param line
	 * @return
	 */
	public static  String process_line(String line){
		line = process_punct(line);
		//process snake case FIRST because of segments like:IgnoreResourcesDialog_filesWithSpaceWarning
		line = process_snake_case(line);
		line = process_camel_case2(line);
		line = process_numbers(line);
		return line;
	}
	
	private static  boolean is_snake_case(String str){
		return str.matches("[A-Za-z0-9]*(_+[A-Za-z0-9]+_?)+");
	}
	/**
	 * Given an identifier (in snake case), returns a list of its individual words
	 * arrow_up ---> [arrow, up]
	 * IgnoreResult_fillerTrue ----> [IgnoreResult, fillerTrue]
	 * noSnakeCase --> [noSnakeCase]
	 * @param ident - Name of identifier
	 * @return
	 */
	public static String[] snakeCaseToWords(String ident){
		List<String> words = new ArrayList<String>();
		String[] w = ident.split("_+");
		for(String s:w)
			words.add(s);
		return words.toArray(new String[0]);
	}
	
	public static String[] camelCaseToWords(String ident){
		return StringUtils.splitByCharacterTypeCamelCase(ident);
	}
	
	/**
	 * Given an identifer, split it based on the snake case, and then the camel case
	 * @param ident
	 * @return
	 */
	public static String[] splitIdentifier(String ident){
		List<String> words = new ArrayList<String>();
		for(String s : snakeCaseToWords(ident)){
			for (String t: camelCaseToWords(s))
				words.add(t);
		}
			
		return words.toArray(new String[0]);
	}
	
	public static void process(String inFile, String outFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		FileWriter fw = new FileWriter(outFile);
		String line;
		
		while((line=br.readLine()) != null){
			line = process_line(line.trim());
			fw.write(line+"\n");
		}
		fw.flush();
		fw.close();
		br.close();
	}
	
	public static void main(String[] args) throws IOException {
		String s = "JavaHotCodeReplaceManager.java";
		String t = "JavaHotCodeReplaceManager_processed.txt";
		process(s, t);
	}
}
