package org.ibfd.word2xml.kfus;

import org.ibfd.word2xml.common.TreeContent;
import org.ibfd.word2xml.common.TreeContentRoot;


/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSWordData {
	
	/**
	 * country div name of the doc file which is used to find
	 * <countrydivname in the countries.xml file 
	 */
	private String countryDivName = null;
	
	/**
	 * For future use
	 */
	private String reviewedName = null;
	
	/**
	 * For future use
	 */
	private String reviewedSortDate = null;
	
	/**
	 * For future use.
	 */
	private String reviewedDate = null;
	
	/**
	 * Tree root
	 */
	private TreeContentRoot treeContentRoot = null;
	
	/**
	 * 
	 */
	public KFUSWordData() {
		
	}
	
	/**
	 * 
	 */
	public void tableRowContentClear() {
		  treeContentRoot.tableRowContentClear();
	}

	/**
	 * @return the reviewedName
	 */
	public String getReviewedName() {
		return reviewedName;
	}

	/**
	 * @param reviewedName the reviewedName to set
	 */
	public void setReviewedName(String reviewedName) {
		this.reviewedName = reviewedName;
	}

	/**
	 * @return the reviewedSortDate
	 */
	public String getReviewedSortDate() {
		return reviewedSortDate;
	}

	/**
	 * @param reviewedSortDate the reviewedSortDate to set
	 */
	public void setReviewedSortDate(String reviewedSortDate) {
		this.reviewedSortDate = reviewedSortDate;
	}

	/**
	 * @return the reviewedDate
	 */
	public String getReviewedDate() {
		return reviewedDate;
	}

	/**
	 * @param reviewedDate the reviewedDate to set
	 */
	public void setReviewedDate(String reviewedDate) {
		this.reviewedDate = reviewedDate;
	}

	/**
	 * @return the treeContentRoot
	 */
	public TreeContentRoot getTreeContentRoot() {
		return treeContentRoot;
	}

	/**
	 * @param treeContentRoot the treeContentRoot to set
	 */
	public void setTreeContentRoot(TreeContentRoot treeContentRoot) {
		this.treeContentRoot = treeContentRoot;
	}
	
	public void addTableRowContent( TreeContent treeContent) {
		this.treeContentRoot.addtableRowContentChild(treeContent);
	}

	/**
	 * @param countryDivName the countryDivName to set
	 */
	public void setCountryDivName(String countryDivName) {
		this.countryDivName = countryDivName;
	}
	
	/**
	 * @return the countryDivName
	 */
	public String getCountryDivName() {
		return countryDivName;
	}
	
}
