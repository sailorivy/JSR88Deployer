import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Handler of property file
 * 
 * @author <a href="mailto:ivy5028@gmail.com">Ivy Wang</a>
 */
public class PropertyManager {

	private Properties allProps = new Properties();

	/**
	 * Load properties from the specified property file.
	 */
	protected void init(String propFileName) {
		if (allProps.isEmpty()) {
			InputStream inStream = null;
			try {
				allProps.load(Object.class.getResourceAsStream(propFileName));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	protected String getPropertyValue(String key) {
		String prop = allProps.getProperty(key);
		if (prop == null || prop.length() < 1) {
			prop = null;
		}
		return prop;
	}
}