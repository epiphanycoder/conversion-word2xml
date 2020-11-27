package org.ibfd.word2xml.common;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class ValidationErrorHandler implements ErrorHandler {
	
	/**
	 * 
	 */
    public void warning(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    /**
     * 
     */
    public void error(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    /**
     * 
     */
    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }
}
