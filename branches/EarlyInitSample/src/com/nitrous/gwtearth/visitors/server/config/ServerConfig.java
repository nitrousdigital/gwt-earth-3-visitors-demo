package com.nitrous.gwtearth.visitors.server.config;

import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
	private static ServerConfig INSTANCE;
	private Properties properties;
	
	private ServerConfig() {
		properties = new Properties();
		load();
	}
	
	public static synchronized ServerConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServerConfig();
		}
		return INSTANCE;
	}
	
	private void load() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("com/nitrous/gwtearth/visitors/server/config/config.properties");			
			properties.load(is);
		} catch (Exception ex) {
			System.err.println("Failed to load server configuration");
			ex.printStackTrace(System.err);
		}
	}
	
	public String getProperty(String name) {
		return properties.getProperty(name);
	}
	
	public String getProperty(String name, String defaultValue) {
		return properties.getProperty(name, defaultValue);
	}
	
	public static void main (String[] args){
		String user = ServerConfig.getInstance().getProperty("anayltics.account.id");
		String pwd = ServerConfig.getInstance().getProperty("anayltics.account.password");
		String table =ServerConfig.getInstance().getProperty("anayltics.account.table.id");
		System.out.println("user="+user);
		System.out.println("password="+pwd);
		System.out.println("table="+table);
	}
}
