package com.jl.vkgrabber.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by JonathanLivingston on 18.05.2014 17:11.
 */
public class Configuration {

	public static final String USER_ID = "com.jl.vkgrabber.user.id";
	public static final String TOKEN = "com.jl.vkgrabber.user.token";
    public static final String OUTPUT_FOLDER = "com.jl.vkgrabber.outputfolder";

	private static final Properties PROPERTIES;

	static {
		PROPERTIES = new Properties();
		try (InputStream stream = Configuration.class
				.getResourceAsStream("/grabber.properties")) {
			PROPERTIES.load(stream);
            String outputPath = PROPERTIES.getProperty(OUTPUT_FOLDER);
            File outputFolder = new File(outputPath);
            if (!outputFolder.exists())
                outputFolder.mkdirs();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	public static String getProperty(String propertyName) {
		return PROPERTIES.getProperty(propertyName, "");
	}

}
