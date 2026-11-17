package bpel.tp.residence;

import javax.jws.WebService;
import tp.bpel.core.Dtos;
import tp.bpel.core.ResidenceLogic;

@WebService(
        serviceName = "ResidenceService",
        portName = "ResidencePort",
        endpointInterface = "bpel.tp.residence.ResidencePT",
        targetNamespace = "http://tp.bpel/residence/",
        wsdlLocation = "WEB-INF/classes/residence.wsdl")
public class ResidenceServiceImpl implements ResidencePT {

    private final ResidenceLogic logic = new ResidenceLogic();

    @Override
    public AssignRoomResponse assignRoom(AssignRoomRequest req) {
        Dtos.AssignResult r = logic.assignRoom(new Dtos.AssignRequest(req.getStudentId()));
        AssignRoomResponse res = new AssignRoomResponse();
        res.setStudentId(r.getStudentId());
        res.setRoomNumber(r.getRoomNumber());
        return res;
    }
}
