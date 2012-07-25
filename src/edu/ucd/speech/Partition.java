package edu.ucd.speech;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class Partition {

	//private String SRC = "C:\\cygwin\\home\\eclipse_source\\java_processed";
	private String SRC  ="/Users/mingxiao10016/Documents/speech_sphinx/eclipse_java_source";
	private HashMap<Integer,Integer> numSeen;
	
	/**
	 * Randomly deletes files from directory until we have per percent remaining
	 * @param dest
	 * @param percent
	 */
	public void partition2(String directory,float per){
		File destFile = new File(directory);
		File[] files = destFile.listFiles();
		
	}
	
	/**
	 * Randomly chooses files from SRC and moves it into dest. 
	 * Keep choosing files until we achieve a ratio of percent.
	 * @param dest
	 * @param percent
	 * @throws IOException 
	 */
	public void partition(String dest, float percent) throws IOException{
		numSeen = new HashMap<Integer,Integer>();
		File destFile = new File(dest);
		File srcFile = new File(SRC);
		long srcSize =FileUtils.sizeOfDirectory(srcFile);
		long destSize =FileUtils.sizeOfDirectory(destFile);
		File[] files = srcFile.listFiles();
		int numFiles = files.length;
		Random random = new Random();
		int randInt;
		float progress;
		//((float)destSize/srcSize) <percent 
		while((progress = (float)destSize/srcSize) <percent ){
			//choose random file
			randInt = random.nextInt(numFiles);
			//move random file
			if(!numSeen.containsKey(randInt)){
				FileUtils.copyFileToDirectory(files[randInt], destFile);
				destSize += FileUtils.sizeOf(files[randInt]);
				System.out.println(progress + " goal: "+percent);
				numSeen.put(randInt, randInt);
			}
		}
		
	}
	/**
	 * Directory dest should contain ALL the files. We want to remove files
	 * until we reach the achieved percentage
	 * @param dest
	 * @param percent
	 */
	public void reversePartition(String dest,float percent){
		numSeen = new HashMap<Integer,Integer>();
		File destFile = new File(dest);
//		File srcFile = new File(SRC);
//		long srcSize =FileUtils.sizeOfDirectory(srcFile);
		long destSize =FileUtils.sizeOfDirectory(destFile);
		long currSize = destSize;
		File[] files = destFile.listFiles();
		int numFiles = files.length;
		Random random = new Random();
		int randInt;
		float progress;
		while((progress = (float)currSize/destSize) >percent){
			randInt = random.nextInt(numFiles);
			if(!numSeen.containsKey(randInt)){
				currSize -= FileUtils.sizeOf(files[randInt]);
				FileUtils.deleteQuietly(files[randInt]);
				System.out.println(progress + " goal: "+percent);
				numSeen.put(randInt, randInt);
			}
		}
		
	}
	
	public Partition(){
	}
	
	public static void main(String[] args) throws IOException {
		
		String d;
//		String test = "C:\\cygwin\\home\\eclipse_source\\temp";
//		new Partition().reversePartition(test, (float) 0.6);
		d = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_20";
		new Partition().partition(d, (float)0.2);
//		d= "C:\\cygwin\\home\\eclipse_source\\java_40";
//		new Partition(d,(float) 0.4);
//		d= "C:\\cygwin\\home\\eclipse_source\\java_60";
//		new Partition().reversePartition(d, (float) 0.6);
//		d= "C:\\cygwin\\home\\eclipse_source\\java_80";
//		new Partition().reversePartition(d,(float) 0.8);
//		d= "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse_90";
//		new Partition().reversePartition(d,(float) 0.9);
//		d = "/Users/mingxiao10016/Documents/speech_sphinx/test/small";
//		new Partition().reversePartition(d, (float).5);
		
	}
}
