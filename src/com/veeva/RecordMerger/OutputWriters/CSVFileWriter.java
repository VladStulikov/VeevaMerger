package com.veeva.RecordMerger.OutputWriters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVFileWriter extends OutputWriter {

	private static final char SEPARATOR = ',';
	
	private String idColumnName = "ID";
	
	@Override
	public String[] getSupportedExtensions() {
		return new String[] {"csv"};
	}

	@Override
	public void printOutput(File output) throws IllegalArgumentException, IOException {
		FileWriter writer = new FileWriter(output);
		CSVWriter csvWriter = new CSVWriter(writer, SEPARATOR);
		
		Set<String> columns = results.columnKeySet();
		
		List<String> firstRow = new ArrayList<>();
	    firstRow.add(idColumnName);
	    firstRow.addAll(columns);
	    csvWriter.writeNext(firstRow.toArray(new String[0]));
	     
	    SortedMap<String,Map<String,String>> rows = results.rowMap();
	     
	    for (String key : rows.keySet()) {
	    	Map<String,String> row = rows.get(key);
	    	List<String> csvRow = new ArrayList<>();
	    	csvRow.add(key);
	    	for (String columnName : columns) {
	    		String cell = row.get(columnName);
	    		csvRow.add(cell == null ? "" : cell);
	    	}
	    	csvWriter.writeNext(csvRow.toArray(new String[0]));
	    }
	      
	    csvWriter.flush();
	    csvWriter.close();
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

}
