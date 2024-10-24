package me.fami6xx.rpuniverse.core.misc;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionInfo {
    private static final String VERSION;

    static {
        String version1;
        Properties properties = new Properties();
        try (InputStream input = VersionInfo.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input != null) {
                properties.load(input);
                version1 = properties.getProperty("app.version");
            } else {
                version1 = "unknown";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            version1 = "unknown";
        }
        VERSION = version1;
    }

    public static String getVersion() {
        return VERSION;
    }
}
