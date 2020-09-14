package com.veeva.RecordMerger.InputReaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.veeva.RecordMerger.annotations.EnableReader;

@EnableReader
public class HTMLFileInputReader extends InputReader {

	private static final String FILE_ENCODING = "UTF-8";
	private static final String BASE_URI = "";
	
	private String tableElementID = "directory";
	private String idColumnName = "ID";
	
	@Override
	public void parseInput(File input) throws IllegalArgumentException, IOException {
		
		Document doc = Jsoup.parse(input, FILE_ENCODING, BASE_URI);
		Element tableElement = doc.getElementById(tableElementID);
		
		if (tableElement == null)
			throw new IllegalArgumentException("Cannot find HTML tag with ID " + tableElementID);		
		
		Elements rows = tableElement.select("tr");
		
		Element firstRow = rows.get(0);
		Elements headerColumns = firstRow.select("th");
		
		ArrayList<String> header = new ArrayList<String>();
		Integer iIDColumnPosition = null;
		
		for (int i = 0; i < headerColumns.size(); i++) {
			
			String cellContent = headerColumns.get(i).text();
			
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
		
    	if (iIDColumnPosition == null)
			throw new IllegalArgumentException(idColumnName + " column is not found");		
		
		for (int i = 1; i < rows.size(); i++) {
		    Element row = rows.get(i);
		    Elements cols = row.select("td");
		    String sID = cols.get(iIDColumnPosition).text();	
		    
		    for(int j = 0; j < cols.size(); j++)
		    	if (j != iIDColumnPosition)
		    		results.put(sID, header.get(j), cols.get(j).text());		    
		}
	}

	public String getTableElementID() {
		return tableElementID;
	}

	public void setTableElementID(String tableElementID) {
		this.tableElementID = tableElementID;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	@Override
	public String[] getSupportedExtensions() {
		return new String[] {"html"};
	}

}
