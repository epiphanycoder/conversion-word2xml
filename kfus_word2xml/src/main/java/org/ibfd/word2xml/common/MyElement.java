package org.ibfd.word2xml.common;

import org.dom4j.tree.DefaultElement;

/**
 * 
 * Legacy code: For dom4j elements' handling
 *
 */
public class MyElement extends DefaultElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param name
	 */
	public MyElement(String name) {
		super(name);
	}

	/**
	 * 
	 * @param name
	 * @param text
	 */
	public MyElement(String name, String text) {
		this(name);
		this.setText(text);
	}
	
	/**
	 * 
	 * @param name
	 * @param text
	 * @param attName
	 * @param attValue
	 */
	public MyElement(String name, String text, String attName, String attValue) {
		this(name, text);
		this.addAttribute(attName, attValue);
	}

	/**
	 * 
	 * @param eleName
	 * @param attName
	 * @param attValue
	 * @return
	 */
	public MyElement addEle(String eleName, String attName, String attValue) {
		return addEle(eleName, "", attName, attValue);
	}

	/**
	 * 
	 * @param eleName
	 * @param eleText
	 * @param attName
	 * @param attValue
	 * @return
	 */
	public MyElement addEle(String eleName, String eleText, String attName, String attValue) {
		MyElement ele = addEle(eleName, eleText);
		ele.addAttribute(attName, attValue);
		return ele;
	}

	/**
	 * 
	 * @param eleName
	 * @param eleText
	 * @return
	 */
	public MyElement addEle(String eleName, String eleText) {
		MyElement ele = addEle(eleName);
		ele.setText(eleText);
		return ele;
	}
	
	/**
	 * 
	 * @param eleName
	 * @return
	 */
	public MyElement addEle(String eleName) {
		MyElement ele = new MyElement(eleName);
		this.add(ele);
		return ele;
	}
}