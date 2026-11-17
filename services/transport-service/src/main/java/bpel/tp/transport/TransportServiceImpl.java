package bpel.tp.transport;

import javax.jws.WebService;
import tp.bpel.core.Dtos;
import tp.bpel.core.TransportLogic;

@WebService(
        serviceName = "TransportService",
        portName = "TransportPort",
        endpointInterface = "bpel.tp.transport.TransportPT",
        targetNamespace = "http://tp.bpel/transport/",
        wsdlLocation = "WEB-INF/classes/transport.wsdl")
public class TransportServiceImpl implements TransportPT {

    private final TransportLogic logic = new TransportLogic();

    @Override
    public ActivatePassResponse activatePass(ActivatePassRequest req) {
        Dtos.ActivateResult r = logic.activatePass(new Dtos.ActivateRequest(req.getStudentId()));
        ActivatePassResponse res = new ActivatePassResponse();
        res.setStudentId(r.getStudentId());
        res.setPassId(r.getPassId());
        return res;
    }
}
