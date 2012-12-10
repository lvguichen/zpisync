package zpisync.desktop;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

import zpisync.desktop.controllers.AppController;
import zpisync.desktop.models.DeviceInfoModel;
import zpisync.desktop.models.PreferencesModel;
import zpisync.desktop.models.TrayModel;
import zpisync.desktop.views.PreferencesView;
import zpisync.desktop.views.TrayView;
import zpisync.shared.FileInfo;
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
				try {
					getInstance().initialize();
				} catch (Exception e) {
					throw new Error(e);
				}
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

	private void initialize() throws Exception {
		initializeAppDir();
		readPreferences();
		prefsModel.getDataDir().mkdir();
		if (!prefsModel.getDataDir().isDirectory())
			throw new Error("Data directory corrupted");
		createUI();
		startUpnp();
		startFileScanner();
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

	FileScanner fileScanner;
	List<FileInfo> files = new ArrayList<>();
	Map<String, FileInfo> fileMap = new HashMap<>();

	public List<FileInfo> getFiles() {
		return files;
	}

	private FileInfo toFileInfo(FileInfo result, Path relativePath, BasicFileAttributes attrs) {
		result.setName(relativePath.getFileName().toString());
		result.setDirectory(attrs.isDirectory());
		result.setModificationTime(new Date(attrs.lastModifiedTime().toMillis()));
		result.setPath(relativePath.toString());
		result.setSize(attrs.size());
		return result;
	}

	private FileInfo toFileInfo(Path relativePath, BasicFileAttributes attrs) {
		return toFileInfo(new FileInfo(), relativePath, attrs);
	}

	private void addFile(Path relativePath, BasicFileAttributes attrs, boolean initial) {
		FileInfo fi = toFileInfo(relativePath, attrs);
		files.add(fi);
		fileMap.put(fi.getPath(), fi);
		fireFilesChanged();
	}

	private void updateFile(Path relativePath, BasicFileAttributes attrs) {
		FileInfo fi = fileMap.get(relativePath.toString());
		if (fi == null) {
			log.severe("missing file info: " + relativePath);
		}
		toFileInfo(fi, relativePath, attrs);
		fireFilesChanged();
	}

	private void removeFile(Path relativePath) {
		String path = relativePath.toString();
		FileInfo fi = fileMap.get(path);
		files.remove(fi);
		fileMap.remove(path);
		fireFilesChanged();
	}

	protected void fireFilesChanged() {
		prefsView.fireFilesChanged(files);
	}

	private void startFileScanner() throws IOException {
		fileScanner = new FileScanner(prefsModel.getDataDir());
		fileScanner.addChangeListener(threadSafeFileListener);
		fileScanner.scan(new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (!dir.equals(prefsModel.getDataDir().toPath())) {
					addFile(fileScanner.relativize(dir), attrs, true);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				addFile(fileScanner.relativize(file), attrs, true);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				log.log(Level.WARNING, "Cannot visit file: " + file, exc);
				return FileVisitResult.CONTINUE;
			}
		});
		// XXX start watching before scanning to avoid race
		fileScanner.startWatcher();
	}

	private void stopFileScanner() {
		try {
			fileScanner.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Unable to stop FileScanner", e);
		}
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

	private void syncNow(DeviceInfoModel devInfo) {
		// TODO launch sync here
	}

	@Override
	public void syncNow() {
		for (DeviceInfoModel devInfo : prefsModel.getKnownDevices()) {
			if (devInfo.isTrusted() && devInfo.isActive())
				syncNow(devInfo);
		}
	}

	@Override
	public void syncNow(String udn) {
		DeviceInfoModel devInfo = prefsModel.getKnownDevice(udn);
		if (devInfo != null && devInfo.isTrusted() && devInfo.isActive())
			syncNow(devInfo);
	}

	@Override
	public void exit() {
		stopUpnp();
		stopFileScanner();
		log.info("Application shutdown");
		System.exit(0);
	}

	@Override
	public void associate(String udn) {
		Device device = upnpService.getRegistry().getDevice(new UDN(udn), false);
		DeviceInfoModel knownDevice = prefsModel.getKnownDevice(udn);
		knownDevice.setTrusted(true);
		prefsView.modelToView(prefsModel);
		writePreferences();
	}

	@Override
	public void rescanDevices() {
		prefsModel.clearDeviceState();
		prefsView.modelToView(prefsModel);
		upnpService.getRegistry().removeAllRemoteDevices();
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

			devInfo.setActive(true);
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

			devInfo.setActive(true);
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
				if (devInfo.getUdn().equals(udn)) {
					if (devInfo.isTrusted())
						devInfo.setActive(false);
					else
						it.remove();
				}
			}
			prefsView.modelToView(prefsModel);
		};
	};

	private RegistryListener threadSafeRegistryListener = EdtProxy.blocking(registryListener, RegistryListener.class);

	private FileScanner.ChangeListener fileListener = new FileScanner.ChangeListener() {

		@Override
		public void event(WatchEvent<Path> event) {
			try {
				Path relativePath = event.context();
				Path file = fileScanner.resolve(relativePath);

				if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
					BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
					addFile(relativePath, attrs, false);
				} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
					BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
					updateFile(relativePath, attrs);
				} else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
					removeFile(relativePath);
				} else {
					throw new Error("Unhandler event kind: " + event.kind());
				}

			} catch (IOException e) {
				log.log(Level.SEVERE, "Unable to process file event", e);
			}
		}
	};

	private FileScanner.ChangeListener threadSafeFileListener = EdtProxy.blocking(fileListener,
			FileScanner.ChangeListener.class);
}
