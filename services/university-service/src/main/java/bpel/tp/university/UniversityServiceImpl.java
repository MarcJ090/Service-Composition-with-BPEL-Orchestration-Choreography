package bpel.tp.university;

import javax.jws.WebService;
import tp.bpel.core.Dtos;
import tp.bpel.core.UniversityLogic;

@WebService(
        serviceName = "UniversityService",
        portName = "UniversityPort",
        endpointInterface = "bpel.tp.university.UniversityPT",
        targetNamespace = "http://tp.bpel/university/",
        wsdlLocation = "WEB-INF/classes/university.wsdl")
public class UniversityServiceImpl implements UniversityPT {

    private final UniversityLogic logic = new UniversityLogic();

    @Override
    public RegisterResponse register(RegisterRequest req) {
        Dtos.RegisterResult r = logic.register(new Dtos.RegisterRequest(req.getStudentId(), req.getProgram()));
        RegisterResponse res = new RegisterResponse();
        res.setStudentId(r.getStudentId());
        return res;
    }

    @Override
    public CancelResponse cancel(CancelRequest req) {
        logic.cancel(req.getStudentId());
        CancelResponse res = new CancelResponse();
        res.setStatus(logic.status(req.getStudentId()));
        return res;
    }
}
