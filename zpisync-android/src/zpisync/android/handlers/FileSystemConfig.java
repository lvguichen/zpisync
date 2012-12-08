package zpisync.android.handlers;

import android.os.Environment;

public class FileSystemConfig {
	public final static String ROOTDIR = "/";
	public final static String STARTDIR = Environment.getExternalStorageDirectory().getAbsolutePath();//ROOTDIR+"sdcard/";
	public final static String SYNCDIR = STARTDIR+"/zpi-sync";
	public final static String SYNCFILE = SYNCDIR+"/syncstatus.zpis";
	public final static String EXTENSION = ".zpis";
	public final static String IGNOREDIR = ".zpisignore";
}
