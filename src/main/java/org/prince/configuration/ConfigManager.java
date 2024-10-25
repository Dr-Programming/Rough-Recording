package org.prince.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
	
	private static String DEFAULT_CONFIG_FILE = "";
	private static String USER_CONFIG_FILE = "";
	
	private Properties defaultProperties;
	private Properties userProperties;
	
	private InitialConfiguration iConfig;
	
	
	public ConfigManager() {
		iConfig = new InitialConfiguration();
		propertiesPath();
		userProperties = new Properties();
		defaultProperties = new Properties();
		loadDefaultProperties();
		loadUserProperties();
	}
	
	private void propertiesPath() {
		USER_CONFIG_FILE = iConfig.getConfigPropertiesPath() + File.separator + "user-properties.properties";
		DEFAULT_CONFIG_FILE = iConfig.getConfigPropertiesPath() + File.separator + "default-properties.properties";
	}
	
	private void loadUserProperties() {
		try(FileInputStream input = new FileInputStream(USER_CONFIG_FILE)){
			userProperties.load(input);
		} catch (IOException e) {
			System.out.println("No exsiting configuration found. Default Setting will be loaded.");
			userProperties.putAll(defaultProperties);
		}
	}
	
	private void loadDefaultProperties() {
		try(FileInputStream input = new FileInputStream(DEFAULT_CONFIG_FILE)){
			defaultProperties.load(input);
		} catch (IOException e) {
			System.out.println("Unable to load Deafult Properties....\nCreating new Default file.");
			makeDefaultPropertiesFile();
		}
	}
	
	private void makeDefaultPropertiesFile() {
		Properties properties = new Properties();
		properties.setProperty("CHANGE", "FALSE");
		properties.setProperty("savePath", iConfig.getSavePath());
		try(FileOutputStream output = new FileOutputStream(DEFAULT_CONFIG_FILE)){
			properties.store(output, "Deafult Configuration");
			loadDefaultProperties();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveUserProperties() {
		try(FileOutputStream output = new FileOutputStream(USER_CONFIG_FILE)){
			userProperties.store(output, "User Configuration");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void restoreDefaults() {
		userProperties.clear();
		userProperties.putAll(defaultProperties);
		saveUserProperties();
	}
	
	public String getProperty(String key) {
		return userProperties.getProperty(key, defaultProperties.getProperty(key));
	}
	
	public void setProperty(String key, String value) {
		userProperties.setProperty(key, value);
	}
}
