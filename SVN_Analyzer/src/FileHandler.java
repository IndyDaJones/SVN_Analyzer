import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileHandler {
	private static final Logger log = Logger.getLogger( Analyzer.class.getName() );
	FileHandlerProperty props;
	Hashtable exceldata;
	/**
	 * Constructor
	 */
	public FileHandler(){
		props = new FileHandlerProperty();
		/*try {
			exceldata = readXLSXFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	/**
	 * Returns the selection of Selection parameters
	 */
	public String getFileLocation() {
		props = new FileHandlerProperty();
		return props.getFileProperty("FileLocation");
	}
	/**
	 * Returns the the location of the sources folder
	 */
	public String getSourceFolderLocation() {
		props = new FileHandlerProperty();
		return props.getFileProperty("SystemSrc");
	}
	/**
	 * Returns the the location of the includes folder
	 */
	public String getIncludesFolderLocation() {
		props = new FileHandlerProperty();
		return props.getFileProperty("SystemIncl");
	}
	/**
	 * Returns the the location of the includes folder
	 */
	public String getExcelFileLocation() {
		props = new FileHandlerProperty();
		return props.getFileProperty("ExcelPath");
	}
	/**
	 * 
	 */
	public String getNumberofWorksheets() {
		props = new FileHandlerProperty();
		return props.getFileProperty("NumWorkSheet");
	}
	/**
	 * Returns the selection of Selection parameters
	 */
	public Hashtable getFileContent() {
		
		try (BufferedReader br = new BufferedReader(new FileReader(props.getFileProperty("FileLocation")))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	System.out.println(line);
		    	//line.split();
		    	System.out.println("Index tracked "+line.lastIndexOf("\\"));
		    	while (line.lastIndexOf("\\") != -1) {
		    		//log.log(Level.INFO,"Data parsed: " + line.substring(line.lastIndexOf("\\")+1, line.length()));
		    		System.out.println(line.substring(line.lastIndexOf("\\")+1, line.length()));
		    		line = line.substring(0, line.lastIndexOf("\\"));
		    		
		    		//Kann geprüft werden: CSL.M99.ATKP.Main\F19_Systems\S01_XMM_VorViren\F10_Sources\1212_TA_OpHandzugabe.JSEQ
		    		// Wenn er CSL.M99.ATKP.Main trackt, kann raus gesprungen werden!
		    		//Danach auf DB Link testen gehen und Resultat geniessen!£
	    		} 
		    }
		    return null;
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Excel
	 */
	public Hashtable readXLSXFile() throws IOException
    {
		Hashtable result = new Hashtable<>();
        InputStream ExcelFileToRead = new FileInputStream(getExcelFileLocation());
        XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
        XSSFWorkbook test = new XSSFWorkbook();
        for (int i = 0; i < Integer.parseInt(getNumberofWorksheets()); i++) {
	        // Load worksheet!
        	XSSFSheet sheet = wb.getSheetAt(i);
	        XSSFRow row; 
	        XSSFCell cell;
	        Iterator rows = sheet.rowIterator();
	        int x = 1;
	        while (rows.hasNext())
	        {
	            row=(XSSFRow) rows.next();
	            Iterator cells = row.cellIterator();
	            String rowResult = "";
	            while (cells.hasNext())
	            {
	            	cell=(XSSFCell) cells.next();
	
	                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING && ! cell.getStringCellValue().equals(""))
	                {
	                    String value = cell.getStringCellValue();
   	                    if (!value.startsWith("C:\\Users\\") && !value.startsWith("X") && !value.startsWith("[") && !value.contains(" ")&& !value.contains(":")&& !value.contains("%") && !value.contains(")")&& !value.contains(".") && !value.contains("Kategorie")&& !value.contains("Bemerkung") && !value.contains("Content") && !value.contains("System")&& !value.contains("Kontrolle") ) {
	   	                    	rowResult = rowResult+value+"|";
	                    }
	                }
	                else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && Double.toString(cell.getNumericCellValue()) != "")
	                {
	                	Double value = cell.getNumericCellValue();
	                	rowResult = rowResult+value.intValue()+"|";
	                }
	                else {
	                	//do nothing 
	                }
	            }
	            if (!rowResult.isEmpty()) {
	            	System.out.println("Output : "+x+	" 			"	+rowResult);
	            	x++;
	            }
	            while (rowResult.indexOf("|") != -1 && !rowResult.isEmpty())
                {
	            	String Sequence = "";
	            	String SeqNum = "";
	            	String SeqName ="";
	            	log.log(Level.INFO,rowResult);
	            	if (rowResult.indexOf("|") != -1 && rowResult.indexOf("|")+1 != rowResult.length()){
	            		Sequence = rowResult.substring(0, rowResult.indexOf("|")); 
	                	rowResult = rowResult.substring(rowResult.indexOf("|")+1,rowResult.length());
	                	log.log(Level.INFO,rowResult);	
	            	}
	            	if(rowResult.indexOf("|") != -1 && rowResult.indexOf("|")+1 != rowResult.length()) {
                	SeqNum =rowResult.substring(0, rowResult.indexOf("|"));
                	if((SeqNum.matches("\\b[0-9][0-9]{1,3}\\b"))) {
                			rowResult = rowResult.substring(rowResult.indexOf("|")+1,rowResult.length());
                			log.log(Level.INFO,"SeqNum "+SeqNum);
                		}
                	}
                	if(rowResult.indexOf("|") != -1 ) {
                		SeqName = rowResult.substring(0, rowResult.indexOf("|"));
                		rowResult =rowResult.substring(rowResult.indexOf("|")+1,rowResult.length());
                		log.log(Level.INFO,"SeqName "+ SeqName);
                	}
                	result.put(Sequence+SeqNum+SeqName, "1");
                }
	        }
	        int y = 1;
	        Set<String> keys = result.keySet();
    		for(String key: keys){
    			System.out.println("Nummer: "+ y + "	Key: "+ key + "						Value: "+result.get(key));
    			y++;
    		}
        }
        return result;
    }
}
