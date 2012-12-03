package zpisync.desktop;

import java.awt.Image;
import java.awt.Toolkit;

public class Resources {

	
	private static Image getImage(String path) {
		return Toolkit.getDefaultToolkit().getImage(Resources.class.getResource(path));
	}
	
	public static Image getAppIcon() {
		return getImage("/zpisync/shared/resources/appicon.png");
	}
	
}
