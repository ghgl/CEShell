/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;

/**
 *  NameMgr
 *  This controls the policy of how to handle output files. The key policy 
 *  rules are whether to overwrite (clobber) existing files if they exist. 
 *  
 *  <br>If an target output file already exists, then it can either generate 
 *   a random name that is valid, or throw an exception.
 *  
 * <p> 
 *  A document can have zero or more content elements. Each content element
 *  Each content element needs to be saved as a separate file.
 *  The simplest case is: one content element in the document. The default
 *  output filename is the same as the document name in the output directory.
 *  <p>
 *  There may already be an existing file with the same name as the output name.
 *  There are two parameters to the rule:
 *  <br>
 *  	<ol>
 *  	<li> dontOverwrite:  default to true. 
 *  			<br>
 *               true: If the output already exists, don't overwrite.
 *               <br>
 *               false: overwrite if it exists
 *      <li> createUniqeName: default to true
 *      	<br> true: if the file already exists, create a unique name
 *      	<br> false: do not create a name. Use the designated name.
 *      </ol>
 *    <p>
 *    If an export is attempted and the filename already exists, then the following
 *    shows the decision tree:
 *     <pre>
 *     
 *     dontOverwite   createUnique   outcome
 *      false          true or false  overwrite the file
 *      true           true           create a new valid name.
 *      true           false          throw exception
 *     		
 *      </pre>      
 *  
 *
 * @author GaryRegier
 * @date   Jul 2, 2011
 */
class NameMgr {
	private boolean createUniqueName = true;
	private boolean dontOverwrite = true;
	private File outputDir = new File(System.getProperty("java.io.tmpdir"));;
	private String docName;
	private String docBaseName;
	private String docExt;
	private int cntNamesCreated = 0;
	private long rndSeed = -1;
	
	/**
	 * Output directory defaults to the java.io.tmpdir
	 */
	
	
	public NameMgr(File outputDir, String docName) {
		this.setOutputDir(outputDir);
		this.setDocName(docName);
	}
	
	public NameMgr(File outputDir, String docName, Boolean noClobber, Boolean generateUniqueNames) {
		this(outputDir, docName);
		this.setDontOverwrite(noClobber);
		this.setCreateUniqueName(generateUniqueNames);
	}
	
	public boolean isCreateUniqueName() {
		return createUniqueName;
	}

	public void setCreateUniqueName(boolean createUniqueName) {
		this.createUniqueName = createUniqueName;
	}

	public boolean isDontOverwrite() {
		return dontOverwrite;
	}



	public void setDontOverwrite(boolean dontOverwrite) {
		this.dontOverwrite = dontOverwrite;
	}



	private void setDocName(String docName) {
		this.docName = docName;
		int extPos = -1;
		
		extPos = docName.lastIndexOf(".");
		if (extPos != -1) {
			docBaseName = docName.substring(0, extPos);
			docExt = docName.substring(extPos);
		} else {
			docBaseName = docName;
			docExt = "bin";
		}
	}
	
	private void setOutputDir(File outputDir) {
		if (! outputDir.exists() && outputDir.canWrite()) {
			throw new IllegalArgumentException("Output dir does not exist or can not write: " + outputDir.toString());
		}
		this.outputDir = outputDir;
	}
	
	public File createFile(String retrievalName) {
		return new File(this.outputDir, retrievalName);
	}

	/**
	 *  this is seriously u*g*l*y* !!
	 * @param totalElements
	 * @return
	 */
	public File[] createNames(int totalElements) throws Exception {
		File[] files = new File[totalElements];
		if (totalElements <= 0) {
			throw new IllegalArgumentException("Total elments needs to be greater than or equal to 1");
		}
		if (totalElements == 1) {
			File outputFile = new File(this.outputDir, this.docName);
			if (outputFile.exists()) {
				if (dontOverwrite)
					if (createUniqueName) {
						outputFile = createUniqueFile();
					}else {
						throw new Exception("Output file already exists: " + outputFile.toString());
					}
				}
			files[0] = outputFile;
		} else {
			for (int i = 0; i < totalElements; i++) {
				String elementBaseName = docBaseName + "_" + i;
				String elementName = elementBaseName +  docExt;
				File outputFile = new File(outputDir, elementName);
				if (outputFile.exists()) {
					if (dontOverwrite)
						if (createUniqueName) {
							outputFile = createUniqueFile();
						} else {
							throw new Exception("Output file already exists: " + outputFile.toString());
						}
					}
				files[i] = outputFile;
			}
		}
		
		return files;
	}
	
	/**
	 * @param localName
	 * @param pos
	 * @return
	 */
	private File createUniqueFile() {
		File uniqueFile = null;
		String newBaseName = this.docBaseName + "_" + getRandomSeed() + "_" + this.cntNamesCreated++;
		String fullName = newBaseName + this.docExt;
		
		uniqueFile = new File(outputDir, fullName);
		return uniqueFile;
	}
	
	private long getRandomSeed()	 {
		if (rndSeed == -1) {
			rndSeed = System.currentTimeMillis();
		}
		return rndSeed;
	}
}
