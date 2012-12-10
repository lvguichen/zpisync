package zpisync.desktop.models;

import java.util.Date;

import zpisync.desktop.ModelBase;

public class DeviceInfoModel extends ModelBase {
	String udn;
	boolean trusted;
	boolean active;
	String displayName;
	String syncUrl;
	Date lastSyncTime;
	Date lastModified;

	public String getUdn() {
		return udn;
	}

	public void setUdn(String udn) {
		this.udn = udn;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSyncUrl() {
		return syncUrl;
	}

	public void setSyncUrl(String syncUrl) {
		this.syncUrl = syncUrl;
	}

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "DeviceInfoModel [udn=" + udn + ", trusted=" + trusted + ", active=" + active + ", displayName="
				+ displayName + "]";
	}
}
