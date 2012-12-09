package zpisync.desktop;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import zpisync.desktop.controllers.AppController;
import zpisync.desktop.models.PreferencesModel;
import zpisync.desktop.models.TrayModel;
import zpisync.desktop.views.PreferencesView;
import zpisync.desktop.views.TrayView;

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
		log.info("Application shutdown");
		System.exit(0);
	}

}
