import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * 
 * @author j.nyffeler
 *
 */
public class AnalyzeATK32 extends Analyze{
	private static final Logger log = Logger.getLogger( SequenceSynchronizer.class.getName() );
	String revision;
	String localRep;
	String revisionRegex;
	ArrayList<String> sequenceNumbers;
	FileHandler file;
	DBHandler db;
	public AnalyzeATK32(){
		file = new FileHandler();
	}
	
	public void start() {
		/**
		 * Start checking out the logs from the repository defined
		 */
		FileHandlerProperty props = new FileHandlerProperty();
		revision = props.getFileProperty("Revision");
		localRep = props.getFileProperty("FileLocation");
		revisionRegex = props.getFileProperty("RevisionRegex");
		db = new DBHandler();
		verifyLatestSequencesFromSource();
	}
	/**
	 * This method uses the checkout directly from the repository. If NO Windows credentials are necessay
	 * one can use this method. 
	 */
	private void verifyLatestSequencesFromSource(){
		log.log(Level.INFO,"Get all FilePaths");
		ResultSet pathes = db.GetPathsOfFiles();
		try {
			Hashtable<String, String> files = null;
			String folder = "";
			while((pathes != null) && (pathes.next())) {
				if(pathes.getString(1) != null && pathes.getString(2) != null && pathes.getString(3) != null) { // ID // FOLDER
					if (!pathes.getString(2).equals(folder)) {
						folder = pathes.getString(2); 
						log.log(Level.INFO,"Load folder " + folder);
						files = getLatestSequencesFromFolder(pathes.getString(2));
					}
					if (files.containsValue(pathes.getString(4)+".SEQ")) {
						log.log(Level.INFO,"FilePath is ok " + folder +" "+ pathes.getString(4));
					}else {
						log.log(Level.INFO,"NOT TRACKED : Newer version found on files system of defined suquence " + pathes.getString(4)+ " in folder "+ folder );
						System.out.println("NOT TRACKED : Newer version found on files system of defined suquence " + pathes.getString(4)+ " in folder "+ folder );
					}
					}
				}
			}catch(SQLException e) {
				log.log(Level.SEVERE,"verifyLatestSequencesFromSource SQL Exception catched " + e.getMessage());
			}
		}

	  /**
	   * This method returns the latest revision from the show log command
	   */
	  public String getLatestRevision() {
		  return "";
	  }
		/**
		 * Gives the latest version number from a set of sequences 
		 * @param System
		 * @param SequenceNumber
		 * @return
		 */
		private Hashtable<String, String> getLatestSequencesFromFolder(String Folder) {
			String path = file.getFileLocation() +"/"+ Folder+"/";
			File rep = new File(path);
			File[] list = rep.listFiles();
			rep = null;
			ArrayList<String> filenames = new ArrayList<String>();
			for ( int i = 0; i < list.length; i++) {
				if (list[i].getName().contains("SEQ") && list[i].getName().contains(".SEQ"))
			    filenames.add(list[i].getName());
			}
			log.log(Level.INFO,"Number of files found:  " + filenames.size());
			Collections.sort(filenames.subList(0, filenames.size()));
			
			Hashtable<String, String> result = new Hashtable<String, String>();
			for ( int i = 0; i < filenames.size(); i++) {
				String sequenceNumb = getSequenceNumberFromFileName(filenames.get(i));
				String latestVersion = getLatestSequenceVersion(list, sequenceNumb);
				if(!result.contains(latestVersion)) {
					result.put(getSequenceNumberFromFileName(latestVersion), latestVersion);
				}
			}
			return result;
		}
		/**
		 * Returns the sequence number from a sequence file name
		 * @param SequenceFileName
		 * @return
		 */
		private String getSequenceNumberFromFileName(String SequenceFileName) {
			return SequenceFileName.substring(SequenceFileName.indexOf("Q")+1, SequenceFileName.indexOf("X")-1);
		}
		/**
		 * Gives the latest version number from a set of sequences 
		 * @param System
		 * @param SequenceNumber
		 * @return
		 */
		private String getLatestSequenceVersion(File[] list, String SequenceNumber) {
			ArrayList<String> filenames = new ArrayList<String>();
			for ( int i = 0; i < list.length; i++) {
				if (list[i].getName().contains("SEQ"+SequenceNumber+"_X") && list[i].getName().contains(".SEQ"))
			    filenames.add(list[i].getName());
			}
			Collections.sort(filenames.subList(0, filenames.size()));
			String version = filenames.get(filenames.size()-1);
			return version;
		}
}