package org.ibfd.word2xml.kfus;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Node;
import org.dom4j.tree.DefaultText;
import org.ibfd.word2xml.common.MyElement;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFUSUtil {

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static ArrayList<Node> str2nodes(String str) {
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		ArrayList<String> hyperlinks = new ArrayList<String>();
		hyperlinks.add(Character.toString((char) 19) + "HYPERLINK");
		hyperlinks.add(Character.toString((char) 19) + " HYPERLINK");
		hyperlinks.add("HYPERLINK");

		String leftBracket = Character.toString((char) 20);
		String rightBracket = Character.toString((char) 21);
		
		// See &#19; HYPERLINK
		// "http://online2.ibfd.org/data/tns/docs/html/tns_2006-07-25_e2_1.html"
		// \t "_blank"
		// &#20;TNS:2006-07-25:E2-1&#21;; &#19; HYPERLINK
		while (contains(hyperlinks, str)) {
			String hyperlink = getHyperlink(hyperlinks, str);

			String text = strBefore(str, hyperlink);
			nodes.add(new DefaultText(cleanText(text)));

			str = strAfter(str, hyperlink);
			str = strAfter(str, "\"");

			String link = strBefore(str, "\"");
			String footRefText = "";
			
			//issue: 2027 asf added starts
			boolean strContainsFootNote = str.contains("Footnote");		
			if (strContainsFootNote){
				String temp = strAfter(str, "Footnote");
				temp = strBefore(temp, "\"");
				link = link + "#" + "Footnote" + temp;
				nodes.add(new MyElement("url", cleanText(temp), "target", link));
			}
			boolean strContainsFootref = str.contains("Footref");		
			if (strContainsFootref){
				String temp = strAfter(str, "Footref");
				footRefText = getFootrefText(temp); 
				temp = strBefore(temp, "\"");				
				link = link + "#" + "Footref" + temp;
				nodes.add(new MyElement("url", cleanText(temp), "target", link));
				
			}
				
			str = strAfter(str, leftBracket);
			
			if (str.contains(rightBracket)) {
				text = strBefore(str, rightBracket);
				str = strAfter(str, rightBracket);
			}else if(str.contains("Footref")){
				str = footRefText;
			}
			else if(str.contains(")")){
				text = strBefore(str, ")");
				str = strAfter(str, text);
			}else {
				text = str;
				str = "";
			}

			
			if (strContainsFootNote == false && strContainsFootref == false)
				nodes.add(new MyElement("url", cleanText(text), "target", link));
		}
		nodes.add(new DefaultText(cleanText(str)));
		
		return nodes;
	}

	/**
	 * 
	 * @param temp
	 * @return
	 */
	private static String getFootrefText(String temp) {
//		if(temp.contains("ECR I-249. ")){
//			for(int i=0; i<temp.length(); i++){
//				System.out.println(""+Character.toString(temp.charAt(i)) + " " +Integer.toString((int)temp.charAt(i)));
//			}
//		}
		if(temp.contains(Character.toString((char)8211))){
			int index = temp.indexOf(Character.toString((char)8211));
			return temp.substring(index);
		}
		return "";
	}

	/**
	 * 
	 * @param hyperlinks
	 * @param str
	 * @return
	 */
	public static boolean contains(List<String> hyperlinks, String str) {
		return getHyperlink(hyperlinks, str) != null;
	}

	/**
	 * 
	 * @param hyperlinks
	 * @param str
	 * @return
	 */
	public static String getHyperlink(List<String> hyperlinks, String str) {
		int minimumAppearPosition = Integer.MAX_VALUE;
		int appearPosition = 0;
		int retHyperIndex = -1;
		for (String h : hyperlinks) {
			if (str.contains(h)) {
				appearPosition = str.indexOf(h);
				if (minimumAppearPosition > appearPosition){
					minimumAppearPosition = appearPosition;
					retHyperIndex = hyperlinks.indexOf(h);
				}
			}
		}
		if (retHyperIndex > -1 ) {
			return hyperlinks.get(retHyperIndex);
		}
		return null;
	}

	/**
	 * 
	 * @param str
	 * @param upto
	 * @return
	 */
	public static String strBefore(String str, String upto) {
		return str.substring(0, str.indexOf(upto));
	}

	/**
	 * 
	 * @param str
	 * @param upto
	 * @return
	 */
	public static String strAfter(String str, String upto) {
		return str.substring(str.indexOf(upto) + upto.length());
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String cleanText(String text) {
		
		text = text.replace(Character.toString((char) 8211), "-");
		text = text.replace(Character.toString((char) 7), "");
		text = text.replace(Character.toString((char) 11), "");
		text = text.replace(Character.toString((char) 12), "");
		text = text.replace(Character.toString((char) 13), "");
		text = text.replace(Character.toString((char) 30), "");
		return text;
	}
	
	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isStr1EqualsStr2(String str1, String str2){
		if (StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)){
			return false;
		}
		str1 = str1.trim().toLowerCase().replaceAll("[\\s]+", "");
		str2 = str2.trim().toLowerCase().replaceAll("[\\s]+", "");
		return str1.equals(str2);
	}
	
	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isStr1ContainsStr2(String str1, String str2){
		if (StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)){
			return false;
		}
		str1 = cleanText(str1);
		str2 = cleanText(str2);
		
		str1 = str1.trim().toLowerCase().replaceAll("[\\s]+", "");
		str2 = str2.trim().toLowerCase().replaceAll("[\\s]+", "");
		return str1.contains(str2);
	}
	
}
