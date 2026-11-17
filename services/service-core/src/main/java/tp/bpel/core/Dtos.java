package tp.bpel.core;

import java.math.BigDecimal;

public final class Dtos {
    private Dtos() { }

    public static class PayRequest {
        private final String studentId;
        private final BigDecimal amount;

        public PayRequest(String studentId, BigDecimal amount) {
            this.studentId = studentId;
            this.amount = amount;
        }

        public String getStudentId() { return studentId; }
        public BigDecimal getAmount() { return amount; }
    }

    public static class PayResult {
        private final boolean approved;
        private final String receipt;

        public PayResult(boolean approved, String receipt) {
            this.approved = approved;
            this.receipt = receipt;
        }

        public boolean isApproved() { return approved; }
        public String getReceipt() { return receipt; }
    }

    public static class RegisterRequest {
        private final String studentId;
        private final String program;

        public RegisterRequest(String studentId, String program) {
            this.studentId = studentId;
            this.program = program;
        }

        public String getStudentId() { return studentId; }
        public String getProgram() { return program; }
    }

    public static class RegisterResult {
        private final String studentId;

        public RegisterResult(String studentId) { this.studentId = studentId; }

        public String getStudentId() { return studentId; }
    }

    public static class AssignRequest {
        private final String studentId;

        public AssignRequest(String studentId) { this.studentId = studentId; }

        public String getStudentId() { return studentId; }
    }

    public static class AssignResult {
        private final String studentId;
        private final String roomNumber;

        public AssignResult(String studentId, String roomNumber) {
            this.studentId = studentId;
            this.roomNumber = roomNumber;
        }

        public String getStudentId() { return studentId; }
        public String getRoomNumber() { return roomNumber; }
    }

    public static class ActivateRequest {
        private final String studentId;

        public ActivateRequest(String studentId) { this.studentId = studentId; }

        public String getStudentId() { return studentId; }
    }

    public static class ActivateResult {
        private final String studentId;
        private final String passId;

        public ActivateResult(String studentId, String passId) {
            this.studentId = studentId;
            this.passId = passId;
        }

        public String getStudentId() { return studentId; }
        public String getPassId() { return passId; }
    }
}
