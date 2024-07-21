package dev.setup.dev;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import install.DirectoryManager;
import utilities.LocalToolBase;

/**
 * Debug management module for SSpro prediction algorithm
 * If SSpro breaks in an upgrade, use this to fix it
 * @author Benjy Strauss
 *
 */

public class SSproDebugModule extends LocalToolBase {
	private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	//log file
	private static final File SSPRO_DEBUG_LOG = new File(DirectoryManager.FILES_BLASTS_SSPRO + "/log.txt");
	//divider to show where one run stopped and another started
	private static final String RUN_DIVIDER = "------------------------------------------------------------";
	//first time running the record method
	private static boolean firstTimeRunning = true;
	
	//determines debug mode
	public static final boolean DEBUG = false;
	//skip the blast
	public static final boolean SKIP_BLAST = false;
	//exit after the blast?
	public static final boolean ONLY_BLAST = false;
	
	public static final File SSPRO_IN = new File("files/blasts/sspro/sspro.in");
	
	/**
	 * Records what variables are, both in the console and in a log file
	 * @param arg0: the string to record
	 */
	public static final void record(String arg0) {
		if(DEBUG) {
			qerr(arg0);
			FileWriter logWriter;
			LocalDateTime now = LocalDateTime.now();
			
			try {
				logWriter = new FileWriter(SSPRO_DEBUG_LOG, true);
				if(firstTimeRunning) {
					if(!SSPRO_DEBUG_LOG.exists()) {
						SSPRO_DEBUG_LOG.createNewFile();
					}
					logWriter.write(RUN_DIVIDER + "\n\n");
					firstTimeRunning = false;
				}
				
				logWriter.write(LOG_FORMATTER.format(now) + "\n");
				logWriter.write(arg0 + "\n\n");
				logWriter.close();
			} catch (IOException e) {
				qerr("Could not write to log: " + arg0);
				e.printStackTrace();
			}
		}
	}
	
}
