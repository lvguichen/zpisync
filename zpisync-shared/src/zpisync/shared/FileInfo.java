package zpisync.shared;

import java.io.File;
import java.util.Date;

public class FileInfo {
	private String path;
	private String name;
	private Date modificationTime;
	private long size;

	private boolean removed;
	
	public FileInfo(){
		super();
	}
	
	public FileInfo(File file, String zpisDIR){
		path = file.getAbsolutePath();
		path = path.substring(path.lastIndexOf(zpisDIR)+zpisDIR.length()+1);
		name = file.getName();
		modificationTime = new Date(file.lastModified());
		size = file.length();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public String getPath() {
		return path;
	}

	public Date getModificationTime() {
		return modificationTime;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "FileInfo [path=" + path + ", removed=" + removed + "]";
	}

}
