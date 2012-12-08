package zpisync.desktop.controllers;

import java.awt.TrayIcon.MessageType;

public interface AppController {
	void displayMessage(String caption, String text, MessageType messageType);

	void exit();

	void showPreferences();

	void syncNow();

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
	};
}