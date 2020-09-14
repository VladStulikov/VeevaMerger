package com.veeva.RecordMerger.InputReaders;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public abstract class InputReader {
	
	protected Table<String, String, String> results;

	public InputReader() {;
		this.results = HashBasedTable.create();
	}	
	
	public Table<String, String, String> getResults() {
		return results;
	}
	
	public abstract String[] getSupportedExtensions();
	
	public abstract void parseInput(File input) throws IllegalArgumentException, IOException;

}
