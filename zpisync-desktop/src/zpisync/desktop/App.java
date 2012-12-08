package zpisync.desktop;

import java.awt.EventQueue;
import java.awt.TrayIcon.MessageType;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import zpisync.desktop.controllers.AppController;
import zpisync.desktop.views.PreferencesView;
import zpisync.desktop.views.TrayView;

public class App implements AppController {

	private static final Logger log = Logger.getLogger(App.class.getName());

	private PreferencesView prefsView;
	private TrayView trayView;

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
		trayView = new TrayView(this);
		prefsView = new PreferencesView(this);
	}

	@Override
	public void displayMessage(String caption, String text, MessageType messageType) {
		trayView.displayMessage(caption, text, messageType);
	}

	@Override
	public void showPreferences() {
		prefsView.setVisible(true);
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
