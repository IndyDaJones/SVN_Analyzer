import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Analyze {
	private static final Logger log = Logger.getLogger( Analyzer.class.getName() );
	FileHandler file;
	DBHandler db;
	/**
	 * Base method which will be overwritten by the specific Analyze class
	 */
	public abstract void start();
	public abstract String getLatestRevision();
	
	public void analyzeData(Hashtable paths) {
		file = new FileHandler();
		db = new DBHandler();
		Hashtable notTracked = new Hashtable<>();
		Set<Integer> keys = paths.keySet();
		for(Integer key: keys){
			if(paths.get(key) != null && paths.get(key).toString().indexOf(".") != -1) {
				String path = paths.get(key).toString();
				String format = path.substring(path.indexOf(".")+1, path.length());
				path = path.substring(0, path.indexOf("."));
				if (format.equals("JSeq") || format.equals("JInc")) {
					while (path.indexOf("/") != -1) {
			    		String folder = path.substring(0, path.indexOf("/"));
			    		
			    		if (folder.equals(file.getSourceFolderLocation())||folder.equals(file.getIncludesFolderLocation())) {
			    			log.log(Level.INFO,"FolderPath is " + folder);
			    			if (path.indexOf("/") != -1) path = path.substring(path.indexOf("/")+1, path.length());
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
								log.log(Level.INFO,"Sequence not defined: " + paths.get(key).toString());
								notTracked.put(notTracked.size()+1, paths.get(key).toString());
								//db.trackSequence(paths.get(key).toString());
							}
						}
					}else {
						// Includes
						if (!db.checkSequence(sequence)) {
							log.log(Level.INFO,"Include not defined: " + paths.get(key).toString());
							//db.trackSequence(paths.getString(2));
							notTracked.put(notTracked.size()+1, paths.get(key).toString());
						}
					}
				}
			}
		}
		printout(notTracked);
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
	 * Prints out the content of the hashtable to the command line
	 * @param result
	 */
	private void printout(Hashtable result) {
		if (result.size()>0) {
			System.out.println("");
			System.out.println("Latest Revision: "+getLatestRevision());
			System.out.println("##################### NOT TRACKED ######################");
			for (int i= 1 ; i <= result.size(); i++) {
				//log.log(Level.INFO,"Undefined Sequence or Include: " + notTracked.get(i));
				System.out.println(result.get(i));
			}
			System.out.println("##################### NOT TRACKED ######################");
			System.out.println("");
		}else {
			System.out.println("");
			System.out.println("Latest Revision: "+getLatestRevision());
			System.out.println("##################### ALL SEQUENCE TRACKED ######################");
			System.out.println("#################################################################");
		}
	}
}
