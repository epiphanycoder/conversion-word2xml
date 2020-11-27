package org.ibfd.word2xml.kfus;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultDocumentType;
import org.dom4j.tree.DefaultElement;
import org.ibfd.word2xml.common.TreeContent;
import org.ibfd.word2xml.common.TreeContentRoot;
import org.ibfd.word2xml.common.CountriesXmlUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSXmlBuilder {

	private String destPath = null;
	
	public Document buildXml(KFUSWordData wd) throws Exception {

		Document doc = new DefaultDocument(new DefaultElement("kfus"));
		Element rootElement = doc.getRootElement();
		
		String countryDivName = wd.getCountryDivName();
		String cdcCode = null;
		if (StringUtils.isNotEmpty(countryDivName)) {
			cdcCode = CountriesXmlUtil.getInstance().getCDCCode(countryDivName);
			if (StringUtils.isNotEmpty(cdcCode)) {
				rootElement.addAttribute("id", "kfus_" + cdcCode); 
				destPath = "kfus_" + cdcCode+ ".xml";
			}
			
			String country = CountriesXmlUtil.getInstance().getCountry(countryDivName);
			String countryCode = CountriesXmlUtil.getInstance().getCountryCode(countryDivName);
			if (StringUtils.isNotEmpty(country) && StringUtils.isNotEmpty(countryCode)) {
				addEle(rootElement, "country", country, "cc", countryCode);
			}
			
			String type = CountriesXmlUtil.getInstance().getCountryDivType(countryDivName);
			if (StringUtils.isNotEmpty(type)) {
				Element countrydiv = addEle(rootElement, "countrydiv", "", "cdc", cdcCode, "type", type);
				addEle(countrydiv, "countrydivname", countryDivName);
			}
		}
		
		Element reviewed = addEle(rootElement, "reviewed");
		addEle(reviewed, "name", "Last reviewed:");
		
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        addEle(reviewed,"date",dateFormat.format(calendar.getTime()), "sortdate", sortDateFormat.format(calendar.getTime()));
		
		
		
		
		TreeContentRoot treeContentRoot = wd.getTreeContentRoot();
		List<TreeContent> rootChildren = treeContentRoot.getContentChildren();
		for (TreeContent treeContent : rootChildren) {
			if (treeContent instanceof KFUSHeading1) {
				KFUSHeading1 kFUSHeading1 = (KFUSHeading1) treeContent;
				Element element = kFUSHeading1.getElement();
				if (element != null) {
					rootElement.add(element);
				}
				
			}
		}		
	
		doc.setDocType(new DefaultDocumentType("kfus", "-//IBFD//ELEMENTS KEYFEATURE//EN", "http://dtd.ibfd.org/dtd/kf.dtd"));

		return doc;
	}

	/**
	 * 
	 * @return
	 */
	public String getDestPath() {
		return this.destPath;
	}
	
	/**
	 * 
	 * @param atEle
	 * @param eleName
	 * @param eleText
	 * @param att1Name
	 * @param att1Value
	 * @param att2Name
	 * @param att2Value
	 * @return
	 */
	private Element addEle(Element atEle, String eleName, String eleText, String att1Name, String att1Value, String att2Name, String att2Value) {
		Element ele = addEle(atEle, eleName, eleText, att1Name, att1Value);
		ele.addAttribute(att2Name, att2Value);
		return ele;
	}

	/**
	 * 
	 * @param atEle
	 * @param eleName
	 * @param eleText
	 * @param attName
	 * @param attValue
	 * @return
	 */
	private Element addEle(Element atEle, String eleName, String eleText, String attName, String attValue) {
		Element ele = addEle(atEle, eleName, eleText);
		ele.addAttribute(attName, attValue);
		return ele;
	}

	/**
	 * 
	 * @param atEle
	 * @param eleName
	 * @param eleText
	 * @return
	 */
	private Element addEle(Element atEle, String eleName, String eleText) {
		Element ele = addEle(atEle, eleName);
		for (Node node : KFUSUtil.str2nodes(eleText)) {
			ele.add(node);
		}
		return ele;
	}

	/**
	 * 
	 * @param atEle
	 * @param eleName
	 * @return
	 */
	private Element addEle(Element atEle, String eleName) {
		DefaultElement ele = new DefaultElement(eleName);
		atEle.add(ele);
		return ele;
	}

}
