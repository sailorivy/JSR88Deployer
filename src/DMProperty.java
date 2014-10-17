/**
 * Wrapper object of dm.properties
 * 
 * @author <a href="mailto:ivy5028@gmail.com">Ivy Wang</a>
 */
public class DMProperty extends PropertyManager {
	private String jar;
	private String uri;
	private String userName;
	private String password;

	private static final String DM_JAR = "dm.jar";
	private static final String DM_URI = "dm.uri";
	private static final String DM_USERNAME = "dm.userName";
	private static final String DM_PASSWORD = "dm.password";

	public DMProperty(String product) {
		super.init("/dm.properties");

		jar = getPropertyValue(getPropertyName(DM_JAR, product));
		uri = getPropertyValue(getPropertyName(DM_URI, product));
		userName = getPropertyValue(getPropertyName(DM_USERNAME, product));
		password = getPropertyValue(getPropertyName(DM_PASSWORD, product));
	}

	public String getJar() {
		return jar;
	}

	public String getUri() {
		return uri;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	private static String getPropertyName(String key, String product) {
		return key + "." + product;
	}
}
