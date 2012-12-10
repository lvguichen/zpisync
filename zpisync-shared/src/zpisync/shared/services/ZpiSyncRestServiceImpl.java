package zpisync.shared.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.Directory;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;

import zpisync.shared.FileInfo;
import zpisync.shared.FileInfoList;
import zpisync.shared.Util;

public class ZpiSyncRestServiceImpl {

	private static final Logger log = Logger.getLogger(ZpiSyncRestServiceImpl.class.getName());

	public static void main(String[] args) throws Exception {

		ZpiSyncRestServiceImpl service = new ZpiSyncRestServiceImpl();
		service.setAuthSecret("secret"); // should be PIN
		service.setDataDir(new File(Util.getHomeDir(), "ZpiDrive"));
		service.start();

		System.out.println("Press enter to exit");
		System.in.read();

		service.stop();
	}

	/**
	 * Initialize library on Android.
	 */
	public static void initOnAndroid() {
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
	}

	File dataDir;
	Component component;
	public Server server;

	boolean enableAuthentication = false;
	String authUser = "zpisync";
	String authSecret = "";

	public void start() throws Exception {
		component = new Component();
		server = component.getServers().add(Protocol.HTTP, 0);
		component.getClients().add(Protocol.FILE);

		component.getDefaultHost().attach("/sync/demo", SyncDemoService.class);
		component.getDefaultHost().attach("/sync/info", SyncInfoService.class);
		
		component.getDefaultHost().attach("/sync/lastMod", SyncLastModDateService.class);
		component.getDefaultHost().attach("/sync/fileInfo", SyncGetFileInfo.class);		
		component.getDefaultHost().attach("/sync/fileList", SyncModFilesService.class);

		// XXX completely insecure
		// TODO add SSL
		// TODO add PIN authentication
		if (enableAuthentication) {
			ChallengeAuthenticator guard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "ZPISYNC");
			MapVerifier mapVerifier = new MapVerifier();
			// Load a single static login/secret pair.
			mapVerifier.getLocalSecrets().put(authSecret, authSecret.toCharArray());
			guard.setVerifier(mapVerifier);

			component.getDefaultHost().attachDefault(guard);
		}

		Directory directory = new Directory(component.getContext().createChildContext(), toFileUrl(getDataDir()));
		directory.setListingAllowed(true);
		directory.setModifiable(true);
		component.getDefaultHost().attach("/data/", directory);

		component.start();

		log.info(String.format("Server running at http://localhost:%d/\n", server.getEphemeralPort()));
	}

	public void stop() throws Exception {
		component.stop();
		log.info("Server stopped");
	}

	private static String toFileUrl(File file) {
		return file.toURI().toString().replaceFirst("^file:/", "file:///");
	}

	public String getAuthSecret() {
		return authSecret;
	}

	public void setAuthSecret(String authSecret) {
		this.authSecret = authSecret;
	}

	public File getDataDir() {
		return dataDir;
	}

	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;
	}

	public static class SyncDemoService extends ServerResource {

		@Get
		public String toString() {
			// Print the requested URI path
			return String.format("Resource URI  : %s\nRoot URI      : %s\nRouted part   : %s\nRemaining part: %s\n",
					getReference(), getRootRef(), getReference().getBaseRef(), getReference().getRemainingPart());
		}

	}

	public static class SyncInfoService extends ServerResource {
		@Get
		public FileInfo retrieve() {
			FileInfo fi = new FileInfo();
			fi.setPath("Foo/Bar.txt");
			fi.setName("Bar.txt");
			fi.setSize(1234);
			fi.setModificationTime(new Date());
			return fi;
		}
	}
	
	public interface ISyncLastModDateService {
		@Get
		long retrieve();
	}
	public static class SyncLastModDateService extends ServerResource implements ISyncLastModDateService {
		@Get
		public long retrieve() {
			return Services.getSyncService().getLastModificationDate().getTime();
		}
	}
	
	public interface ISyncGetFileInfo {
		@Get
		FileInfo retrieve();
	}
	public static class SyncGetFileInfo extends ServerResource implements ISyncGetFileInfo {
		@Get
		public FileInfo retrieve() {
			String path = this.getQuery().getValues("path");
			return Services.getSyncService().getFileInfo(path);
		}
	}
	
	public interface ISyncModFilesService {
		@Get("json")
		FileInfo[] retrieve();
	}
	public static class SyncModFilesService extends ServerResource implements ISyncModFilesService {
		@Get("json")
		public FileInfo[] retrieve() {
			String date = this.getQuery().getValues("since");
			long time = 0;
			if (!Util.isNullOrEmpty(date))
				time = Long.parseLong(date);
			Date mod = new Date(time);
			return Services.getSyncService().getFileList(mod).toArray(new FileInfo[0]);
		}
	}
}
