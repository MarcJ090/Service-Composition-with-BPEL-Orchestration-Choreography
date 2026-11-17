package bpel.tp.bank;

import javax.jws.WebService;
import tp.bpel.core.BankLogic;
import tp.bpel.core.Dtos;

@WebService(
        serviceName = "BankService",
        portName = "BankPort",
        endpointInterface = "bpel.tp.bank.BankPT",
        targetNamespace = "http://tp.bpel/bank/",
        wsdlLocation = "WEB-INF/classes/bank.wsdl")
public class BankServiceImpl implements BankPT {

    private final BankLogic logic = new BankLogic();

    @Override
    public PayResponse pay(PayRequest req) {
        Dtos.PayResult r = logic.pay(new Dtos.PayRequest(req.getStudentId(), req.getAmount()));
        PayResponse res = new PayResponse();
        res.setApproved(r.isApproved());
        res.setReceipt(r.getReceipt());
        return res;
    }
}
