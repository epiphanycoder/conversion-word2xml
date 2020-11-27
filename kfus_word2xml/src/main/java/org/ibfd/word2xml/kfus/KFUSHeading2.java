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
public class KFUSHeading2 extends TreeContent {
	
	/**
	 * XML element name
	 */
	private String elementName = null;
	
	/**
	 * XML id Attribute
	 */
	private String id = null;
	
	/**
	 * XML title element
	 */
	private String title = null;
	
	/**
	 * For future purpose
	 * XML extxref element's collection attribute
	 */
	private String extxrefCollectionAttr = null;
	
	/**
	 * For future purpose
	 * XML extxref element's target attribute
	 */
	private String extxrefTargetAttr = null;

	/**
	 * 
	 */
	@Override
	public Element getElement() {
		MyElement element = null;
		if (StringUtils.isNotEmpty(this.elementName)){
			element = new MyElement(this.elementName);
		}
		if (element == null) return (null);
		element.addAttribute("id", getId());
		//element.addAttribute("display", "heading2");
		
		MyElement titleEle = element.addEle("title");
		if(StringUtils.isNotEmpty(this.extxrefCollectionAttr) && StringUtils.isNotEmpty(this.extxrefTargetAttr)){
			MyElement extxref = titleEle.addEle("extxref", this.title, "collection", this.extxrefCollectionAttr);
			extxref.addAttribute("target", this.extxrefTargetAttr);
		}else{
			titleEle.setText(title);
		}
		
		List<TreeContent> contentChildren2 = this.getContentChildren();
		for (TreeContent treeContent : contentChildren2) {
			if (treeContent instanceof KFUSNormal) {
				KFUSNormal kFUSNormal = (KFUSNormal) treeContent;
				element.add(kFUSNormal.getElement());
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
	 * @return the extxrefCollectionAttr
	 */
	public String getExtxrefCollectionAttr() {
		return extxrefCollectionAttr;
	}

	/**
	 * @param extxrefCollectionAttr the extxrefCollectionAttr to set
	 */
	public void setExtxrefCollectionAttr(String extxrefCollectionAttr) {
		this.extxrefCollectionAttr = extxrefCollectionAttr;
	}

	/**
	 * @return the extxrefTargetAttr
	 */
	public String getExtxrefTargetAttr() {
		return extxrefTargetAttr;
	}

	/**
	 * @param extxrefTargetAttr the extxrefTargetAttr to set
	 */
	public void setExtxrefTargetAttr(String extxrefTargetAttr) {
		this.extxrefTargetAttr = extxrefTargetAttr;
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

//	/**
//	 * @return the nameValuePairs
//	 */
//	public List<KFCANormal> getNameValuePairs() {
//		return nameValuePairs;
//	}
//
//	public void addNameValuePair(KFCANormal normal) {
//		nameValuePairs.add(normal);
//	}


}
