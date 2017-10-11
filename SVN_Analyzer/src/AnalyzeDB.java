import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author j.nyffeler
 *
 */
public class AnalyzeDB extends Analyze{
	private static final Logger log = Logger.getLogger( Analyzer.class.getName() );
	DBHandler db;
	FileHandler file;
	public AnalyzeDB(){
		file = new FileHandler();
	}
	
	public void start() {
		log.log(Level.INFO,"create DBHandler");
		db = new DBHandler();
		log.log(Level.INFO,"DBHandler created");
		CheckoutDBEntries();
	}
	/**
	 * This sequence completes the sequence number with leading 0s.
	 * @param SequenceNumber
	 * @return Sequence number with corresponding leading 0s.
	 */
	private String completeSequenceNumber(String SequenceNumber) {
		while(SequenceNumber.indexOf("0") != -1) {
			SequenceNumber = SequenceNumber.substring(SequenceNumber.indexOf("0")+1, SequenceNumber.length());
			if(!SequenceNumber.startsWith("0")) {
				break;
			}
		}
		return SequenceNumber;
	}
	/**
	 * 
	 */
	private void CheckoutDBEntries(){
		log.log(Level.INFO,"call db.GetSVNPathesFromDB()");
		ResultSet paths = db.GetSVNPathesFromDB();
		Hashtable notTracked = new Hashtable<>();
		try {
			while((paths != null) && (paths.next())) {
				if(paths.getString(1) != null && paths.getString(2) != null) {
					String id = paths.getString(1);
					String path = paths.getString(2);
					String format = path.substring(path.indexOf(".")+1, path.length());
					path = path.substring(0, path.indexOf("."));
					if (format.equals("JSeq") || format.equals("JInc")) {
						while (path.indexOf("/") != -1) {
				    		String folder = path.substring(0, path.indexOf("/"));
				    		
				    		if (folder.equals(file.getSourceFolderLocation())||folder.equals(file.getIncludesFolderLocation())) {
				    			log.log(Level.INFO,"FolderPath is " + folder);
				    			path = path.substring(path.indexOf("/")+1, path.length());
				    			break;
				    		}
				    		path = path.substring(path.indexOf("/")+1, path.length());
				    		log.log(Level.INFO,"FilePath is " + path);
						}
						log.log(Level.INFO,"Let's go on with: " + path);
						String system = "";
						String sequence ="";
						String seqNum = "";
						if (path.indexOf("/") != -1){
							system = path.substring(0, path.indexOf("/"));
							path = path.substring(path.indexOf("/")+1, path.length());
							sequence = path.substring(path.indexOf("/")+1, path.length());
						}else {
							sequence = path;
						}
						if (system.equals("")) {
							system = file.getIncludesFolderLocation();
						}
						log.log(Level.INFO,"System: " + system);
						log.log(Level.INFO,"Sequence: " + sequence);
						if((sequence.indexOf("_") != -1) && !system.equals(file.getIncludesFolderLocation())) {
							seqNum = completeSequenceNumber(sequence.substring(0, sequence.indexOf("_")));
							sequence = sequence.substring(sequence.indexOf("_")+1, sequence.length());
							if(!system.equals("")&&!seqNum.equals("")&&!sequence.equals("")) {
								if (!db.checkSequence(system, seqNum, sequence)) {
									log.log(Level.INFO,"Sequence not defined: " + paths.getString(2));
									System.out.println("Sequence not defined: "+paths.getString(2));
									notTracked.put(notTracked.size()+1, paths.getString(2));
									//db.trackSequence(paths.getString(2));
								}
							}
						}else {
							// Includes
							if (!db.checkSequence(sequence)) {
								log.log(Level.INFO,"Include not defined: " + paths.getString(2));
								//db.trackSequence(paths.getString(2));
								notTracked.put(notTracked.size()+1, paths.getString(2));
							}
						}
					}
				}
			}
			for (int i= 1 ; i <= notTracked.size(); i++) {
				System.out.println(notTracked.get(i));
			}
			// Prnt
		} catch (SQLException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
			paths = null;
		}
	}
	public String getLatestRevision() {
		return "nor Tracked";
	}
	}