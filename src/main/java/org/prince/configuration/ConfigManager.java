package org.prince.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
	
	private static final String USER_CONFIG_FILE = "src/main/resources/config.properties";
	private static final String DEFAULT_CONFIG_FILE = "src/main/resources/default.properties";
	
	private Properties userProperties;
	private Properties defaultProperties;
	
	public ConfigManager() {
		userProperties = new Properties();
		defaultProperties = new Properties();
		loadDefaultProperties();
		loadUserProperties();
	}
	
	private void loadUserProperties() {
		try(FileInputStream input = new FileInputStream(USER_CONFIG_FILE)){
			userProperties.load(input);
		} catch (IOException e) {
//			System.out.println("No exsiting configuration found. A new one will be created.");
			System.out.println("No exsiting configuration found. Default Setting will be loaded.");
			userProperties.putAll(defaultProperties);
		}
	}
	
	private void loadDefaultProperties() {
		try(FileInputStream input = new FileInputStream(DEFAULT_CONFIG_FILE)){
			defaultProperties.load(input);
		} catch (IOException e) {
			System.out.println("Unable to load Deafult Properties....");
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
