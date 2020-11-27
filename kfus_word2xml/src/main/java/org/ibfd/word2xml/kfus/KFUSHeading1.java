package org.ibfd.word2xml.kfus;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.ibfd.word2xml.common.MyElement;
import org.ibfd.word2xml.common.TreeContent;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSHeading1 extends TreeContent {

	/**
	 * XML element name
	 */
	private String elementName = null;
	
	/**
	 * Xml title content
	 */
	private String title = null;
	
	/**
	 * XML id attribute
	 */
	private String id = null;
	
	/**
	 * 
	 */
	public KFUSHeading1(){
		
	}
	
	/**
	 * 
	 */
	@Override
	public Element getElement() throws Exception {
		MyElement element = null;
		if (StringUtils.isNotEmpty(this.elementName)){
			element = new MyElement(this.elementName);
		}
		if (element == null) return (null);
		element.addAttribute("id", this.id);
		//element.addAttribute("display", "heading1");
		
		element = addTaxTopics(element);
		
		element.addEle("title", this.title);
		
		List<TreeContent> contentChildren2 = this.getContentChildren();
		for (TreeContent treeContent : contentChildren2) {
			if (treeContent instanceof KFUSHeading2) {
				KFUSHeading2 kFUSHeading2 = (KFUSHeading2) treeContent;
				Element heading2Ele = kFUSHeading2.getElement();
				if (heading2Ele != null){
					element.add(heading2Ele);
				}
			}else if(treeContent instanceof KFUSNormal){
				KFUSNormal kFUSNormal = (KFUSNormal) treeContent;
				Element normalEle = kFUSNormal.getElement();
				if (normalEle != null) {
					element.add(normalEle);
				}
			}
		}
		
		return element;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 
	 * <kfca_companies id="kfca_companies_ns">
	 * <taxtopics>
	 *     <taxtopic tc="1_1_22_2" score="3">Local / Municipal / Cantonal</taxtopic>
	 *     <taxtopic tc="1_1_21_2" score="3">Local / Municipal / Cantonal</taxtopic>
	 * </taxtopics>
	 * 
	 * <kfca_individuals id="kfca_individuals_ns">
	 * <taxtopics>
     *     <taxtopic tc="1_2_21_2" score="3">Local / Municipal / Cantonal</taxtopic>
     *     <taxtopic tc="1_2_22_2" score="3">Local / Municipal / Cantonal</taxtopic>
     * </taxtopics>
	 * 
	 * <kfca_other_taxes id="kfca_ns_other_taxes">
     * <taxtopics>
     *     <taxtopic tc="1_5" score="3">Other Taxes</taxtopic>
     * </taxtopics>
	 * 
	 * <kfca_turnover_taxes id="kfca_turnover_taxes_al">
     * <taxtopics>
     *     <taxtopic tc="1_4" score="3">VAT</taxtopic>
     * </taxtopics>
	 * @param heading1Element
	 * @return
	 */
	private MyElement addTaxTopics (MyElement heading1Element) {
		
		//create a taxtopics element
		MyElement taxtopicsElement = heading1Element.addEle("taxtopics");
		
		//add taxtopic elements
		if (heading1Element.getName().equals(KFUSAppConstant.XML_HEADING1_A_COMPANIES)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1_22_2", "3", "Local / Municipal / Cantonal");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1_21_2", "3", "Local / Municipal / Cantonal");
		}
		else if (heading1Element.getName().equals(KFUSAppConstant.XML_HEADING1_B_INDIVIDUALS)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_2_21_2", "3", "Local / Municipal / Cantonal");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_2_22_2", "3", "Local / Municipal / Cantonal");
		}
		else if (heading1Element.getName().equals(KFUSAppConstant.XML_HEADING1_C_OTHER_TAXES)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_5", "3", "Other Taxes");
		}
		else if (heading1Element.getName().equals(KFUSAppConstant.XML_HEADING1_D_TURNOVER_TAXES)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_4", "3", "VAT");
		}
		return heading1Element;
	}
	
	/**
	 * 
	 * @param taxtopicsElement
	 * @param tc
	 * @param score
	 * @param text
	 * @return
	 */
	private MyElement addTaxTopic (MyElement taxtopicsElement, String tc, String score, String text) {
		Element taxtopic = taxtopicsElement.addEle("taxtopic", text);
		taxtopic.addAttribute("tc", tc);
		taxtopic.addAttribute("score", score);
		return taxtopicsElement;
	}

}
