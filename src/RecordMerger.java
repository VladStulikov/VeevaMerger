import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.reflections.Reflections;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import com.veeva.RecordMerger.InputReaders.InputReader;
import com.veeva.RecordMerger.OutputWriters.CSVFileWriter;
import com.veeva.RecordMerger.OutputWriters.OutputWriter;
import com.veeva.RecordMerger.annotations.EnableReader;

public class RecordMerger {

	public static final String FILENAME_COMBINED = "combined.csv";
	public static final String DATAFILES_DIR = "data/";
	public static final String READERS_PACKAGE_NAME = "com.veeva.RecordMerger.InputReaders";
	
	private static Map<String, InputReader> inputReaders = new HashMap<String, InputReader>();
	private static OutputWriter outputWriter = new CSVFileWriter(); 
	
	private static void initializeReaders() throws Exception {
		Reflections reflections = new Reflections(READERS_PACKAGE_NAME);
		Set<Class<?>> readersClasses = reflections.getTypesAnnotatedWith(EnableReader.class);
		
		for (Class<?> clazz: readersClasses) {			
			InputReader reader = (InputReader) clazz.getDeclaredConstructor().newInstance();
			String[] supportedFileExtensions = reader.getSupportedExtensions();
			for (String supportedExtension : supportedFileExtensions) {
				if (inputReaders.containsKey(supportedExtension))
					throw new Exception("File extension " + supportedExtension + " is not unique amongst Input File Readers, exiting");
				else
					inputReaders.put(supportedExtension, reader);
			}		
		}
	}
			
	/**
	 * Entry point of this test.
	 *
	 * @param args command line arguments: first.html and second.csv.
	 * @throws Exception bad things had happened.
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("Usage: java RecordMerger file1 [ file2 [...] ]");
			System.exit(1);
		}

		initializeReaders();
		
		TreeBasedTable<String,String,String> resultTable = TreeBasedTable.create();

		for (int i = 0; i < args.length; i++) {
			
			String fileName = args[i];
			String fileNameExtension = FilenameUtils.getExtension(fileName);
			
			if (fileNameExtension.isEmpty())
				System.out.println("File " + fileName + " skipped as it has no extension");
			
			if (inputReaders.containsKey(fileNameExtension)) {
				InputReader reader = inputReaders.get(fileNameExtension);
				
				File file = new File(DATAFILES_DIR + fileName);
				
				if (!file.exists())
					System.out.println("File " + fileName + " does not exist");	
				else {
					try {
						reader.parseInput(file);
					} catch (IllegalArgumentException e) {
						System.out.println("File " + fileName + " has incorrect structure: " + e.getMessage());
						return;
					} catch (IOException e) {
						System.out.println("File " + fileName + " read error: " + e.getMessage());
						return;
					}
					
					for (Table.Cell<String, String, String> cell : reader.getResults().cellSet()) {
						
						String rowKey = cell.getRowKey();
						String columnKey = cell.getColumnKey();
						String value = cell.getValue();
						
						if (resultTable.contains(rowKey, columnKey) && !value.equals(resultTable.get(rowKey, columnKey)))
							System.out.println("Column " + columnKey + " for record ID " + rowKey + " has different values in the files");
						else
							resultTable.put(rowKey, columnKey, value);
					}
				}	
			} else
				System.out.println("File " + fileName + " was skipped as its extension was not recognised");
		}
		
		String fullFileName = DATAFILES_DIR + FILENAME_COMBINED;
		
		File output = new File(fullFileName);
		outputWriter.setResults(resultTable);
		outputWriter.printOutput(output);
		
		System.out.println("Files merged into " + fullFileName);
	}
}
