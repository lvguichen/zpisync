package zpisync.desktop;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

public class App implements AppController {

	private static final Logger log = Logger.getLogger(App.class.getName());

	private PreferencesUI prefsUi;
	private TrayUI trayUi;

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
		readPreferences();
		createUI();
		displayMessage("ZpiSync", "Service is now running", MessageType.INFO);
	}

	private void readPreferences() {
	}

	private void createUI() {
		trayUi = new TrayUI(this);
		prefsUi = new PreferencesUI(this);
	}

	@Override
	public void displayMessage(String caption, String text, MessageType messageType) {
		trayUi.displayMessage(caption, text, messageType);
	}

	@Override
	public void showPreferences() {
		prefsUi.setVisible(true);
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
