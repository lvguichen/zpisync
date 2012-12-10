package zpisync.android.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zpisync.android.handlers.ConfigHandler;
import zpisync.android.handlers.RunHandler;
import zpisync.shared.FileInfo;
import zpisync.shared.services.SyncService;

public class AndroidSyncService implements SyncService{
	List<FileInfo> fl = null;
	
	public AndroidSyncService(){
		List<File> f = RunHandler.listFiles();
		fl = new ArrayList<FileInfo>();
		int size = f.size();
		for (int i = 0; i < size; i++) {
			fl.add(new FileInfo(f.get(i), new File(ConfigHandler.SYNCDIR)));
		}
	}
	
	public Date getLastModificationDate() {
		Date last = fl.get(0).getModificationTime();
		int i = 1;
		while (i<fl.size()){
			if (fl.get(i).getModificationTime().compareTo(last)>0)
				last = fl.get(i).getModificationTime();
			i++;
		}
		return last;
	}

	public List<FileInfo> getFileList(Date modifiedSince) {
		List<FileInfo> subList = new ArrayList<FileInfo>();
		int size = fl.size();
		for (int i = 0; i < size; i++) {
			if (modifiedSince.compareTo(fl.get(i).getModificationTime())<=0){
				subList.add(fl.get(i));
			}
		}
		return subList;
	}

	public FileInfo getFileInfo(String path) {
		int i = 0; 
		while (i<fl.size() && !path.equalsIgnoreCase(fl.get(i).getPath()))
			i++;
		return i>=fl.size()?null:fl.get(i);
	}



}
