package zpisync.android.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

public class ConfigHandler {
	public final static String ROOTDIR = "/";
	public final static String STARTDIR = Environment.getExternalStorageDirectory().getAbsolutePath();//ROOTDIR+"sdcard/";
	public final static String SYNCDIR = STARTDIR+"/zpi-sync";
	public final static String SYNCFILE = SYNCDIR+"/syncstatus.zpis";
	public final static String IDFILE = SYNCDIR+"/deviceID.zpis";
	public final static String EXTENSION = ".zpis";
	public final static String IGNOREDIR = ".zpisignore";
	public final static String DEVICEID = "ZPI-SYNC";
	public static String getDeviceID() {
		if (RunHandler.isReady()){
			String deviceID = DEVICEID;
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(IDFILE)));
				String line;
				while ((line = br.readLine()) != null) {
					deviceID += "-"+line;
				}
				return deviceID;	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return DEVICEID;
	} 
}
