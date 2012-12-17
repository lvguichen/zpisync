package zpisync.desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.teleal.common.io.IO;

public class HttpUtil {

	public static void main(String[] args) throws Exception {
		downloadUrl(new URL("http://www.google.com/"), System.out);
	}

	public static void downloadUrlFast(URL url, File file, boolean useProxy) throws IOException, FileNotFoundException {
		try (ReadableByteChannel rbc = Channels.newChannel(openConnection(url, useProxy).getInputStream())) {
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			}
		}
	}

	private static URLConnection openConnection(URL url, boolean useProxy) throws IOException {
		if (useProxy)
			return url.openConnection();
		return url.openConnection(Proxy.NO_PROXY);
	}

	public static void downloadUrl(URL url, OutputStream out) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url.toString());
		HttpResponse httpResponse = httpClient.execute(httpGet);

		try (InputStream in = httpResponse.getEntity().getContent()) {
			IO.copy(in, out);
		}
	}

	public static void downloadUrl(URL url, File file) throws IOException, FileNotFoundException {
		try (FileOutputStream fout = new FileOutputStream(file)) {
			downloadUrl(url, fout);
		}
	}

}
