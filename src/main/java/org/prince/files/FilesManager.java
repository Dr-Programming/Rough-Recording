package org.prince.files;

import java.io.File;

import org.prince.configuration.ConfigManager;
import org.prince.configuration.Fields;

public class FilesManager {
	
	private ConfigManager configManager;
	
	
	public FilesManager(ConfigManager congifManager) {
		this.configManager = congifManager;
	}
	
	public boolean isFolderAvailable(String folderName) {
		File file = new File(configManager.getProperty(Fields.savePath.toString()) + folderName);
		System.out.println(configManager.getProperty(Fields.savePath.toString()) + folderName + " File checked.");
		if(file.exists()) {
			return true;
		}
		return false;
	}
	
	public String createPath(String karpanNo, String diamondNo, boolean createKarpan, boolean createDCode) {
		System.out.println("karpan: " + createKarpan);
		System.out.println("diamond: " + createDCode);
		if(createKarpan) {
			File file = new File(configManager.getProperty("savePath") + File.separator + karpanNo);
			if(!file.mkdir()) {
				System.out.println("Problem creating the Karpan folder.");
				return null;
			}
		}
		if(createDCode) {
			File file = new File(configManager.getProperty("savePath") + File.separator + karpanNo + File.separator + diamondNo);
			if(!file.mkdir()) {
				System.out.println("Problem creating the Diamond folder.");
				return null;
			}
		}
		String path = configManager.getProperty("savePath")+ karpanNo + File.separator + diamondNo;
		return path;
	}

}
