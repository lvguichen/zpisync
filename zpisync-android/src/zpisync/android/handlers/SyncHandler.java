package zpisync.android.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zpisync.shared.FileInfo;

public class SyncHandler {
	private List<FileInfo> myList;
	private List<FileInfo> sharedList;
	private List<FileInfo> toSend;
	private List<FileInfo> toReceive;
	private List<FileInfo> toKeep;
	private List<FileInfo> toDelete;
	
	public void getMyList(){
		compareLists();
		updateUsageLog();
	};
	private void updateUsageLog(){
		//TODO creates summary of each sync action;
	}
	private void compareLists(){
		toSend = new ArrayList<FileInfo>();
		toReceive = new ArrayList<FileInfo>();
		toKeep = new ArrayList<FileInfo>();
		toDelete = new ArrayList<FileInfo>();
		int size = myList.size();
		int val = -1;
		for (int i = 0;  i<size; i++) {
			FileInfo file = myList.get(i);
			val = whatDo(file);
			switch (val) {
			case 0:
				toKeep.add(file);
				break;
			case 1:
				toSend.add(file);
				break;
			case 2:
				toReceive.add(file);
				break;
			case 3:
				toDelete.add(file);
				break;
			default:
				break;
			}
		}
	}
	
	private int whatDo(FileInfo file){
		// 0 - nothing
		// 1 - send 
		// 2 - receive
		// 3 - delete
		int size = sharedList.size();
		for (int i = 0; i < size; i++) {
			if (file.equals(sharedList.get(i))){
				return 0;
			} else {
				if (file.getId()==sharedList.get(i).getId()){
					FileInfo anotherFile = sharedList.get(i);
					Date myDate = file.getModificationTime();
					Date shDate = anotherFile.getModificationTime();
					int dateDiff = myDate.compareTo(shDate);
					if (dateDiff<0){
						if (anotherFile.isRemoved()) return 3;
						return 2;
					}
					if (dateDiff>0){
						return 1;
					}
					if (anotherFile.isRemoved()) return 3;
				}
			}
		}
		return 1;
	}
}
