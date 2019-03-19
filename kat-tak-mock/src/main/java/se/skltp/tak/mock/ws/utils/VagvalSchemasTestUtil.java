package se.skltp.tak.mock.ws.utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoIdType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.FilterInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VirtualiseringsInfoIdType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VirtualiseringsInfoType;

public class VagvalSchemasTestUtil {

	public static final Duration IN_TEN_YEARS;
	public static final Duration AN_HOUR_AGO;
	public static final Duration TWO_HOURS_AGO;
	public static final Duration IN_ONE_HOUR;

	static {
		Duration tenYearsDuration = null;
		Duration anHourAgo = null;
		Duration twoHoursAgo = null;
		Duration inOneHour = null;
		try {
			tenYearsDuration = DatatypeFactory.newInstance().newDurationYearMonth(true, 10, 0);
			anHourAgo = DatatypeFactory.newInstance().newDuration(false, 0, 0, 0, 1, 0, 0);
			twoHoursAgo = DatatypeFactory.newInstance().newDuration(false, 0, 0, 0, 2, 0, 0);
			inOneHour = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, 1, 0, 0);
		} catch (DatatypeConfigurationException e) {
		}
		IN_TEN_YEARS = tenYearsDuration;
		AN_HOUR_AGO = anHourAgo;
		TWO_HOURS_AGO = twoHoursAgo;
		IN_ONE_HOUR = inOneHour;

		getRelativeDate(tenYearsDuration);

	}

	public static XMLGregorianCalendar getRelativeDate(Duration relativeDuration) {
		XMLGregorianCalendar relativeDate = XmlGregorianCalendarUtil.getNowAsXMLGregorianCalendar();
		relativeDate.add(relativeDuration);
		return relativeDate;
	}

	public static VirtualiseringsInfoType createRouting(String adress, String rivVersion, String namnrymnd, String receiver) {
		return createRouting(adress, rivVersion, namnrymnd, receiver, getRelativeDate(AN_HOUR_AGO), getRelativeDate(IN_TEN_YEARS));
	}

	public static VirtualiseringsInfoType createRouting(String adress, String rivVersion, String namnrymnd, String receiver, XMLGregorianCalendar fromTidpunkt, XMLGregorianCalendar tomTidpunkt) {

		VirtualiseringsInfoType vi = new VirtualiseringsInfoType();
		vi.setAdress(adress);
		vi.setFromTidpunkt(fromTidpunkt);
		vi.setTomTidpunkt(tomTidpunkt);
		vi.setReceiverId(receiver);
		vi.setRivProfil(rivVersion);
		VirtualiseringsInfoIdType viId = new VirtualiseringsInfoIdType();
		viId.setValue(String.valueOf(1));
		vi.setVirtualiseringsInfoId(viId);
		vi.setTjansteKontrakt(namnrymnd);
		return vi;
	}

	public static AnropsBehorighetsInfoType createAuthorization(String sender, String namnrymd, String receiver) {
		return createAuthorization(sender, namnrymd, receiver, getRelativeDate(AN_HOUR_AGO), getRelativeDate(IN_TEN_YEARS));
	}

	public static AnropsBehorighetsInfoType createAuthorization(String sender, String namnrymd, String receiver, XMLGregorianCalendar fromTidpunkt, XMLGregorianCalendar tomTidpunkt) {

		AnropsBehorighetsInfoIdType aboId = new AnropsBehorighetsInfoIdType();
		aboId.setValue(String.valueOf(1));
		AnropsBehorighetsInfoType abo = new AnropsBehorighetsInfoType();
		abo.setAnropsBehorighetsInfoId(aboId);
		abo.setFromTidpunkt(fromTidpunkt);
		abo.setTomTidpunkt(tomTidpunkt);
		abo.setReceiverId(receiver);
		abo.setSenderId(sender);
		abo.setTjansteKontrakt(namnrymd);
		return abo;
	}


	public static FilterInfoType createFilterInfo(String serviceDomain, String categorizationsCommaSeparated){
		AnropsBehorighetsInfoType abo = new AnropsBehorighetsInfoType();
		FilterInfoType filterInfoType = new FilterInfoType();
		filterInfoType.setServiceDomain(serviceDomain);
		String[] categorizations = categorizationsCommaSeparated.split(",");
		for(String categorization: categorizations) {
			filterInfoType.getCategorization().add(categorization);
		}

		return filterInfoType;
	}

}
