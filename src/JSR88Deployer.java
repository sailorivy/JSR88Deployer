/**
 * Main class of JSR 88 deployer
 * 
 * @author <a href="mailto:ivy5028@gmail.com">Ivy Wang</a>
 */
public class JSR88Deployer {
	private static final String DEFAULT_PRODUCT = "JBoss";

	public static void main(String[] args) {
		String product = getProduct(args).toLowerCase();
		DMProperty dmProp = new DMProperty(product);
		AppInfo appInfo = new AppInfo();

		String result = "unknown status";
		DeploymentHandler handler = new DeploymentHandler(dmProp);
		// distribute-stream
		result = handler.distribute(appInfo.getType(), appInfo.getAppPath(),
				appInfo.getPlanPath(), true, product);
		System.out.println("The result of distribution of "
				+ appInfo.getAppPath() + " is " + result);

		// distribute-file
		result = handler.distribute(appInfo.getType(), appInfo.getAppPath(),
				appInfo.getPlanPath(), false, product);
		System.out.println("The result of distribution of "
				+ appInfo.getAppPath() + " is " + result);

		// start
		result = handler.start(handler.getAvailableModules());
		System.out.println("The result of start is " + result);

		// stop
		result = handler.stop(handler.getAvailableModules());
		System.out.println("The result of stop is " + result);

		// undeploy
		result = handler.undeploy(handler.getAvailableModules());
		System.out.println("The result of undeployment is " + result);

		// release
		handler.release();
		System.out.println("Release connection and exit.");
	}

	private static String getProduct(String... args) {
		if (args == null || args.length < 1) {
			return DEFAULT_PRODUCT;
		}
		return args[0];
	}
}
