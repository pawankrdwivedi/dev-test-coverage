package mapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("❌ config.properties not found in resources folder");
            } else {
                properties.load(input);
                System.out.println("✅ Loaded config.properties successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get a property by key
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Optional: get property with default value
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}