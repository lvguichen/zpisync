package zpisync.desktop.models;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import zpisync.desktop.ModelBase;
import zpisync.desktop.XmlUtil;
import zpisync.shared.Util;

public class PreferencesModel extends ModelBase {

	private final static Logger log = Logger.getLogger(PreferencesModel.class.getName());

	private String pin;
	private File dataDir;

	private List<DeviceInfoModel> knownDevices = new ArrayList<>();

	public PreferencesModel() {
		this.pin = generatePin();
		this.dataDir = new File(Util.getHomeDir(), "ZpiDrive");

		String customDataDir = System.getProperty("zpisync.datadir");
		if (customDataDir != null)
			this.dataDir = new File(customDataDir);
	}

	public static String generatePin() {
		StringBuilder sb = new StringBuilder();
		Random rnd = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(rnd.nextInt(9));
		}
		return sb.toString();
	}
	
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public File getDataDir() {
		return dataDir;
	}

	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;
	}

	public List<DeviceInfoModel> getKnownDevices() {
		return knownDevices;
	}

	public void load(File file) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(file);
			Element doc = dom.getDocumentElement();

			load(doc);

		} catch (Exception e) {
			log.log(Level.SEVERE, "Unable to load preferences", e);
			throw new Error(e);
		}

	}

	public void save(File file) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.newDocument();
			Element doc = dom.createElement("preferences");
			dom.appendChild(doc);

			save(doc);
			XmlUtil.write(dom, file);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Unable to save preferences", e);
			throw new Exception(e);
		}

	}

	private void load(Element root) {
		setDataDir(new File(XmlUtil.getText(root, "data-dir")));
		setPin(XmlUtil.getText(root, "pin"));
		if (Util.isNullOrEmpty(getPin()))
			setPin(generatePin());

		Element knownDevs = XmlUtil.getChild(root, "known-devices");
		for (Element knownDev : XmlUtil.getChildren(knownDevs, "device")) {
			DeviceInfoModel devInfo = new DeviceInfoModel();
			devInfo.setDisplayName(knownDev.getAttribute("name"));
			devInfo.setUdn(knownDev.getAttribute("udn"));
			devInfo.setTrusted(true);
			try {
				devInfo.setLastSyncTime(DateFormat.getInstance().parse(knownDev.getAttribute("last-sync")));
			} catch (ParseException e) {
				devInfo.setLastSyncTime(null);
				log.log(Level.SEVERE, "Parse error", e);
			}
		}
	}

	private void save(Element root) {
		XmlUtil.createElement(root, "data-dir", getDataDir().getAbsolutePath());
		XmlUtil.createElement(root, "pin", getPin());

		Element knownDevs = XmlUtil.createElement(root, "known-devices");
		for (DeviceInfoModel devInfo : getKnownDevices()) {
			if (devInfo.isTrusted()) {
				Element knownDev = XmlUtil.createElement(knownDevs, "device");
				knownDev.setAttribute("name", devInfo.getDisplayName());
				knownDev.setAttribute("udn", devInfo.getUdn());
				if (devInfo.getLastSyncTime() != null)
					knownDev.setAttribute("last-sync", DateFormat.getInstance().format(devInfo.getLastSyncTime()));
			}
		}
	}
}
