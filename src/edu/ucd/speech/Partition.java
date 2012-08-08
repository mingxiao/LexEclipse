package edu.ucd.speech;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

/**
 * SRC is the folder containing ALL the eclipse source code
 * We want to randomly partition it to create language models out of.
 * Right now we choose files randomly from SRC until a certain ratio is achieved. (20%,40%,...)
 * 
 * Do we also need to keep the files that were not chosen somewhere else too?
 * Meaning if we want 20% of the eclipse code, should we keep track of another folder
 * that contains the 80% that was not chosen?
 * 
 * I think we need to. The 20% is used to train our language model, but we can't use 
 * the same 20% to test it. (This is what I've been doing before and I think its throwing
 * off my results.)
 * @author mingxiao10016
 *
 */
public class Partition {

	private static String SRC  ="/Users/mingxiao10016/Documents/speech_sphinx/eclipse_java_source";
	private static HashMap<Integer,Integer> numSeen;
	
	
	/**
	 * Randomly chooses files from SRC and moves it into dest. 
	 * Keep choosing files until we achieve a ratio of percent.
	 * 
	 * @param dest
	 * @param percent
	 * @throws IOException 
	 */
	public static void partition(String dest, float percent) throws IOException{
		numSeen = new HashMap<Integer,Integer>();
		File destFile = new File(dest);
		destFile.mkdir();
//		if(!destFile.exists())
//			  destFile.createNewFile();
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
		//create the test set using the filter() method
		filter(SRC,dest,dest+"-rest");
	}
	/**
	 * Directory dest should contain ALL the files from SRC. We want to remove files
	 * until we reach the achieved percentage.
	 * 
	 * Change: so that
	 * @param dest
	 * @param percent
	 */
	public static void reversePartition(String dest,float percent){
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
	/**
	 * Move all the file from source to dest that does not appear in filter directory
	 * Want to do this so that we separate the test and training set.
	 * Suppose we randomly choose 20% of eclipse to be our training set.
	 * That means we want the other 80 percent to be in the test set.
	 * @param source
	 * @param filter
	 * @param dest
	 */
	public static void filter(String source, String filter,String dest){
		File sourceFile = new File(source);
		File filterFile = new File(filter);
		
		//create the directory structure if it does not exist for destFile
		File destFile = new File(dest);
		if(destFile.getParentFile() != null){
			destFile.getParentFile().mkdirs();
		}
		
		List<File> sourceList = Arrays.asList(sourceFile.listFiles());
		List<String> filterList = Arrays.asList(filterFile.list());
		
		for(File file: sourceList){
			if(!filterList.contains(file.getName())){
//				System.out.println(file);
				try {
					FileUtils.copyFileToDirectory(file, destFile);
					System.out.println("moving: "+file.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
//		for(String s:sourceList)
//			System.out.println(s);
//		System.out.println("+++++++++++++");
//		for (String s: filterList)
//			System.out.println(s);
		
		
	}
	
	public static void main(String[] args) throws IOException {
		
//		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90-rest";
//		String filter = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-90";
//		filter(SRC,filter, dest);
		String dest = "/Users/mingxiao10016/Documents/speech_sphinx/lang_model/eclipse-60";
		partition(dest,(float) 0.6);
	}
}
