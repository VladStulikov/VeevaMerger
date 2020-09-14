package com.veeva.RecordMerger.InputReaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.veeva.RecordMerger.annotations.EnableReader;

import au.com.bytecode.opencsv.CSVReader;

@EnableReader
public class CSVFileInputReader extends InputReader {
	
	private String idColumnName = "ID";

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	@Override
	public void parseInput(File input) throws IllegalArgumentException, IOException {
				
		try (
			CSVReader csvReader = new CSVReader (new InputStreamReader(new FileInputStream(input)));
		) {
			String[] row;
			boolean isFirstIteration = true;
			ArrayList<String> header = new ArrayList<String>();
			Integer iIDColumnPosition = null;		
			
			while ((row = csvReader.readNext()) != null) {
			    if (isFirstIteration) {
			    	for (int i = 0; i < row.length; i++) {
			    		String cellContent = row[i];
			    		
						if (cellContent.isEmpty())
							throw new IllegalArgumentException("Header row is invalid: no column names");
						
						if (cellContent.equals(idColumnName)) {
							if (iIDColumnPosition != null)
								throw new IllegalArgumentException("Header row is invalid: ID column should be unique");
							else
								iIDColumnPosition = i;
						}
						header.add(cellContent);
			    	}
			    	isFirstIteration = false;
					
			    	if (iIDColumnPosition == null)
						throw new IllegalArgumentException(idColumnName + " column is not found");		    				    	
			    }
			    else {
				    for (int i = 0; i < row.length; i++)
				    	if (i != iIDColumnPosition)
				    		results.put(row[iIDColumnPosition], header.get(i), row[i]);
			    }    
			}			
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String[] getSupportedExtensions() {
		return new String[] {"csv"};
	}

}
