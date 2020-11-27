package org.ibfd.word2xml.common;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author asfak.mahamud
 *
 */

public class CountriesXmlUtil {
    
	/**
	 * Instance of <code>CountriesXmlUtil</code> itself
	 */
	private static CountriesXmlUtil cXU = null;
	
	/**
	 * Document object for countries.xml
	 */
	private Document doc = null;
	
	/**
	 * Path of Countries.xml in the online.
	 */
	private static final String COUNTRIES_XML_PATH = "http://dtd.ibfd.org/dtd/config/countries.xml";
	
	/**
	 * private constructor
	 */
	private CountriesXmlUtil() {
		doc = DocUtils.readDocument(COUNTRIES_XML_PATH);
	}
	
	/**
	 * 
	 * @return CountriesXmlUtil
	 */
	public static CountriesXmlUtil getInstance() {
		if (cXU == null) {
			cXU = new CountriesXmlUtil();
		}
		return cXU;
	}
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value cdc "ab".
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
     *
	 * </pre>
	 *  
	 * @param countryDivName
	 * @return
	 */
	public String getCDCCode (String countryDivName) {
		Element cdc = (Element)this.doc.selectSingleNode("//country/countrydiv[countrydivname='" + countryDivName +"']/cdc");
		if (cdc == null) {
			return (null);
		}
		return cdc.getStringValue().trim();
	}
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value shortname "Canada".
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
     *
	 * </pre>
	 * 
	 * @param countryDivName
	 * @return
	 */
	public String getCountry (String countryDivName) {
		Element country = (Element)this.doc.selectSingleNode("//country[countrydiv/countrydivname='" + countryDivName +"']/shortname");
		if (country == null) {
			return (null);
		}
		return country.getStringValue().trim();
	}
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value code "ca".
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
     *
	 * </pre>
	 * 
	 * @param countryDivName
	 * @return
	 */
	public String getCountryCode (String countryDivName) {
		Element countryCode = (Element)this.doc.selectSingleNode("//country[countrydiv/countrydivname='" + countryDivName +"']/code");
		if (countryCode == null) {
			return (null);
		}
		return countryCode.getStringValue().trim();
	}
	
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value type "province".
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
     *
	 * </pre>
	 * 
	 * @param countryDivName
	 * @return
	 */
	public String getCountryDivType (String countryDivName) {
		Element countryDivType = (Element)this.doc.selectSingleNode("//country/countrydiv[countrydivname='" + countryDivName +"']/type");
		if (countryDivType == null) {
			return (null);
		}
		return countryDivType.getStringValue().trim();
	}
	
	
	
	
	
//	public static void main (String [] args) {
//		System.out.println(CountriesXmlUtil.getInstance().getCDCCode("Alberta"));
//		System.out.println(CountriesXmlUtil.getInstance().getCountry("Alberta"));
//	}
}
