package zpisync.desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {

	public static Element getChild(Element element, String tagName) {
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element child = (Element) node;
				if (child.getTagName().equals(tagName))
					return child;
			}
		}
		return null;
	}

	public static List<Element> getChildren(Element element, String tagName) {
		List<Element> children = new ArrayList<>();
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element child = (Element) node;
				if (tagName != null && child.getTagName().equals(tagName))
					children.add(child);
			}
		}
		return children;
	}

	public static String getText(Element element, String tagName) {
		return getText(element, tagName, null);
	}

	public static String getText(Element element, String tagName, String def) {
		Element child = getChild(element, tagName);
		String text = null;
		if (child != null)
			text = child.getTextContent();
		if (text == null)
			text = def;
		return text;
	}

	public static Element createElement(Element element, String tagName) {
		return createElement(element, tagName, null);
	}

	public static Element createElement(Element element, String tagName, String text) {
		Document doc = element.getOwnerDocument();
		Element child = doc.createElement(tagName);
		if (text != null)
			child.setTextContent(text);
		element.appendChild(child);
		return child;
	}

	public static void write(Document doc, File file) throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException, FileNotFoundException {
		write(doc, new FileOutputStream(file));
	}

	public static void write(Document doc, OutputStream output) throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException, FileNotFoundException {
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD, "xml");
		tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		tr.transform(new DOMSource(doc), new StreamResult(output));
	}

}
