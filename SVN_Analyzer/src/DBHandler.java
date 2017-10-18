import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandler {
	private static final Logger log = Logger.getLogger( Analyzer.class.getName() );
	DBConnection conn;
	DBHandlerProperty props;
	/**
	 * Constructor
	 */
	public DBHandler(){
		props = new DBHandlerProperty();
		log.log(Level.INFO,"call initDB");
		initDB();
		log.log(Level.INFO,"Database initiated");
	}
	/**
	 * This method creates a new database instance
	 */
	private void initDB(){
		log.log(Level.INFO,"Try to create DBConnection");
		this.conn = new DBConnection(props.getDBProperty("DatabaseLocation"));
	}
	/**
	 * Return the table name defined in the database.property files
	 * @return
	 */
	public String getTrackTableName() {
		return props.getDBProperty("TrackTable");
	}
	/**
	 * Return the table name defined in the database.property files
	 * @return
	 */
	public String getSourceTableName() {
		return props.getDBProperty("SourceTable");
	}
	/**
	 * Return the table name defined in the database.property files
	 * @return
	 */
	public String getTargetTableName() {
		return props.getDBProperty("TargetTable");
	}
	/**
	 * Returns the defined pathes to the source files
	 */
	public ResultSet GetSVNPathesFromDB() {
		//Statement s;
		int i = 0;
		ResultSet rs = null;
		try {
			Statement s = conn.createStatement();
		    log.log(Level.INFO,"Statement established");
		    // Fetch table
		    String selection = "SELECT ID, Path FROM "+ getSourceTableName();
		    log.log(Level.INFO,"Query to execute: " + selection);
		    s.execute(selection);
		    rs = s.getResultSet();
		    s.close();
		    } catch (SQLException e) {
		    	conn.closeConnection();
		    	log.log(Level.SEVERE,"Exception catched: " +e.getLocalizedMessage());
		    }
		return rs;
	}
	/**
	 * In this method the result from the checksum calculation is stored in the database table 
	 * @param ID Idetifier from the record
	 * @param checksum calculated checksum
	 */
	public boolean checkSequence(String system, String seqnum, String sequence) {
	try {
		Statement s = conn.createStatement();
	    log.log(Level.INFO,"Statement established");
	    // Fetch table
	    String query = "SELECT ID FROM "+ getTargetTableName()+ " WHERE lower(system) = '"+system.toLowerCase()+ "' AND SequenceNumber = "+seqnum+" AND lower(SequenceName) = '"+sequence.toLowerCase()+"'";
	    log.log(Level.INFO,"Query to execute: " + query);
	    s.execute(query);
	    ResultSet rs = s.getResultSet();
	    s.close();
	    if(rs.next()) {
	    	return true;
	    }else {
	    	return false;
	    }
	    	
	    } catch (SQLException e) {
	    	conn.closeConnection();
	    	log.log(Level.SEVERE,"Exception catched: " +e.getLocalizedMessage());
	    	return false;
	    }
	}
	/**
	 * In this method the result from the checksum calculation is stored in the database table 
	 * @param ID Idetifier from the record
	 * @param checksum calculated checksum
	 */
	public boolean checkSequence(String sequence) {
	try {
		Statement s = conn.createStatement();
	    log.log(Level.INFO,"Statement established");
	    // Fetch table
	    String query = "SELECT ID FROM "+ getTargetTableName()+ " WHERE SequenceName = '"+sequence+"'";
	    log.log(Level.INFO,"Query to execute: " + query);
	    s.execute(query);
	    ResultSet rs = s.getResultSet();
	    s.close();
	    if(rs.next()) {
	    	return true;
	    }else {
	    	return false;
	    }
	    	
	    } catch (SQLException e) {
	    	conn.closeConnection();
	    	log.log(Level.SEVERE,"Exception catched: " +e.getLocalizedMessage());
	    	return false;
	    }
	}
	/**
	 * In this method the result from the checksum calculation is stored in the database table 
	 * @param ID Idetifier from the record
	 * @param checksum calculated checksum
	 */
	public boolean trackSequence(String path) {
	try {
		Statement s = conn.createStatement();
	    log.log(Level.INFO,"Statement established");
	    // Fetch table
	    String query = "INSERT INTO "+ getTrackTableName()+" (Path) VALUES ('"+path+"')";
	    log.log(Level.INFO,"Query to execute: " + query);
	    s.execute(query);
	    ResultSet rs = s.getResultSet();
	    s.close();
	    if(rs.next()) {
	    	return true;
	    }else {
	    	return false;
	    }
	    	
	    } catch (SQLException e) {
	    	conn.closeConnection();
	    	log.log(Level.SEVERE,"Exception catched: " +e.getLocalizedMessage());
	    	return false;
	    }
	}
}
