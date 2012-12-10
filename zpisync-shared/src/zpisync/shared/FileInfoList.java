package zpisync.shared;

import java.util.ArrayList;
import java.util.List;

public class FileInfoList extends ArrayList<FileInfo> {
	public FileInfoList() {
	}

	public FileInfoList(List<FileInfo> fileList) {
		super(fileList);
	}
}