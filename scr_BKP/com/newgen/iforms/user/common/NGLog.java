package com.newgen.iforms.user.common;

import com.newgen.commonlogger.NGLogger;

public class NGLog {

	public static void consoleLog(String message) {
		NGLogger.writeConsoleLog(Campaign_Constants.sParentFolder, Campaign_Constants.sChildLogFolder, message);
	}

	public static void errorLog(String message) {
		NGLogger.writeErrorLog(Campaign_Constants.sParentFolder, Campaign_Constants.sChildLogFolder, message);
	}
	
	public static void errorLog(String message, Throwable t) {
		NGLogger.writeErrorLog(Campaign_Constants.sParentFolder, Campaign_Constants.sChildLogFolder, message, t);
	}

	public static void xmlLog(String message) {
		NGLogger.writeXmlLog(Campaign_Constants.sParentFolder, Campaign_Constants.sChildLogFolder, message);
	}

	public static void trasactionLog(String message) {
		NGLogger.writeTransactionLog(Campaign_Constants.sParentFolder, Campaign_Constants.sChildLogFolder, message);
	}
}
