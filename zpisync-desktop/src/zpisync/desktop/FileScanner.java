package zpisync.desktop;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			scanner.scan();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void scan() throws IOException {
		Util.require(rootDirectory.isDirectory());

		Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.printf("Visiting: %s %s %s\n", rootPath.relativize(file), attrs.size(),
						attrs.lastModifiedTime());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				log.log(Level.WARNING, "Cannot visit file: " + file, exc);
				return FileVisitResult.CONTINUE;
			}
		});

		if (true)
			return;
		watchService = rootPath.getFileSystem().newWatchService();
		WatchKey watchKey = rootPath.register(watchService, AllEvents);
	}

	@Override
	public void close() throws IOException {
		// TODO stop watcher
	}
}
