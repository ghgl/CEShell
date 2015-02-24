/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *  FileUtil
 *
 * @author regier
 * @date   Feb 17, 2012
 */
public class FileUtil {
	
	public static void store(List<String> data, File outFile) throws Exception{
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outFile));
			for (String line : data) {
				writer.write(line);
				writer.newLine();
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch(Exception ee) {
					// ignore this catch
				}
			}
		}
		writer = null;
	}
	
	public static ArrayList<String> load(File inFile) throws Exception {
		BufferedReader reader = null;
		ArrayList<String> contents = new ArrayList<String>();
		String line = null;
		try {
			reader = new BufferedReader(new FileReader(inFile));
			while ((line = reader.readLine()) != null) {
				contents.add(line);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					// no-op
				}
				reader = null;
			}
		}
		return contents;
	}

}
