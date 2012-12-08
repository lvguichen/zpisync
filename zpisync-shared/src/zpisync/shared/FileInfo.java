package zpisync.shared;

import java.util.Date;

public class FileInfo {
	private long id;
	private long parentId;
	private String name;
	private Date changeTime;
	private Date modificationTime;
	private long size;

	private boolean removed;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (changeTime == null) {
			if (other.changeTime != null)
				return false;
		} else if (!changeTime.equals(other.changeTime))
			return false;
		if (id != other.id)
			return false;
		if (modificationTime == null) {
			if (other.modificationTime != null)
				return false;
		} else if (!modificationTime.equals(other.modificationTime))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentId != other.parentId)
			return false;
		if (removed != other.removed)
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public long getId() {
		return id;
	}

	public Date getModificationTime() {
		return modificationTime;
	}

	public String getName() {
		return name;
	}

	public long getParentId() {
		return parentId;
	}

	public long getSize() {
		return size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changeTime == null) ? 0 : changeTime.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((modificationTime == null) ? 0 : modificationTime.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (parentId ^ (parentId >>> 32));
		result = prime * result + (removed ? 1231 : 1237);
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", parentId=" + parentId + ", name=" + name + ", removed=" + removed + "]";
	}

}
