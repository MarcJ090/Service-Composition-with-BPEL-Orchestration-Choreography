package tp.bpel.core;

import java.util.UUID;

public final class TransportLogic {
    public Dtos.ActivateResult activatePass(Dtos.ActivateRequest req) {
        String pass = "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Dtos.ActivateResult(req.getStudentId(), pass);
    }
}
