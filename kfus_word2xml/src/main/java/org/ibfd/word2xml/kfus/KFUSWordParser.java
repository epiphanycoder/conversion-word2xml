package org.ibfd.word2xml.kfus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.ibfd.word2xml.common.TreeContentRoot;
import org.ibfd.word2xml.common.CountriesXmlUtil;

import com.aspose.words.Cell;
import com.aspose.words.CellCollection;
import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.Node;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphCollection;
import com.aspose.words.Row;
import com.aspose.words.Shading;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSWordParser extends DocumentVisitor {
	/**
	 * Store KFCAWordData
	 */
	private KFUSWordData kFUSWordData;
	
	/**
	 * KFCAHeading1 means for example A. Companies .. 
	 */
	private KFUSHeading1 kFUSHeading1 = null;
	
	/**
	 * KFCAHeading2 means for example 4. Income subject to tax 
	 */
	private KFUSHeading2 kFUSHeading2 = null;
	
	/**
	 * KFCANormal means name value pairs...
	 */
	private KFUSNormal kFUSNormal = null;
	
	/**
	 * Checks whether first row is passed or not
	 */
	private boolean isFirstRowPassed = false;
	
	/**
	 * File Cdc code means &lt;cdc/> value from countris.xml file in online
	 * 
	 * For example
	 * For countryDivName = "Alberta" fileCdcCode is "ab".
	 * 
	 *&lt;country treaty="yes">
     *    &lt;shortname>Canada&lt;/shortname>
     *    &lt;name>CANADA&lt;/name>
     *    &lt;code>ca&lt;/code>
     *    &lt;countrydiv>
	 *        &lt;countrydivname>Provincial Taxation&lt;/countrydivname>
	 *            &lt;cdc>prvn&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *        &lt;/countrydiv>
     *        &lt;countrydiv>
	 *            &lt;countrydivname>Alberta&lt;/countrydivname>
	 *            &lt;cdc>ab&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *    &lt;/countrydiv>
     *&lt;/country>
	 */
	private String fileCdcCode = null;
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public KFUSWordData parse(String fileName) {
		kFUSWordData = new KFUSWordData();
		isFirstRowPassed = false;

		Document doc;
		try {
			kFUSWordData.setTreeContentRoot(new TreeContentRoot());
			doc = new Document(fileName);
			doc.acceptAllRevisions();
			
		} catch (Exception e) {
			KFUSMain.handleException(e);
			return null;
		}

		try {
			doc.accept(this);
		} catch (Exception e) {
			KFUSMain.handleException(e);
			return null;
		}

		return kFUSWordData;
	}

	/**
	 *  Visits all rows....
	 */
	@Override
	 public int visitRowStart(Row row) throws Exception{
		//System.out.println(row.getText() + " cell foreground color: " + row.getCells().get(0).getCellFormat().getShading().getForegroundPatternColor());
		//System.out.println(row.getText() + " cell background color: " + row.getCells().get(0).getCellFormat().getShading().getBackgroundPatternColor());
		if (!isFirstRowPassed && isFirstRow(row)) {
			processFirstRow(row);
			isFirstRowPassed = true;
		}
		else if (isFirstRowPassed) {
			if (isHeading1(row)){
				processHeading1(row);
			}
			else if (isHeading2(row)){
				processHeading2(row);
			}
			else if (isNormal(row)){
				processNormal(row);
			}
		}
		return super.visitRowStart(row);
	}
	
	/**
	 * 
	 * @param row
	 */
	private void processNormal(Row row) {
		this.kFUSNormal = new KFUSNormal();
		CellCollection cells = row.getCells();
		
		//Left cell contains name
		Cell leftCell = cells.get(0);
		String name = leftCell.getText();
		
		if (StringUtils.isEmpty(name)) {return;}
		name = name.trim();
		if (StringUtils.isEmpty(name)) {return;}//check again after trimming.
		
		kFUSNormal.setName(KFUSUtil.cleanText(name));
		
		// Special Case
		// If left column contains 1. Corporate income tax rates then
		// name element will have <extxref like the following.
		/*
		 * <kfus_corp_tax_rates id="kfus_corp_tax_rates_ns">
		 *	<name>
		 *		<extxref target="nathsuba_ca_ns_s_1.1.">1. Corporate income tax rates</extxref>
		 *	</name>
		 * */
		
		
		// Right cell contains values
		Cell rightCell = cells.get(1);
		List<String> values = new ArrayList<String>();
		for (Node node : rightCell.getParagraphs()) {
			Paragraph para = (Paragraph)node;
			String paraText = para.getText();
			String[] splitedParaText = paraText.split(Character.toString(((char)11)));
			int len = splitedParaText.length;
			for(int i=0; i<len; i++){
				values.add(KFUSUtil.cleanText(splitedParaText[i]));
			}
		}
		kFUSNormal.setValues(values);
		String[] elementNameAndIdAndExtxrefTargetForNormalRow = getElementNameAndIdAndExtxrefTargetForNormalRow(name);
		if (elementNameAndIdAndExtxrefTargetForNormalRow == null) {
			return;
		}
		kFUSNormal.setElementName(elementNameAndIdAndExtxrefTargetForNormalRow[0]);
		kFUSNormal.setId(elementNameAndIdAndExtxrefTargetForNormalRow[1]);
		kFUSNormal.setExtxrefTargetAttr(elementNameAndIdAndExtxrefTargetForNormalRow[2]);
		
		
		/**
		 * If left column value of KFCANormal starts with a Number and a dot then add it to KFCAHeading1
		 * otherwise add it to KFCAHeading2
		 * 
		 */
		if (doesNameStartWithNumber (name)){
			if (this.kFUSHeading1 != null){
				this.kFUSHeading1.addtableRowContentChild(kFUSNormal);
			}
		}else if (this.kFUSHeading2 != null) {
				this.kFUSHeading2.addtableRowContentChild(kFUSNormal);
		}
		
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean doesNameStartWithNumber(String name) {
		if (name == null) {
			return false;
		}
		Pattern pat = Pattern.compile("^\\s*([0-9]+)\\..*$");
		Matcher fit = pat.matcher(name);
        if (fit.matches() && fit.groupCount() == 1) {
        	return true;
        }
		return false;
	}
	
	

	/**
	 * 
	 * @param row
	 */
	private void processHeading2(Row row) {
		this.kFUSHeading2 = new KFUSHeading2();
		String rowText = row.getText();
		
		if (KFUSUtil.isStr1ContainsStr2(rowText, "3. Income subject to tax")){
			setHeading2("kfus_income_subject_ind", rowText, ("kfus_income_subject_ind_"+this.fileCdcCode), "");
		}
		
		kFUSHeading1.addtableRowContentChild(kFUSHeading2);
	}

	/**
	 * 
	 * @param elementName
	 * @param title
	 * @param extxrefTargetAttrPostfix
	 */
	private void setHeading2 (String elementName, String title, String id, String extxrefTargetAttrPostfix) {
				
		this.kFUSHeading2.setElementName(elementName);
		this.kFUSHeading2.setId(id);
		this.kFUSHeading2.setTitle(KFUSUtil.cleanText(title));

	}
	
	/**
	 * 
	 * @param row
	 */
	private void processHeading1(Row row) {
		//KFUSMain.logger.info("Degug: Headin1 Row " + row.getText() );
		
		this.kFUSHeading1 = new KFUSHeading1();
		String rowText = row.getText();
		
		if (KFUSUtil.isStr1ContainsStr2(rowText,"Companies")){
			setHeading1(KFUSAppConstant.XML_HEADING1_A_COMPANIES, KFUSAppConstant.XML_HEADING1_A_COMPANIES + "_" + this.fileCdcCode, rowText.trim());
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(rowText,"Individuals")){
			setHeading1(KFUSAppConstant.XML_HEADING1_B_INDIVIDUALS, KFUSAppConstant.XML_HEADING1_B_INDIVIDUALS + "_" + this.fileCdcCode, rowText.trim());			
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(rowText,"Other Direct Taxes")){
			setHeading1(KFUSAppConstant.XML_HEADING1_C_OTHER_TAXES, KFUSAppConstant.XML_HEADING1_C_OTHER_TAXES + "_" + this.fileCdcCode, rowText.trim());
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(rowText,"Turnover Taxes")){
			setHeading1(KFUSAppConstant.XML_HEADING1_D_TURNOVER_TAXES, KFUSAppConstant.XML_HEADING1_D_TURNOVER_TAXES + "_" + this.fileCdcCode, rowText.trim());
		}
		
		kFUSWordData.addTableRowContent(kFUSHeading1);
	}

	/**
	 * 
	 * @param elementName
	 * @param taxpenId
	 * @param title
	 */
	private void setHeading1 (String elementName, String taxpenId, String title){
		this.kFUSHeading1.setElementName(elementName);
		this.kFUSHeading1.setId(taxpenId);
		this.kFUSHeading1.setTitle(KFUSUtil.cleanText(title));
	}
	
	/**
	 * 
	 * @param row
	 * @throws Exception
	 */
	private void processFirstRow(Row row) throws Exception {
		//KFUSMain.logger.info("Degug: Processing first Row " + row.getText() );
		ParagraphCollection paragraphs = row.getCells().get(0).getParagraphs();
		for (Node node : paragraphs) {
			String paraText = ((Paragraph)node).getText();
			if (paraText == null ) {continue;}
			paraText = KFUSUtil.cleanText(paraText);
			if (paraText.toLowerCase().contains("key") && paraText.toLowerCase().contains("-")) {
				String[] splitText = paraText.split("-");
				if (StringUtils.isEmpty(splitText[0])) {
					return;	
				}
				String countryDivName = splitText[0].trim();
				
				//read countries.xml online for CDCCode this will be used in many places
				this.fileCdcCode = CountriesXmlUtil.getInstance().getCDCCode(countryDivName);
				this.fileCdcCode = ((this.fileCdcCode == null) ? "" : this.fileCdcCode );
				
				kFUSWordData.setCountryDivName(countryDivName);
				break;
			}
		}
	}

	/**
	 * 
	 * KFCAHeading2 and KFCANormal are almost same in the word file.
	 * Only 1 difference is found but that is very much weak.
	 * 
	 * Both KFCAHeading2 and KFCANormal row has same color and same number of columns (two columns).
	 * In KFCAHeading2 the second column is empty.
	 * If in any how the second column of KFCANormal row becomes empty then 
	 * there will be no difference between KFCAHeading2 and KFCANormal.
	 * 
	 *  
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isNormal (Row row) throws Exception {
		boolean isNormal = false;
		//1. background color 255 255 255 or 182 221 232
		//2. foreground color 255 255 0   or 0    0   0
		//3. two columns
		//4. second column MUST BE FILLED WITH TEXT.
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
		
		//Special case for some rows.
		if ( shading.getBackgroundPatternColor().equals(new Color(182,221,232))) {
			return true;
		}
		
		boolean isBackgroundColorRight = shading.getBackgroundPatternColor().equals(new Color(255, 255, 255));
		boolean isForgroundColorRight = shading.getForegroundPatternColor().equals(new Color(255, 255, 0));
		
		boolean hasTwoColumns = false;
		boolean isSecondColumnNotEmpty = false;
		
		if (isBackgroundColorRight && isForgroundColorRight) {
			hasTwoColumns = (row.getCells().getCount() == 2);
			String secondColumnText = null;
			if (hasTwoColumns) {
				secondColumnText = row.getCells().get(1).getText();
				if (secondColumnText != null) {
					secondColumnText = KFUSUtil.cleanText(secondColumnText).trim();
				}
				isSecondColumnNotEmpty = hasTwoColumns && StringUtils.isNotEmpty(secondColumnText);
			}
		}
		isNormal = isBackgroundColorRight && isForgroundColorRight && hasTwoColumns && isSecondColumnNotEmpty;
		return isNormal;
	}
	
	/**
	 * 
	 * KFCAHeading2 and KFCANormal are almost same in the word file.
	 * Only 1 difference is found but that is very much weak.
	 * 
	 * Both KFCAHeading2 and KFCANormal row has same color and same number of columns (two columns).
	 * In KFCAHeading2 the second column is empty.
	 * If in any how the second column of KFCANormal row becomes empty then 
	 * there will be no difference between KFCAHeading2 and KFCANormal.
	 * 
	 *  
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isHeading2 (Row row) throws Exception {
		//1. background color 255 255 255
		//2. foreground color 255 255 0
		//3. two columns
		//4. second column must be empty
		
		boolean isHeading2 = false;
		
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
		boolean isBackgroundColorRight = shading.getBackgroundPatternColor().equals(new Color(255, 255, 255));
		boolean isForgroundColorRight = shading.getForegroundPatternColor().equals(new Color(255, 255, 0));
		
		boolean hasTwoColumns = false;
		boolean isSecondColumnEmpty = false;
		
		if (isBackgroundColorRight && isForgroundColorRight) {
			hasTwoColumns = row.getCells().getCount() == 2;
			String secondColumnText = null;
			if (hasTwoColumns) {
				secondColumnText = row.getCells().get(1).getText();
				if (secondColumnText != null) {
					secondColumnText = KFUSUtil.cleanText(secondColumnText).trim();
				}
				isSecondColumnEmpty = hasTwoColumns && StringUtils.isEmpty(secondColumnText);
			}
		}
		isHeading2 = isBackgroundColorRight && isForgroundColorRight && hasTwoColumns && isSecondColumnEmpty;
		return isHeading2;
	}
	
	/**
	 * ForegroundColor must be 128 0 0 and columns count must be 1
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isFirstRow (Row row) throws Exception{
		boolean isFirstRow = false;
		//System.out.println("isFirstRow: Row Text: " + row.getText());
		Color color = new Color (128,0,0);
		if (row.getCells().getCount() == 1 && row.getCells().get(0).getCellFormat().getShading().getForegroundPatternColor().equals(color))
			isFirstRow = true;
		return isFirstRow;
	}
	
	/**
	 * BackgroundColor 192 192 192
	 * ForegroundColor 255 255 0
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isHeading1 (Row row) throws Exception {
		boolean isHeading1 = false;
		Color foregroundColor = new Color (255, 255, 0);
		Color backgroundColor = new Color (192, 192, 192);
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
		
		if (shading.getForegroundPatternColor().equals(foregroundColor) && shading.getBackgroundPatternColor().equals(backgroundColor)){
			isHeading1 = true;
		}
		return isHeading1;
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private String[] getElementNameAndIdAndExtxrefTargetForNormalRow(String name) {
		String retValue[] = null;
		name = name.trim();
		
		if (KFUSUtil.isStr1ContainsStr2(name, "1.  Corporate income tax rates")) {
			retValue = new String[3];
			retValue[0] = "kfus_corp_tax_rates";
			retValue[1] = "kfus_corp_tax_rates_" + this.fileCdcCode;
			retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_1.5.1.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "3. Business income apportionment formula")) {
			retValue = new String[3];
			retValue[0] = "kfus_bus_income_app_form";
			retValue[1] = "kfus_bus_income_app_form_" + this.fileCdcCode;
			retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_1.4.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "2. Tax base")) {
			if (this.kFUSHeading1.getElementName().equals(KFUSAppConstant.XML_HEADING1_A_COMPANIES)) {
				retValue = new String[3];
				retValue[0] = "kfus_tax_base_companies";
				retValue[1] = "kfus_tax_base_companies_" + this.fileCdcCode;
				retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_1.3.1.";
			}else if (this.kFUSHeading1.getElementName().equals(KFUSAppConstant.XML_HEADING1_B_INDIVIDUALS)) {
				retValue = new String[3];
				retValue[0] = "kfus_tax_base_ind";
				retValue[1] = "kfus_tax_base_ind_" + this.fileCdcCode;
				retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_1.2.1.";
			}
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "4. Capital gains")) {
			if (this.kFUSHeading1.getElementName().equals(KFUSAppConstant.XML_HEADING1_A_COMPANIES)) {
				retValue = new String[3];
				retValue[0] = "kfus_capital_gains";
				retValue[1] = "kfus_capital_gains_" + this.fileCdcCode;
				retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_3.2.";
			}else if (this.kFUSHeading1.getElementName().equals(KFUSAppConstant.XML_HEADING1_B_INDIVIDUALS)) {
				retValue = new String[3];
				retValue[0] = "kfus_capital_gains_ind";
				retValue[1] = "kfus_capital_gains_ind_" + this.fileCdcCode;
				retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_1.4.2.";
			}
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "5. Alternative minimum tax")) {
			if (this.kFUSHeading1.getElementName().equals(KFUSAppConstant.XML_HEADING1_A_COMPANIES)) {
				retValue = new String[3];
				retValue[0] = "kfus_amt";
				retValue[1] = "kfus_amt_" + this.fileCdcCode;
				retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_1.5.2.";
			}else if (this.kFUSHeading1.getElementName().equals(KFUSAppConstant.XML_HEADING1_B_INDIVIDUALS)) {
				retValue = new String[3];
				retValue[0] = "kfus_amt_ind";
				retValue[1] = "kfus_amt_ind_" + this.fileCdcCode;
				retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_1.4.3.";
			}
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "6. Group taxation")) {
			retValue = new String[3];
			retValue[0] = "kfus_group";
			retValue[1] = "kfus_group_" + this.fileCdcCode;
			retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_2.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "7. Foreign tax deduction")) {
			retValue = new String[3];
			retValue[0] = "kfus_foreign_tax";
			retValue[1] = "kfus_foreign_tax_" + this.fileCdcCode;
			retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_5.4.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "8. Tax return due date")) {
			retValue = new String[3];
			retValue[0] = "kfus_tax_return_due_date";
			retValue[1] = "kfus_tax_return_due_date_" + this.fileCdcCode;
			retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_1.6.2.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "1. Personal income tax rates")) {
			retValue = new String[3];
			retValue[0] = "kfus_ind_income_tax_rates";
			retValue[1] = "kfus_ind_income_tax_rates_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_1.4.";
		}
		 
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "Non-residents")) {
			retValue = new String[3];
			retValue[0] = "kfus_income_subject_ind_nonres";
			retValue[1] = "kfus_income_subject_ind_nonres_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_4.2.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "Residents")) {
			retValue = new String[3];
			retValue[0] = "kfus_income_subject_ind_res";
			retValue[1] = "kfus_income_subject_ind_res_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_4.1.";
		}

		
		else if (KFUSUtil.isStr1ContainsStr2(name, "6. Tax return due date")) {
			retValue = new String[3];
			retValue[0] = "kfus_tax_return_due_date_ind";
			retValue[1] = "kfus_tax_return_due_date_ind_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_1.5.2.";
		}

		else if (KFUSUtil.isStr1ContainsStr2(name, "1. Net wealth tax")) {
			retValue = new String[3];
			retValue[0] = "kfus_net_wealth_tax";
			retValue[1] = "kfus_net_wealth_tax_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_3.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "2. Estate tax")) {
			retValue = new String[3];
			retValue[0] = "estate_tax";
			retValue[1] = "estate_tax_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_2.1.";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "3.  Inheritance tax")) {
			retValue = new String[3];
			retValue[0] = "inheritance_tax";
			retValue[1] = "inheritance_tax_" + this.fileCdcCode;
			retValue[2] = "";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "4.  Generation skipping tax")) {
			retValue = new String[3];
			retValue[0] = "gen_skip_tax";
			retValue[1] = "gen_skip_tax_" + this.fileCdcCode;
			retValue[2] = "";
		}
		
		else if (KFUSUtil.isStr1ContainsStr2(name, "5.  Gift tax ")) {
			retValue = new String[3];
			retValue[0] = "gift_tax";
			retValue[1] = "gift_tax_" + this.fileCdcCode;
			retValue[2] = "nathsubb_us_" + this.fileCdcCode + "_s_2.2.";
		}
			
		else if (KFUSUtil.isStr1ContainsStr2(name, "1.  Sales and use taxes")) {
			retValue = new String[3];
			retValue[0] = "kfus_sales";
			retValue[1] = "kfus_sales_" + this.fileCdcCode;
			retValue[2] = "nathsuba_us_" + this.fileCdcCode + "_s_6.";
		}
		
		return retValue;
	}
	

}
