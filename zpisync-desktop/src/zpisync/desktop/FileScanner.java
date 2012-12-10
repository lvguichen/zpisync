package zpisync.desktop;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import zpisync.shared.Util;

public class FileScanner implements Closeable {

	private static final Logger log = Logger.getLogger(FileScanner.class.getName());

	private File rootDirectory;
	private Path rootPath;

	private WatchService watchService;

	private static final Kind<?>[] AllEvents = new Kind[] { StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY,
			StandardWatchEventKinds.OVERFLOW };

	public FileScanner(File rootDirectory) {
		Util.require(rootDirectory.isDirectory());

		this.rootDirectory = rootDirectory;
		this.rootPath = rootDirectory.toPath();
	}

	public static void main(String[] args) {
		try (FileScanner scanner = new FileScanner(new File(Util.getHomeDir(), "Google Drive"))) {
			scanner.scan(new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					System.out.printf("Visiting: %s %s %s\n", scanner.relativize(file), attrs.size(),
							attrs.lastModifiedTime());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					log.log(Level.WARNING, "Cannot visit file: " + file, exc);
					return FileVisitResult.CONTINUE;
				}
			});
			scanner.addChangeListener(new ChangeListener() {
				@Override
				public void event(WatchEvent<Path> event) {
					System.out.printf("File changed: %s %s %d\n", event.kind(), event.context(), event.count());
				}
			});
			scanner.startWatcher();
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void scan(FileVisitor<? super Path> visitor) throws IOException {
		Util.require(rootDirectory.isDirectory());
		Files.walkFileTree(rootPath, visitor);
	}

	public void startWatcher() throws IOException {
		watchService = rootPath.getFileSystem().newWatchService();
		// XXX FILE_TREE is specific to SunJDK+Windows but there is no other way
		// to do this reliably
		watchKey = rootPath.register(watchService, AllEvents, com.sun.nio.file.ExtendedWatchEventModifier.FILE_TREE);

		watcherThread = new Thread(watcherLoop, "FS-Watcher");
		watcherThread.setDaemon(true);
		watcherThread.start();

		log.fine("FileWatcher started");
	}

	private WatchKey watchKey;
	private Thread watcherThread;
	private Runnable watcherLoop = new Runnable() {

		@Override
		public void run() {
			for (;;) {
				// wait for key to be signaled
				WatchKey key;
				try {
					key = watchService.take();
				} catch (InterruptedException x) {
					return;
				}

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					// TBD - provide example of how OVERFLOW event is handled
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						log.severe("Unhandled file watcher overflow");
						continue;
					}
					publish(createChangeEvent((WatchEvent<Path>) event, key));
				}

				// reset key return if directory no longer accessible
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}

		private void publish(WatchEvent<Path> changeEvent) {
			fireChange(changeEvent);
		}

		private WatchEvent<Path> createChangeEvent(WatchEvent<Path> event, WatchKey key) {
			return event;
		}
	};

	private List<ChangeListener> changeListeners = new ArrayList<>();

	public synchronized void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	public synchronized void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}

	protected void fireChange(WatchEvent<Path> event) {
		for (ChangeListener listener : changeListeners) {
			try {
				listener.event(event);
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	public interface ChangeListener extends EventListener {
		void event(WatchEvent<Path> event);
	}

	private class FakeWatchEvent implements WatchEvent<Path> {

		private WatchEvent.Kind<Path> kind;
		private int count;
		private Path context;

		public FakeWatchEvent(WatchEvent.Kind<Path> kind, int count, Path context) {
			super();
			this.kind = kind;
			this.count = count;
			this.context = context;
		}

		@Override
		public WatchEvent.Kind<Path> kind() {
			return kind;
		}

		@Override
		public int count() {
			return count;
		}

		@Override
		public Path context() {
			return context;
		}

	}

	@Override
	public void close() throws IOException {
		// TODO stop watcher
		if (watcherThread != null) {
			watcherThread.interrupt();
			try {
				watcherThread.join(2000);
				log.info("FileWatcher stopped");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			watcherThread = null;
		}

		if (watchKey != null) {
			watchKey.cancel();
			log.info("FileWatcher unregistered");
			watchKey = null;
		}
	}

	public Path relativize(Path file) {
		return rootPath.relativize(file);
	}

	public Path resolve(Path path) {
		return rootPath.resolve(path);
	}
}
