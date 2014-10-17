import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressObject;

import org.jboss.as.ee.deployment.spi.DeploymentMetaData;
import org.jboss.as.ee.deployment.spi.JarUtils;

/**
 * Handler of deployment
 * 
 * @author <a href="mailto:ivy5028@gmail.com">Ivy Wang</a>
 */
public class DeploymentHandler {
	private DeploymentManager dm = null;
	private final String J2EE_DEPLOYMENT_FACTORY = "J2EE-DeploymentFactory-Implementation-Class";

	public DeploymentHandler(DMProperty props) {
		getDeploymentManager(props);
	}

	/**
	 * Retrieve a deployment manager instance from the specified Java EE
	 * application server based on the properties.
	 */
	private synchronized void getDeploymentManager(DMProperty props) {
		if (dm == null) {
			try {
				File file = new File(props.getJar());
				Manifest mf = new JarFile(file).getManifest();
				String className = mf.getMainAttributes().getValue(
						J2EE_DEPLOYMENT_FACTORY);

				Class factory = Class.forName(className);
				DeploymentFactory df = (DeploymentFactory) factory
						.newInstance();

				DeploymentFactoryManager dfm = DeploymentFactoryManager
						.getInstance();
				dfm.registerDeploymentFactory(df);

				dm = dfm.getDeploymentManager(props.getUri(),
						props.getUserName(), props.getPassword());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Retrieve the list of deployment targets.
	 */
	public Target[] getTargets() {
		return dm.getTargets();
	}

	/**
	 * Distribute the application.
	 */
	public String distribute(ModuleType type, String appPath, String planPath,
			boolean isStream, String product) {
		File appFile = null;
		File planFile = null;
		FileInputStream appStream = null;
		FileInputStream planStream = null;

		// application
		try {
			appFile = new File(appPath);
			appStream = new FileInputStream(appFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// deployment plan
		if (planPath != null) {
			try {
				planFile = new File(planPath);
				planStream = new FileInputStream(planFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				// For some Java EE application server like JBoss, the
				// deployment plan can't be null.
				planFile = createDeploymentPlan(appFile.getName(), product);
				if (planFile != null) {
					planStream = new FileInputStream(planFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (isStream) {
			return getStatus(dm.distribute(getTargets(), type, appStream,
					planStream));
		} else {
			return getStatus(dm.distribute(getTargets(), appFile, planFile));
		}
	}

	/**
	 * Start the applications.
	 */
	public String start(TargetModuleID[] moduleIDs) {
		return getStatus(dm.start(moduleIDs));
	}

	/**
	 * Stop the application.
	 */
	public String stop(TargetModuleID[] moduleIDs) {
		return getStatus(dm.stop(moduleIDs));
	}

	/**
	 * Remove the application from the target server.
	 */
	public String undeploy(TargetModuleID[] moduleIDs) {
		return getStatus(dm.undeploy(moduleIDs));
	}

	/**
	 * Release the connection from the application server.
	 */
	public void release() {
		dm.release();
	}

	/**
	 * Retrieve the list of all Java EE application on the identified targets.
	 */
	public TargetModuleID[] getAvailableModules() {
		List<TargetModuleID> moduleIDs = new ArrayList<TargetModuleID>();
		TargetModuleID[] ids = null;
		try {
			ids = dm.getAvailableModules(ModuleType.WAR, getTargets());
			if (ids != null) {
				moduleIDs.addAll(Arrays.asList(ids));
			}
			ids = dm.getAvailableModules(ModuleType.EJB, getTargets());
			if (ids != null) {
				moduleIDs.addAll(Arrays.asList(ids));
			}
			ids = dm.getAvailableModules(ModuleType.EAR, getTargets());
			if (ids != null) {
				moduleIDs.addAll(Arrays.asList(ids));
			}
			ids = dm.getAvailableModules(ModuleType.RAR, getTargets());
			if (ids != null) {
				moduleIDs.addAll(Arrays.asList(ids));
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TargetException e) {
			e.printStackTrace();
		}
		return moduleIDs.toArray(new TargetModuleID[1]);
	}

	/**
	 * Get the status of the process.
	 */
	private String getStatus(ProgressObject proObj) {
		// waiting for completing
		while (!proObj.getDeploymentStatus().isCompleted()
				&& !proObj.getDeploymentStatus().isFailed()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}

		DeploymentStatus status = proObj.getDeploymentStatus();
		if (status.isCompleted()) {
			return "completed";
		}
		if (status.isFailed()) {
			return "failed";
		}
		return "unknown status";
	}

	/**
	 * Create the deployment plan in the way provided by Java EE application
	 * server. For some Java EE application server like JBoss, the deployment
	 * plan can't be null.
	 */
	private static File createDeploymentPlan(String appName, String product)
			throws Exception {
		if ("jboss".equals(product)) {
			File deploymentPlan = File.createTempFile("deploymentPlan", ".zip");
			deploymentPlan.deleteOnExit();

			JarOutputStream jos = new JarOutputStream(new FileOutputStream(
					deploymentPlan));

			DeploymentMetaData metaData = new DeploymentMetaData(appName);

			String metaStr = metaData.toXMLString();

			JarUtils.addJarEntry(jos, DeploymentMetaData.ENTRY_NAME,
					new ByteArrayInputStream(metaStr.getBytes()));
			jos.flush();
			jos.close();

			return deploymentPlan;
		} else {
			// If the application server needs a not null deployment plan,
			// create the deployment plan in the way provided by it.
			// In the example, because the deployment plan can be null for
			// GlassFish, so return null.
			return null;
		}
	}
}
