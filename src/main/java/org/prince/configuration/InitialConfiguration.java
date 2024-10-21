package org.prince.configuration;

import java.io.File;

class InitialConfiguration {
	
	private String userHomeDir;
	private String deafultSavePath;
	private static final String HOME_PORPERTY = "user.home";
	private static final String MASTER_FOLDER_NAME = "Rough-Recording";
	private static String DEAFULT_DIRECTORY;
	private File defDir;
	private File saveDir;
	
	protected InitialConfiguration() {
		userHomeDir = System.getProperty(HOME_PORPERTY);
		makeDefaultDirectory();
		makeDefaultSavePath();
	}
	
	private void makeDefaultDirectory() {
		DEAFULT_DIRECTORY = userHomeDir + File.separator + MASTER_FOLDER_NAME;
		defDir = new File(DEAFULT_DIRECTORY);
		if(!defDir.exists()) {
			defDir.mkdir();
		}
	}
	
	private void makeDefaultSavePath() {
		deafultSavePath = DEAFULT_DIRECTORY + File.separator + "Recorded-Videos";
		saveDir = new File(deafultSavePath);
		if(!saveDir.exists()) {
			saveDir.mkdir();
		}
	}
	
	protected String getSavePath() {
		return saveDir.getAbsolutePath() + File.separator;
	}
	
	protected String getConfigPropertiesPath() {
		String path = DEAFULT_DIRECTORY + File.separator + "Config";
		File file = new File(path);
		if(!file.exists()) {
			file.mkdir();
		}
		return file.getAbsolutePath();
	}
	
	
//	public static void main(String[] args) {
//		
//		String userHome = System.getProperty("user.home");
//		String defualtPath = userHome + File.separator + "Rough-recording-Videos";
//		File dir = new File(defualtPath);
//		if(!dir.exists()) {
//			dir.mkdir();
//		}
//		
//		String path = dir.getAbsolutePath();
//		System.out.println("Created Path : "+ path);
//	}
}
