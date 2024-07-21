package dev.inProgress.sspro6.hh_suite.log;

import java.io.OutputStream;

import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.time_t;

/**
 * Attempted translation from hhsuite_3.3.0 Log.h
 * Created on: May 26, 2014
 *     @Author: meiermark
 * @translator Benjamin Strauss
 * 
 */

//#ifndef LOG_H_
//#define LOG_H_

//#include <sstream>
//#include <string>
//#include <cstdio>
//#include <sys/time.h>

//inline std::string NowTime();

public class Log extends CppTranslator {
	
	protected OutputStream os;
	
	public Log() { }
	
	public OutputStream Get(LogLevel level = INFO) {
	    os << "- " << NowTime();
	    os << " " << to_string(level) << ": ";
	    os << String(level > DEBUG ? level - DEBUG : 0, '\t');
	    return os;
	}
	
	public static LogLevel reporting_level() {
	    LogLevel reportingLevel = LogLevel.DEBUG4;
	    return reportingLevel;
	}
	
	public static String to_string(LogLevel level) {
		String buffer[] = { "ERROR", "WARNING", "INFO", "DEBUG",
				"DEBUG1", "DEBUG2", "DEBUG3", "DEBUG4" };
	    return buffer[level.ordinal()];
	}
	
	public static LogLevel from_string(String level) {
		switch(level) {
		case "DEBUG4":		return LogLevel.DEBUG4;
		case "DEBUG3":		return LogLevel.DEBUG3;
		case "DEBUG2":		return LogLevel.DEBUG2;
		case "DEBUG1":		return LogLevel.DEBUG1;
		case "DEBUG":		return LogLevel.DEBUG;
		case "INFO":		return LogLevel.INFO;
		case "WARNING":		return LogLevel.WARNING;
		case "ERROR":		return LogLevel.ERROR;
		}
		
	    Log().Get(WARNING) << "Unknown logging level '" << level << "'. Using INFO level as default.";
	    return LogLevel.INFO;
	}
	
	public static LogLevel from_int(int level) {
		switch(level) {
		case 7:				return LogLevel.DEBUG4;
		case 6:				return LogLevel.DEBUG3;
		case 5:				return LogLevel.DEBUG2;
		case 4:				return LogLevel.DEBUG1;
		case 3:				return LogLevel.DEBUG;
		case 2:				return LogLevel.INFO;
		case 1:				return LogLevel.WARNING;
		case 0:				return LogLevel.ERROR;
		default: 
			Log().Get(WARNING) << "Unknown logging level '" << level << "'. Using INFO level as default.";
			return LogLevel.INFO;
		}
	}

	//private Log(const Log&);
	//private Log& operator =(const Log&);
	
	public String NowTime() {
	    char[] buffer = new char[11];
	    time_t t;
	    time(&t);
	    tm r = {0};
	    strftime(buffer, sizeof(buffer), "%X", localtime_r(&t, &r));
	    
	    timeval tv;
	    gettimeofday(&tv, 0);
	    char[] result = new char[100];
	    sprintf(result, "%s.%03ld", buffer, (long)tv.tv_usec / 1000);
	    return new String(result);
	}
	
	public static void HH_LOG(level) {
		if (level <= Log.reporting_level()) \ Log().Get(level);
	}
	
	public void deconstruct() {
		finalize();
		os.write('\n');
	    fprintf(stderr, "%s", os);
	    stderr.flush();
	}
}

//typedef Log FILELog;

//#define HH_LOG(level) \
//  if (level <= Log::reporting_level()) \ Log().Get(level)

//#endif /* LOG_H_ */

