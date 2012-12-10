package zpisync.desktop;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

import zpisync.desktop.controllers.AppController;
import zpisync.desktop.models.DeviceInfoModel;
import zpisync.desktop.models.PreferencesModel;
import zpisync.desktop.models.TrayModel;
import zpisync.desktop.views.PreferencesView;
import zpisync.desktop.views.TrayView;
import zpisync.shared.Util;
import zpisync.shared.services.SwitchPower;

public class App implements AppController {

	private static final Logger log = Logger.getLogger(App.class.getName());

	private File confDir;

	private PreferencesView prefsView;
	private PreferencesModel prefsModel = new PreferencesModel();

	private TrayView trayView;
	private TrayModel trayModel = new TrayModel();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setupLookAndFeel();
				setInstance(new App());
				getInstance().initialize();
			}
		});
	}

	private static App instance;

	public static synchronized App getInstance() {
		return instance;
	}

	private static synchronized void setInstance(App app) {
		if (instance != null)
			throw new Error("App is already running!");
		instance = app;
	}

	public static void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private void initialize() {
		initializeAppDir();
		readPreferences();
		prefsModel.getDataDir().mkdir();
		if (!prefsModel.getDataDir().isDirectory())
			throw new Error("Data directory corrupted");
		createUI();
		startUpnp();
		displayMessage("ZpiSync", "Service is now running", MessageType.INFO);
	}

	private void initializeAppDir() throws Error {
		String customConfDir = System.getProperty("zpisync.confdir");
		if (customConfDir != null)
			confDir = new File(customConfDir);
		if (confDir == null)
			confDir = new File(Util.getHomeDir(), ".zpisync");
		confDir.mkdir();
		if (!confDir.isDirectory())
			throw new Error("Configuration directory missing: " + confDir);
	}

	private void readPreferences() {
		File file = getPreferencesFile();
		if (file.isFile())
			prefsModel.load(file);
	}

	private File getPreferencesFile() {
		return new File(confDir, "prefs.xml");
	}

	private void writePreferences() {
		File file = getPreferencesFile();
		try {
			prefsModel.save(file);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Unable to write preferences", e);
		}
	}

	private void createUI() {
		trayView = new TrayView(this);
		prefsView = new PreferencesView(this);
	}

	UpnpService upnpService;

	private void startUpnp() {
		upnpService = new UpnpServiceImpl(threadSafeRegistryListener);
		upnpService.getRegistry().removeAllRemoteDevices();
		upnpService.getControlPoint().search(1);
	}

	private void stopUpnp() {
		upnpService.shutdown();
	}

	@Override
	public void displayMessage(String caption, String text, MessageType messageType) {
		trayView.displayMessage(caption, text, messageType);
	}

	@Override
	public void showPreferences() {
		prefsView.modelToView(prefsModel);
		prefsView.setVisible(true);
	}

	@Override
	public void saveState() {
		prefsView.viewToModel(prefsModel);
		writePreferences();
	}

	@Override
	public void syncNow() {
		// TODO Auto-generated method stub
	}

	@Override
	public void exit() {
		stopUpnp();
		log.info("Application shutdown");
		System.exit(0);
	}

	@Override
	public void rescanDevices() {
		prefsModel.getKnownDevices().clear();
		prefsView.modelToView(prefsModel);
		upnpService.getControlPoint().search(1);
	}

	private RegistryListener registryListener = new DefaultRegistryListener() {

		// cling is supposedly thread-safe

		private Service<?, ?> findService(Device<?, ?, ?> device, Class<?> serviceType) {
			LocalService service = new AnnotationLocalServiceBinder().read(SwitchPower.class);
			return device.findService(service.getServiceType());
		}

		@Override
		public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
			log.log(Level.INFO, "Device added: {0} {1} {2}", new Object[] { device.getDisplayString(),
					device.getDetails().getFriendlyName(), device });

			Service<?, ?> switchPower = findService(device, SwitchPower.class);
			if (switchPower == null)
				return;

			String udn = device.getIdentity().getUdn().getIdentifierString();
			DeviceInfoModel devInfo = prefsModel.getKnownDevice(udn);
			boolean add = false;
			if (devInfo == null) {
				devInfo = new DeviceInfoModel();
				add = true;
			}

			devInfo.setDisplayName(device.getDetails().getFriendlyName());
			devInfo.setUdn(udn);

			if (add)
				prefsModel.getKnownDevices().add(devInfo);
			prefsView.modelToView(prefsModel);
		};

		@Override
		public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
			Service<?, ?> switchPower = findService(device, SwitchPower.class);
			if (switchPower == null)
				return;

			String udn = device.getIdentity().getUdn().getIdentifierString();
			DeviceInfoModel devInfo = prefsModel.getKnownDevice(udn);
			if (devInfo == null)
				return;

			devInfo.setDisplayName(device.getDetails().getFriendlyName());
			devInfo.setUdn(udn);

			prefsView.modelToView(prefsModel);
		};

		@Override
		public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
			log.log(Level.INFO, "Device removed: {0} {1} {2}", new Object[] { device.getDisplayString(),
					device.getDetails().getFriendlyName(), device });

			Service<?, ?> switchPower = findService(device, SwitchPower.class);
			if (switchPower == null)
				return;

			String udn = device.getIdentity().getUdn().getIdentifierString();

			Iterator<DeviceInfoModel> it = prefsModel.getKnownDevices().iterator();
			while (it.hasNext()) {
				DeviceInfoModel devInfo = it.next();
				if (devInfo.getUdn().equals(udn) && !devInfo.isTrusted())
					it.remove();
			}
			prefsView.modelToView(prefsModel);
		};
	};

	private RegistryListener threadSafeRegistryListener = EdtProxy.blocking(registryListener, RegistryListener.class);

}
