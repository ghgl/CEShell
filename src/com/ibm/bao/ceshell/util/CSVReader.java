/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.ibm.bao.ceshell.impl.BulkImportBatch;

/**
 *  CSVReader
 *
 * @author regier
 * @date   Sep 8, 2011
 */
public class CSVReader {
	
	private BulkImportBatch batch;
	
	public CSVReader(BulkImportBatch bulkImportInfo) {
		this.batch = bulkImportInfo;
	}
	
	public List<List<String>> load() throws Exception  {
		BufferedReader reader = new BufferedReader(new FileReader(batch.getSrcDataFile()));
		List<List<String>> results = new ArrayList<List<String>>();
		CSV parser = new CSV(batch.getConfigInfo().getSeparatorChar());
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			List<String> fields = parser.parse(line);
			results.add(fields);
		}
		return results;
	}
}
