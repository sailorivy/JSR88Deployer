import javax.enterprise.deploy.shared.ModuleType;

/**
 * Wrapper object of app.properties
 * 
 * @author <a href="mailto:ivy5028@gmail.com">Ivy Wang</a>
 */
public class AppInfo extends PropertyManager {
	private String appPath;
	private String planPath;
	private ModuleType type;

	private static final String APP_PATH = "app.path";
	private static final String PLAN_PATH = "plan.path";
	private static final String APP_TYPE = "app.type";

	public AppInfo() {
		init("/app.properties");

		appPath = getPropertyValue(APP_PATH);
		planPath = getPropertyValue(PLAN_PATH);
		type = getType(getPropertyValue(APP_TYPE.toLowerCase()));
	}

	public String getAppPath() {
		return appPath;
	}

	public String getPlanPath() {
		return planPath;
	}

	public ModuleType getType() {
		return type;
	}

	private ModuleType getType(String type) {
		if (type.equals("web")) {
			return ModuleType.WAR;
		}
		if (type.equals("ejb")) {
			return ModuleType.EJB;
		}
		if (type.equals("rar")) {
			return ModuleType.RAR;
		}
		if (type.equals("ear")) {
			return ModuleType.EAR;
		}
		return null;
	}
}
