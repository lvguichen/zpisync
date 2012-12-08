package zpisync.android.handlers;

import java.io.File;
import java.io.IOException;

public class FirstRun {
	public static String prepareDevice() {
		File dir = new File(FileSystemConfig.SYNCDIR);
		StringBuffer log = new StringBuffer();
		if (dir.exists() && dir.isDirectory()) {
			log.append("Sync dir exists");
		} else {
			File f = null;
			f = dir;
			boolean success = f.mkdir();
			if (!success) {
				log.append("Sync dir could not be created!");
				return log.toString();
			}
			log.append("Sync dir created");
		}
		log.append("|");
		File file = new File(FileSystemConfig.SYNCFILE);
		if (file.exists()) {
			log.append("Sync file exists");
		} else {
			File f = null;
			f = file;
			boolean success;
			try {
				success = f.createNewFile();
				if (!success) {
					log.append("Sync file could not be created!");
					return log.toString();
				}
				log.append("Sync file created");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// File file = new java.io.File(dir)
		return log.toString();
	}
}
