import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;
/**
 * 
 * @author j.nyffeler
 *
 */
public class AnalyzeRepo extends Analyze{
	private static final Logger log = Logger.getLogger( Analyzer.class.getName() );
	String revision;
	String localRep;
	String revisionRegex;
	String rev = "";
	public AnalyzeRepo(){}
	
	public void start() {
		/**
		 * Start checking out the logs from the repository defined
		 */
		FileHandlerProperty props = new FileHandlerProperty();
		revision = props.getFileProperty("Revision");
		localRep = props.getFileProperty("FileLocation");
		revisionRegex = props.getFileProperty("RevisionRegex");
		
		//CheckoutExcel();
		checkLogFromRepositoryCMD();
		//CheckoutRepository();
		//svnLogTest();
	}
	private void CheckoutExcel() {
		file = new FileHandler();
		try {
			Hashtable test = new Hashtable<>();
			test = file.readXLSXFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 log.log(Level.SEVERE,"CheckoutExcel failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void CheckoutRepository(){
			DAVRepositoryFactory.setup( );

	        String url = "";
	        String name = "";
	        String password = "";
	        long startRevision = 0;
	        long endRevision = -1; //HEAD (the latest) revision

	        SVNRepository repository = null;
	        try {
	        	repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
	            @SuppressWarnings("deprecation")
				ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);

	            repository.setAuthenticationManager( authManager );
	            
	            System.out.println("Test Connection..");
	            repository.testConnection();
	            System.out.println("Connection done..");

	            Collection logEntries = null;
	            System.out.println( "Try to connect to repository " );
	            logEntries = repository.log( new String[] { "" } , null , startRevision , endRevision , true , true );
	            System.out.println( "--------------------------------------------6" );
	            for ( Iterator entries = logEntries.iterator( ); entries.hasNext( ); ) {
	                SVNLogEntry logEntry = ( SVNLogEntry ) entries.next( );
	                System.out.println( "---------------------------------------------" );
	                System.out.println ("revision: " + logEntry.getRevision( ) );
	                System.out.println( "author: " + logEntry.getAuthor( ) );
	                System.out.println( "date: " + logEntry.getDate( ) );
	                System.out.println( "log message: " + logEntry.getMessage( ) );
	                System.out.println( "--------------------------------------------7" );
	                if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
	                    System.out.println( );
	                    System.out.println( "changed paths:" );
	                    Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );

	                    for ( Iterator changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
	                        SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
	                        System.out.println( " "
	                                + entryPath.getType( )
	                                + " "
	                                + entryPath.getPath( )
	                                + ( ( entryPath.getCopyPath( ) != null ) ? " (from "
	                                        + entryPath.getCopyPath( ) + " revision "
	                                        + entryPath.getCopyRevision( ) + ")" : "" ) );
	                    }
	                }
	            }
            }catch(SVNException e) {
            	System.out.println( "Exception chatched: " + e.getMessage());
            }
        }
	
	/**
	 * svn log -v -r 1133:BASE
	 * 
	 */
	private void checkLogFromRepositoryCMD(){
		String command = "cd "+localRep+" && svn log -v -r "+revision+":BASE";
		try {
			//updateRevision();
			analyzeData(executeCommand());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE,"checkLogFromRepositoryCMD stopped due to error: : " + e.getMessage());
			System.out.println(e.getMessage());
		}
		//executeCommand(command);
			
	}
	/**
	 * executeCommand
	 * @return
	 */
	  public Hashtable executeCommand(){
		  String command = "";
	        ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", "cd \""+localRep+"\" && svn log -v -r "+revision+":BASE\"");
	        builder.redirectErrorStream(true);
	        Process p;
	        Hashtable result = new Hashtable<>();
			try {
				p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line;
		        int i = 0;
		        List<Integer> deleted = new ArrayList<Integer>();
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            if (line.startsWith("r")) {
		            	// Revision information catched
		            	if (line.indexOf("+") != -1) {
		            		rev = line.substring(0, line.indexOf("+"));
		            	}
		            }
		            log.log(Level.INFO,"SVN log is analyzed according to merge or add command: " + line);
		            if(line.startsWith("   M /")||line.startsWith("   A /")) {
		            	line = line.substring(line.indexOf("/"), line.length());
		            	if(!result.contains(line)) {
		            		result.put(i, line);
		            		i++;
		            	}
		            }
		            log.log(Level.INFO,"SVN log is analyzed according to delete command: " + line);
		            if(line.startsWith("   D /")) {
		            	line = line.substring(line.indexOf("/"), line.length());
		            	if(result.contains(line)) {
		            		Set<Integer> keys = result.keySet();
		            		for(Integer key: keys){
		            			if(result.get(key).toString().equals(line)) {
		            				deleted.add(key);
		            			}
		            		}
		            	}
		            }
		        }
		        if(!deleted.isEmpty()) {
		        	for ( int del = 0 ; del < deleted.size() ; del++) {
		        		log.log(Level.INFO,"Sequence with ID <"+deleted.get(del)+">, path: " + result.get(deleted.get(del))+" removed from list!");
		        		result.remove(deleted.get(del));
		        	}
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.log(Level.SEVERE,"SVN processing stopped due to error: " + e.getMessage());
			}
			int i = 1;
			Set<Integer> keys = result.keySet();
    		for(Integer key: keys){
    			log.log(Level.INFO,"Nummer: "+ i + "	Key: "+ key + "		Value: "+result.get(key));
    			i++;
    		}
	        return result;
	    }
	  /**
	   * This method updates the repository to the newest verison
	   */
	  public void updateRevision(){
		  String command = "";
	        ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", "cd \""+localRep+"\" && svn update\"");
	        builder.redirectErrorStream(true);
	        Process p;
	        try {
				p = builder.start();
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String line;
		        int i = 0;
		        while (true) {
		            line = r.readLine();
		            if (line == null) { break; }
		            log.log(Level.INFO, "Update repo: "+line);
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.log(Level.SEVERE,"Include not defined: " + e.getMessage());
			}
	    }
	  /**
	   * This method returns the latest revision from the show log command
	   */
	  public String getLatestRevision() {
		  return rev;
	  }
	/**
	 * 
	 */
	public static void svnLogTest() {
	    final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
	    final SvnLog log = svnOperationFactory.createLog();
	    SVNURL url = null;
	    try {
	        url = SVNURL
	                .parseURIEncoded("");
	    } catch (SVNException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    log.setSingleTarget(SvnTarget.fromURL(url));
	    log.addRange(SvnRevisionRange.create(SVNRevision.create(111),
	            SVNRevision.create(222)));
	    log.getRevisionRanges();
	    SVNLogEntry logEntry = null;
	    try {
	        logEntry  = log.run();
	        System.out.println(logEntry.getAuthor() + " " + logEntry.getRevision() + " " 
	                + logEntry.getDate());

	    } catch (SVNException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
}