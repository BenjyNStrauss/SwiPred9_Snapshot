package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import install.DirectoryManager;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class SwiPredLogger extends LocalToolBase {
	//location of the system log file
	private static final String DEFAULT_SYSTEM_LOG = DirectoryManager.FILES_LOGS + "/syslog.txt";
	private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	
	//the name of the system log
	private static String systemLog = DEFAULT_SYSTEM_LOG;
	
	public static final void log(String arg0) {
		FileWriter logWriter;
		LocalDateTime now = LocalDateTime.now();
		
		try {
			logWriter = new FileWriter(systemLog, true);
			logWriter.write(LOG_FORMATTER.format(now) + "\n");
			logWriter.write(arg0 + "\n\n");
			logWriter.close();
		} catch(FileNotFoundException FNFE) {
			qerr("Could not write to log: " + arg0);
			File files = new File(DirectoryManager.FILES);
			if(!files.exists()) {
				qerr("Cannot write to log file because file system is not set up.");
				DirectoryManager.fileSystemSetupError();
			} else {
				File files_logs = new File(DirectoryManager.FILES_LOGS);
				if(!files_logs.exists()) {
					qerr("Cannot write to log file because file system is not set up.");
					DirectoryManager.fileSystemSetupError();
				} else {
					qerr("Cannot write to log for unknown reason.");
					qerr("View exception message for more information.");
					qerr("Alternatively, contact: " + BMAIL);
					FNFE.printStackTrace();
				}
			}
		} catch (IOException e) {
			qerr("Could not write to log: " + arg0);
			e.printStackTrace();
		}
	}
	
	public static final boolean clearLog() {
		return clearLog(null);
	}
	
	public static final boolean clearLog(String logFile) {
		if(logFile == null) {
			return new File(DEFAULT_SYSTEM_LOG).delete();
		} else {
			return new File(DirectoryManager.FILES_LOGS + "/" + logFile).delete();
		}
	}
	
	public static void setSystemLog(String logFile) {
		if(!logFile.contains(".")) {
			logFile += TXT;
		}
		
		if(!logFile.contains("/")) {
			systemLog = DirectoryManager.FILES_LOGS + "/" + logFile;
		} else {
			systemLog = logFile;
		}
	}
}
