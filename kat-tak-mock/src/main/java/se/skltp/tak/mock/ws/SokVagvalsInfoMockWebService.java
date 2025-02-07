package se.skltp.tak.mock.ws;

import java.util.List;
import jakarta.xml.ws.Endpoint;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VirtualiseringsInfoType;
import se.skltp.tak.mock.ws.utils.VagvalSchemasTestListsUtil;

public class SokVagvalsInfoMockWebService {
    private Endpoint endpoint;
    private String url;
    SokVagvalsServiceSoap11LitDoc sokVagvalsInfo;

    public SokVagvalsInfoMockWebService(String url) {
        sokVagvalsInfo = new SokVagvalsServiceSoap11LitDoc();
        this.url = url;
        setVirtualiseringarResult(VagvalSchemasTestListsUtil.getStaticVagvalList());
        setAnropsBehorigheterResult(VagvalSchemasTestListsUtil.getStaticBehorighetList());
    }

    public Endpoint start(){
        endpoint = Endpoint.publish(url, sokVagvalsInfo);
        return endpoint;
    }

    public void stop(){
        if(endpoint!=null){
            endpoint.stop();
            endpoint=null;
        }
    }

    public void setVirtualiseringarResult(List<VirtualiseringsInfoType> virtualiseringar){
        sokVagvalsInfo.hamtaAllaVirtualiseringar(null).getVirtualiseringsInfo().clear();
        sokVagvalsInfo.hamtaAllaVirtualiseringar(null).getVirtualiseringsInfo().addAll(virtualiseringar);
   }

    public void setAnropsBehorigheterResult(List<AnropsBehorighetsInfoType> anropsBehorigheter) {
        sokVagvalsInfo.hamtaAllaAnropsBehorigheter(null).getAnropsBehorighetsInfo().clear();
        sokVagvalsInfo.hamtaAllaAnropsBehorigheter(null).getAnropsBehorighetsInfo().addAll(anropsBehorigheter);
    }

}
