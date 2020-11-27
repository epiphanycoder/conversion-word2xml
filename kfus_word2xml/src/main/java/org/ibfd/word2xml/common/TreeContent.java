package org.ibfd.word2xml.common;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public abstract class TreeContent {
	
	/**
	 * 
	 */
	private List<TreeContent> contentChildren = new ArrayList<TreeContent>();
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Element getElement() throws Exception;
	
	/**
	 * 
	 * @param tr
	 */
	public void addtableRowContentChild (TreeContent tr) {
		contentChildren.add(tr);
	}
	
	/**
	 * 
	 */
	public void tableRowContentClear (){
		if (contentChildren != null && contentChildren.size() > 0) {
			contentChildren.clear();
		}
	}

	/**
	 * @return the contentChildren
	 */
	public List<TreeContent> getContentChildren() {
		return contentChildren;
	}
	
}
