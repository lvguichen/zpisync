package zpisync.shared.services;

public class Services {
	private static SyncService syncService;

	public static SyncService getSyncService() {
		return syncService;
	}

	public static void setSyncService(SyncService syncService) {
		Services.syncService = syncService;
	}
}
