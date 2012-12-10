package zpisync.desktop.controllers;

import java.awt.TrayIcon.MessageType;

public interface AppController {
	void displayMessage(String caption, String text, MessageType messageType);

	void exit();

	void associate(String udn);

	void rescanDevices();

	void showPreferences();

	void saveState();

	void syncNow();

	void syncNow(String udn);

	AppController NULL = new AppController() {
		public void displayMessage(String caption, String text, MessageType messageType) {
		}

		@Override
		public void exit() {
		}

		@Override
		public void showPreferences() {
		}

		@Override
		public void syncNow() {
		}

		@Override
		public void syncNow(String udn) {
		}

		@Override
		public void saveState() {
		}

		@Override
		public void rescanDevices() {
		}

		@Override
		public void associate(String udn) {
		}
	};
}