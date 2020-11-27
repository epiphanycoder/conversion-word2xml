package org.ibfd.word2xml.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultText;
import org.dom4j.tree.FlyweightProcessingInstruction;
import org.ibfd.word2xml.kfus.KFUSMain;
import org.xml.sax.SAXException;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class DocUtils {
	
	/**
	 * 
	 * @return
	 */
	public synchronized static Document newInstance() {
		return DocumentHelper.createDocument();
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	public synchronized static Document readDocument(File f) {
		
		try {
			System.out.println("Loading file - " + f);
			return getReader().read(f);
		} catch (Exception e) {
			KFUSMain.handleException(e);
			return null;
		}
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public synchronized static Element string2Element(String s) {
		try {
			InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));
			Document doc = getReader().read(isr);
			return doc.getRootElement();
		} catch (DocumentException e) {
			KFUSMain.handleException(e);
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	private static SAXReader getReader() {
		String feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
		SAXReader reader = new SAXReader();
		reader.setValidation(false);
		try {
			reader.setFeature(feature, false);
		} catch (SAXException e) {
			KFUSMain.handleException(e);
		}
		return reader;
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public synchronized static Document readDocument(String uri) {
		try {
			//System.out.println("Loading file - " + uri);
			return getReader().read(uri);
		} catch (Exception e) {
			KFUSMain.handleException(e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static boolean isValid(File f) throws Exception {
		System.out.println("Validating " + f.getAbsolutePath()+ "\n");
		SAXReader reader = new SAXReader();
		reader.setValidation(true);
		reader.setErrorHandler(new ValidationErrorHandler());
		Document doc = reader.read(f);
		boolean isNotNull = (doc != null);
		
		return (isNotNull);
	}

	/**
	 * by default, dom4j XMLWriter writes processing instructions after doctype
	 * declaration. this method writes processing instructions before doctype
	 * declaration.
	 * 
	 * @param doc
	 * @param df
	 */
	public synchronized static void writeDocument(Document doc, File df) {
		//System.out.println((df.exists() ? "Overwriting" : "Creating") + " file - " + df);
		df.getParentFile().mkdirs();
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setTrimText(false);
			format.setIndentSize(4);
			XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(df), "UTF-8"),format);
			FlyweightProcessingInstruction xmlDeclaration = new FlyweightProcessingInstruction("xml",
					"version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ");

			writer.write(xmlDeclaration);
			writer.write(doc.processingInstructions());
			writer.write(doc.getDocType());
			writer.write(doc.getRootElement());
			writer.close();
			
			if(!DocUtils.isValid(df)) {
				throw new Exception (df.getAbsolutePath() + " has dtd validation error.");
			}
			
		} catch (Exception e) {
			KFUSMain.handleException(e);
		}
	}

	/**
	 * 
	 * @param doc
	 * @param elementName
	 */
	@SuppressWarnings("unchecked")
	public static void removeElement(Document doc, String elementName) {
		List elementsToRemove = doc.selectNodes("//" + elementName);

		for (Iterator iter = elementsToRemove.iterator(); iter.hasNext();) {
			Element elementToRemove = (Element) iter.next();

			List elementToRemovesSiblings = elementToRemove.getParent().elements();
			for (int i = 0; i < elementToRemovesSiblings.size(); i++) {
				if (elementToRemovesSiblings.get(i) == elementToRemove) {
					elementToRemovesSiblings.set(i, new DefaultText(elementToRemove.getText()));
				}
			}
		}
	}

	/**
	 * 
	 * @param doc
	 * @param tagName
	 * @param attName
	 * @return
	 */
	public synchronized static String getAttValue(Document doc, String tagName, String attName) {
		try {
			return ((Element) doc.selectSingleNode("//" + tagName)).attributeValue(attName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param doc
	 * @param tagName
	 * @param attName
	 * @param attValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized static org.dom4j.Element getElementByTagNameAndAttValue(Document doc, String tagName,
			String attName, String attValue) {
		try {
			List<Element> sections = doc.selectNodes("//" + tagName);
			for (int i = 0; i < sections.size(); i++) {
				Element e = sections.get(i);
				if (e.attributeValue(attName) != null && (e.attributeValue(attName)).equals(attValue)) {
					return e;
				}
			}
		} catch (Exception e) {
			KFUSMain.handleException(e);
			return null;
		}

		return null;
	}
	
}
