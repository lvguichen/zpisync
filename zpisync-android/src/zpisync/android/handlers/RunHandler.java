package zpisync.android.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.teleal.cling.model.types.UDN;

import android.widget.Toast;

public class RunHandler {
	public static boolean isReady(){
		File dir = new File(ConfigHandler.SYNCDIR);
		if (!dir.exists() || !dir.isDirectory()) {
		return false;
		}
		File file = new File(ConfigHandler.SYNCFILE);
		if (!file.exists()) {
			return false;
		}
		file = new File(ConfigHandler.IDFILE);
		if (!file.exists()) {
			return false;
		}

		return true;
	}
	public static String prepareDevice() {
		File dir = new File(ConfigHandler.SYNCDIR);
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
		File file = new File(ConfigHandler.SYNCFILE);
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
		file = new File(ConfigHandler.IDFILE);
		if (file.exists()) {
			log.append("ID file exists");
		} else {
			File f = null;
			f = file;
			boolean success;
			try {
				success = f.createNewFile();
				if (!success) {
					log.append("ID file could not be created!");
					return log.toString();
				}
				log.append("ID file created");
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write(UDN.uniqueSystemIdentifier("ZPISYNC").getIdentifierString());
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return log.toString();
	}
	public static List<File> listFiles(){
		File folder = new File (ConfigHandler.SYNCDIR);
		return listFiles(folder);
	}
	public static List<File> listFiles(File folder){
		File[] listOfFiles = folder.listFiles();
		List<File> list = new ArrayList<File>();
		File file;
		String fileName;
		String fileExtension;
		boolean ignore;
		for (int i = 0; i < listOfFiles.length; i++) {
			ignore = false;
			file = listOfFiles[i];
			if (file.isFile()){
				fileName = file.getName();
				if (fileName.lastIndexOf('.')>0) {
					fileExtension = fileName.substring(fileName.lastIndexOf('.'));
					if (fileExtension.equalsIgnoreCase(".zpis")) {
						ignore = true;
					}
				}
			} 
			if (!ignore){
				list.add(file);
				if (file.isDirectory()){
					list.addAll(listFiles(file));
				}
			}
		}
		return list;
	}
}
