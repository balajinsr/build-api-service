package com.broadcom.nbiapps.util;


import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;

import java.util.*;
 
/**
 * @author sandipbose 
 * 
 * Code that finds files that
 * match the specified glob pattern.
 * The file or directories that match
 * the pattern are sent back to the caller as a List.
 * 
 * For more information on what
 * constitutes a glob pattern, see
 * http://docs.oracle.com/javase/javatutorials/tutorial/essential/io/fileOps.html#glob
 *
 * To be used only to search a filesystem's.
 * Please check the commented out main() method for implementation details
 *
 */
 
public class FileFinder extends SimpleFileVisitor<Path> {
 
	/**
	 * A {@code FileVisitor} that finds
	 * all files that match the
	 * specified pattern.
	 */
	private final PathMatcher matcher;
	private String startingDirectory;
	private List<String> ignoredDirectories = null;
	private List<String> matchedContents = null;

	 
	/**
	 * Search for a given glob pattern
	 * @param startingDirectory
	 * @param pattern
	 */
	FileFinder(String startingDirectory, String pattern) {
	    this.startingDirectory = startingDirectory;
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	    matchedContents = new ArrayList<String>();
	}
	
	/**
	 * Search for a given glob pattern excluding the given list directories 
	 * @param startingDirectory
	 * @param pattern
	 * @param ignoreDirectories
	 */
	FileFinder(String startingDirectory, String pattern, String ignoreDirectories) {
		this.startingDirectory = startingDirectory;
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		matchedContents = new ArrayList<String>();
		ignoredDirectories = Arrays.asList(ignoreDirectories.split(","));
	}
	
	/**
	 * Compares the glob pattern against the file or directory name
	 * @param file
	 */
    void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            matchedContents.add(file.toString());
        }
    }
	 
    /**
     * Returns the List of matched files. 
     * @param absolutePath = true: The files will include absolute path's.
     * 					   = false: The files will include relative path's from the starting directory. 	 
     * @return
     * @throws IOException
     */
	public List<String> getResults(boolean absolutePath) throws IOException {
		Files.walkFileTree(Paths.get(this.startingDirectory), this);
		if (!absolutePath){
			Iterator<String> itr = matchedContents.iterator();
			while (itr.hasNext()){
				String tmpLocation = (String)itr.next();
				matchedContents.set(matchedContents.indexOf(tmpLocation), 
						tmpLocation.replaceAll(this.startingDirectory, ""));
			}
		}
		return matchedContents;
	}
	 
	/**
	 * Invoke the pattern matching method on each file.
	 */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }
	 
	/**
	 * Invoke the pattern matching method on each directory.
	 */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    	if (ignoredDirectories != null && ignoredDirectories.contains(dir.getFileName().toString())){
    		return SKIP_SUBTREE;
    	}else{
    		find(dir);
    		return CONTINUE;
    	}
    }
 
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
}

