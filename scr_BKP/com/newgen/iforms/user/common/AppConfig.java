package com.newgen.iforms.user.common;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

	private static final AppConfig instance = new AppConfig();
	private static Properties properties;

	public AppConfig getInstance() {
		return instance;
	}

	private AppConfig() {
		if (properties == null) {
			properties = new Properties();
			String filePath = System.getProperty("user.dir") + File.separator + "CAMPAIGN_CONFIG" + File.separator
					+ "ALJFS_CAMPAIGN" + File.separator + "CAMPAIGN.properties";
			try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
				properties.load(fileInputStream);
			} catch (IOException e) {
				System.out.println("Exception occured while reading property file : " + e);
			}
		}
	}

	public static String getProperty(String key) {
		if (properties != null) {
			return properties.getProperty(key, "");
		}
		return null;
	}
}
