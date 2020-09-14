package com.veeva.RecordMerger.OutputWriters;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.TreeBasedTable;

public abstract class OutputWriter {
	
	protected TreeBasedTable<String, String, String> results;
	
	public TreeBasedTable<String, String, String> getResults() {
		return results;
	}

	public void setResults(TreeBasedTable<String, String, String> results) {
		this.results = results;
	}

	public abstract String[] getSupportedExtensions();
	
	public abstract void printOutput (File output) throws IllegalArgumentException, IOException;

}
