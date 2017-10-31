import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SequenceSynchronizer {
	private static final Logger log = Logger.getLogger( SequenceSynchronizer.class.getName() );
	static String logFile;
	static String datasource;
	static Analyze analyze;
	public static void main(String[] args) {
		
		log.log(Level.INFO,"start main");
		AnalyzerProperty props = new AnalyzerProperty();
		logFile = props.getServiceProperty("LogFileDest");
		datasource = props.getServiceProperty("DataSource");
		initLogging();
		start();
		log.log(Level.INFO,"end main");
		
	}
	/**
	 * In this method the logging Handler is initialized
	 */
		private static void initLogging(){
			try {
				/**
				 * Using the setUseParentHandlers = false flag, the program no more writes in the console
				 */
				log.setUseParentHandlers(true);
				Handler handler = new FileHandler( logFile +"Analyzer.log" );
				log.addHandler(handler);
				log.log(Level.INFO,"File location of Service:"+logFile);
			} catch (SecurityException e) {
				log.log(Level.SEVERE,e.getLocalizedMessage());
			} catch (IOException e) {
				log.log(Level.SEVERE,e.getLocalizedMessage());
			}
		}
		/**
		 * In this method the logging Handler is initialized
		 */
			private static void start(){
				log.log(Level.INFO,"create Calculator");
				if (datasource.equals("DB")) {
					analyze = new AnalyzeDB();
				}else if (datasource.equals("ATK+")) {
					analyze = new AnalyzeRepo();
				}else if (datasource.equals("ATK32")) {
					analyze = new AnalyzeATK32();
				}
				log.log(Level.INFO,"Calculator created");
				log.log(Level.INFO,"call Calculator.start()");
				if (analyze != null) analyze.start();
				log.log(Level.INFO,"Calculator started");
			}
}
