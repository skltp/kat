package se.skltp.tak.mock.ws.utils;

import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.CATEGORIZATION_1;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.DOMAIN_1;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.AN_HOUR_AGO;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.IN_ONE_HOUR;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.IN_TEN_YEARS;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.TWO_HOURS_AGO;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.createAuthorization;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.createFilterInfo;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.createRouting;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.getRelativeDate;


import java.util.ArrayList;
import java.util.List;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VirtualiseringsInfoType;

public class VagvalSchemasTestListsUtil {

  // Utility class
  private VagvalSchemasTestListsUtil() {
  }

  public static HamtaAllaAnropsBehorigheterResponseType getStaticBehorighet() {
    HamtaAllaAnropsBehorigheterResponseType hamtaAllaAnropsBehorigheterResponseType = new HamtaAllaAnropsBehorigheterResponseType();
    hamtaAllaAnropsBehorigheterResponseType.getAnropsBehorighetsInfo().addAll(getStaticBehorighetList());
    return hamtaAllaAnropsBehorigheterResponseType;
  }

  public static HamtaAllaVirtualiseringarResponseType getStaticVagval() {
    HamtaAllaVirtualiseringarResponseType hamtaAllaVirtualiseringarResponseType = new HamtaAllaVirtualiseringarResponseType();
    hamtaAllaVirtualiseringarResponseType.getVirtualiseringsInfo().addAll(getStaticVagvalList());
    return hamtaAllaVirtualiseringarResponseType;
  }

  public static List<AnropsBehorighetsInfoType> getStaticBehorighetList() {
    List<AnropsBehorighetsInfoType> authorization = new ArrayList<>();
    AnropsBehorighetsInfoType authorizationInfo = createAuthorization(TestTakDataDefines.SENDER_1, TestTakDataDefines.NAMNRYMD_1,
        TestTakDataDefines.RECEIVER_1);
    authorizationInfo.getFilterInfo().add(createFilterInfo(DOMAIN_1, CATEGORIZATION_1));
    authorization.add(authorizationInfo);
    authorization
        .add(createAuthorization(TestTakDataDefines.SENDER_2, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1));
    authorization
        .add(createAuthorization(TestTakDataDefines.SENDER_1, TestTakDataDefines.NAMNRYMD_2, TestTakDataDefines.RECEIVER_1));
    authorization.add(
        createAuthorization(TestTakDataDefines.SENDER_3, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1,
            getRelativeDate(TWO_HOURS_AGO), getRelativeDate(AN_HOUR_AGO)));
    authorization.add(
        createAuthorization(TestTakDataDefines.SENDER_3, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1,
            getRelativeDate(IN_ONE_HOUR), getRelativeDate(IN_TEN_YEARS)));
    return authorization;
  }

  public static List<VirtualiseringsInfoType> getStaticVagvalList() {
    List<VirtualiseringsInfoType> routing = new ArrayList<>();
    routing.add(createRouting(TestTakDataDefines.ADDRESS_1, TestTakDataDefines.RIV21, TestTakDataDefines.NAMNRYMD_1,
        TestTakDataDefines.RECEIVER_1, getRelativeDate(TWO_HOURS_AGO), getRelativeDate(AN_HOUR_AGO)));
    routing.add(createRouting(TestTakDataDefines.ADDRESS_2, TestTakDataDefines.RIV21, TestTakDataDefines.NAMNRYMD_1,
        TestTakDataDefines.RECEIVER_1, getRelativeDate(IN_ONE_HOUR), getRelativeDate(IN_TEN_YEARS)));
    routing.add(createRouting(TestTakDataDefines.ADDRESS_1, TestTakDataDefines.RIV21, TestTakDataDefines.NAMNRYMD_1,
        TestTakDataDefines.RECEIVER_2));
    routing.add(createRouting(TestTakDataDefines.ADDRESS_1, TestTakDataDefines.RIV20, TestTakDataDefines.NAMNRYMD_2,
        TestTakDataDefines.RECEIVER_2));
    routing.add(createRouting(TestTakDataDefines.ADDRESS_2, TestTakDataDefines.RIV21, TestTakDataDefines.NAMNRYMD_2,
        TestTakDataDefines.RECEIVER_2));
    return routing;
  }


}
