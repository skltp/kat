package se.skltp.tak.mock.ws.utils;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XmlGregorianCalendarUtil {
	private static DatatypeFactory datatypeFactory = getDatatypeFactory();

	private XmlGregorianCalendarUtil() {
    // Private Utility class
  }
	
	private static DatatypeFactory getDatatypeFactory() {
		try {
			return DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Could not create DatatypeFactory", e);
		}
	}

	public static final XMLGregorianCalendar getNowAsXMLGregorianCalendar() {
		GregorianCalendar now = (GregorianCalendar) GregorianCalendar.getInstance();
		return datatypeFactory.newXMLGregorianCalendar(now);
	}

	public static boolean isTimeWithinInterval(XMLGregorianCalendar time, XMLGregorianCalendar from, XMLGregorianCalendar to){
		return time.compare(from) != DatatypeConstants.LESSER && time.compare(to) != DatatypeConstants.GREATER;
	}

}
