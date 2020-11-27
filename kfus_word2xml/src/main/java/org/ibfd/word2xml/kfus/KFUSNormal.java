package org.ibfd.word2xml.kfus;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.ibfd.word2xml.common.MyElement;
import org.ibfd.word2xml.common.TreeContent;


/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSNormal extends TreeContent{

	/**
	 * Xml element name 
	 */
	private String elementName = null;
	
	/**
	 * For future use. From experience of TaxpenWord2Xml we found it's use
	 */
	private boolean isTaxRatesChild = false;
	
	/**
	 * Xml name element
	 */
	private String name = null;
	
	/**
	 * Xml name element can contain extxref. 
	 * collection attr is for future use. 
	 */
	private String extxrefCollectionAtt = null;
	
	/**
	 * extxref's target attribute
	 */
	private String extxrefTargetAttr = null;
	
	/**
	 * element's id attribute
	 */
	private String id = null;
	
	/**
	 * values
	 */
	private List<String> values = new ArrayList<String>();
	

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
		//element.addAttribute("display", "row");
		element.addAttribute("id", getId());
		
		MyElement name = element.addEle("name");
		if (StringUtils.isNotEmpty(this.extxrefTargetAttr)){
			name.addEle("extxref", this.name.trim(),"target", this.extxrefTargetAttr);
			
		}else{
			name.setText(this.name.trim());
		}
		MyElement valuesEle = element.addEle("values");
		String childName = "value";
		if (isTaxRatesChild){
			childName = "rate";
		}
		for (String value : this.values) {
			MyElement childEle = valuesEle.addEle(childName);
			ArrayList<Node> nodes = KFUSUtil.str2nodes(value);
			for (Node node : nodes) {
				childEle.add(node);
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
	 * @return the isTaxRatesChild
	 */
	public boolean isTaxRatesChild() {
		return isTaxRatesChild;
	}

	/**
	 * @param isTaxRatesChild the isTaxRatesChild to set
	 */
	public void setTaxRatesChild(boolean isTaxRatesChild) {
		this.isTaxRatesChild = isTaxRatesChild;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

	/**
	 * @return the extxrefCollectionAtt
	 */
	public String getExtxrefCollectionAtt() {
		return extxrefCollectionAtt;
	}

	/**
	 * @param extxrefCollectionAtt the extxrefCollectionAtt to set
	 */
	public void setExtxrefCollectionAtt(String extxrefCollectionAtt) {
		this.extxrefCollectionAtt = extxrefCollectionAtt;
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

}
