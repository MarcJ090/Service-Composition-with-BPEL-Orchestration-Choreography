package tp.bpel.core;

public final class ResidenceLogic {
    public Dtos.AssignResult assignRoom(Dtos.AssignRequest req) {
        int n = Math.abs(req.getStudentId().hashCode()) % 500 + 100;
        return new Dtos.AssignResult(req.getStudentId(), "R-" + n);
    }
}
