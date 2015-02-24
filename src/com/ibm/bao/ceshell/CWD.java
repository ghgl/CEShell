package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;

/**
 * CWD has responsibility for keeping a data structure of the current 
 * working directory. It has utility functions for turning partial paths 
 * into full paths.
 * 
 * @author GaryRegier
 *
 */
public class CWD {
	
	public static final String 
		PATH_DELIM = "/",
		PARENT_DIR = "..",
		CURRENT_DIR = ".",
		ROOT_DIR = "/";
	
	private List<String> workingDir;
	private CEShell shell;
	
	public CWD() {
		workingDir = new ArrayList<String>();
	}
	
	public CWD(String pathFromTop) {
		this();
		workingDir = parsePath(pathFromTop);
	}
	
	public CEShell getShell() {
		return shell;
	}

	public void setShell(CEShell shell) {
		this.shell = shell;
	}

	public String pwd() {
		return pathToString(workingDir);
	}
	
	public boolean isRootDir(String path) {
		return (ROOT_DIR.equals(path));
	}
	
	/**
	 * Return the name portion of a file path
	 * <pre>
	 * This will be the portion of the path following the path delimiter
	 *   /foo/baz/bar       will return bar
	 *   ../bar             will return bar
	 *   bar                will return bar
	 *  </pre
	 *  
	 * @param path  String of length of one character or more.
	 * @return String path portion of the full path
	 */
	public String getName(String path) {
		List<String> entries = parsePath(path);
		if (entries.size() == 0) {
			throw new IllegalArgumentException("No name in path");
		}
		return entries.get(entries.size() - 1).toString();
	}
	
	/**
	 * Remove the name portion from the fullPath including the last delimiter.
	 * If the last character passed in is the path delimiter, then the entire
	 * path is returned
	 * <pre>
	 *       /foo/baz/bar       returns /foo/baz     (/bar removed) 
	 *       /foo/baz/bar/      returns /foo/baz/bar (no name portion following the delimiter
	 *       /bar               returns /
	 *       /                  returns /
	 *       x                  throws IllegalArgumentException (should start with path delimiter)
	 *  </code>
	 * 
	 * If the path is to the root directory, then the <code>
	 * isRootDirectory()</code> method will return true.
	 * 
	 *  The methods getName() and getPath() compliment each other. 
	 *  Given a path, calling these two methods should return different parts 
	 *  of the same path. These parts can be recombined with the path delimiter
	 * 
	 * @param fullPath
	 * @return String the path portion of the name, 
	 * @return ROOT_DIR if it is the root
	 * 
	 */
	public String getPath(String fullPath) {
		if (fullPath == null) {
			throw new IllegalArgumentException("null value in getPath()");
		}
		if (! fullPath.startsWith(PATH_DELIM)){
				throw new IllegalArgumentException(
						"Expected fullPath to start with " + PATH_DELIM);
		
		}
		if (fullPath.length() == 1) {
			return ROOT_DIR; 
		}
		if (fullPath.endsWith(PATH_DELIM)) {
			return fullPath;
		}
		int pos = fullPath.lastIndexOf(PATH_DELIM);
		if (pos == 0) {
			return ROOT_DIR;
		}
		return fullPath.substring(0, pos);
	}

	/**
	 * cd
	 * Change Directory to a new directory.
	 *  
	 * Work on a copy in case an error happens, the original can be restored 
	 * without side effects
	 *
	 * @param path
	 * @throws Exception if the path does not exist.
	 */
	public String cd(String path) throws Exception {
		List<String> candidateWorkingDir = null;
		String candidateFullPath = null;
		if (path.startsWith(PATH_DELIM)) {
			candidateWorkingDir = parsePath(path);
		} else {
			List<String> relativePathList = parsePath(path);
			
			List<String> workingCopy = new ArrayList<String>(workingDir);
			candidateWorkingDir = mergeLists(workingCopy, relativePathList);
		}
		candidateFullPath = pathToString(candidateWorkingDir);
		
		if (! isInCurrentPath(candidateFullPath)) {	
			if (! dirExists(candidateFullPath)) {
				// use the value passed in in the error msg
				throw new Exception("Path does not exist: " + path); 
			}
		}
		// OK. New working dir is set
		this.workingDir = candidateWorkingDir;
		return candidateFullPath;
	}
	
	/**
	 * dirExists 
	 * Verify whether a path exists
	 * <p>
	 * 
	 * @param fullPath:String 
	 * @param pathAsList:List
	 * 
	 * @return
	 */
	private boolean dirExists(String fullPath) {
		
		try {
			Folder targetFolder = null;
			PropertyFilter idFilter = new PropertyFilter();
			
			idFilter.setMaxRecursion(0);
			idFilter.addIncludeProperty(
					new FilterElement(null, null, null, "ID", null)); 
			
			targetFolder = Factory.Folder.fetchInstance(
					getShell().getObjectStore(), fullPath, idFilter);
			return (targetFolder != null) ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * isParentPath
	 * In order to avoid making a round-trip to the server, this can 
	 * check whether the path is a parent path of the current working 
     * directory.
	 * 
	 * @param pathAsList:List
	 * @return true if the path exists in the parentList
	 */
	protected boolean isInCurrentPath(String pathAsString) {
		String workingPathStr = pathToString(this.workingDir);
		return (workingPathStr.startsWith(pathAsString));
	}

	public String relativePathToFullPath(String relativePath) {
		String absolutePathStr = null;
		boolean fromParent = (relativePath.startsWith(PATH_DELIM));
		if (! fromParent) {
			List<String> relativePathList = parsePath(relativePath);
			List<String> workingCopy = new ArrayList<String>(workingDir);
			List<String> absolutePath = null;
			absolutePath = mergeLists(workingCopy, relativePathList);
			absolutePathStr = pathToString(absolutePath);
		} else {
			List<String> relativePathList = parsePath(relativePath);
			absolutePathStr = pathToString(relativePathList);
		}
		
		return absolutePathStr;
	}
	
	protected List<String> mergeLists(List<String> workingCopy, 
			List<String> pathList) {
		int workingDepth = workingCopy.size();
		Iterator<String> iter = pathList.iterator();
		while (iter.hasNext()) {
			String nextElement = iter.next();
			if (CURRENT_DIR.equals(nextElement) ) {
				continue;
			}
			if (PARENT_DIR.equals(nextElement)) {
				if (workingCopy.size() == 0) {
					throw new RuntimeException("Illegal path");
				}
				workingCopy.remove(workingDepth - 1);
				workingDepth--;
				continue;
			}
			workingCopy.add(nextElement);
			workingDepth++;
		}
		return workingCopy;
	}

	private List<String> parsePath(String path) {
		StringTokenizer tokenizer = new StringTokenizer(path, PATH_DELIM);
		List<String> pathList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			pathList.add(tokenizer.nextToken());
		}
		
		return pathList;
	}
	
	protected String pathToString(List<String> pathList) {
		String fullPath = "";
		
		if (pathList.isEmpty()) {
			return PATH_DELIM;
		}
		
		for (Iterator<String> it = pathList.iterator(); it.hasNext();) {
			String nextDir = it.next();
			fullPath = fullPath + PATH_DELIM + nextDir;
		}
		return fullPath;
	}
}
