package tp.bpel.core;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BusinessLogicTest {

    @Test
    public void bankApprovesAmountAtThreshold() {
        Dtos.PayResult r = new BankLogic().pay(
                new Dtos.PayRequest("STU-1", new BigDecimal("500000")));
        assertTrue(r.isApproved());
        assertTrue(r.getReceipt().startsWith("RCT-"));
    }

    @Test
    public void bankRefusesAmountAboveThreshold() {
        Dtos.PayResult r = new BankLogic().pay(
                new Dtos.PayRequest("STU-2", new BigDecimal("900000")));
        assertFalse(r.isApproved());
        assertEquals("N/A", r.getReceipt());
    }

    @Test
    public void universityCompensationMarksStudentCancelled() {
        UniversityLogic uni = new UniversityLogic();
        uni.register(new Dtos.RegisterRequest("STU-3", "MSc-ISI"));
        assertEquals("ENROLLED", uni.status("STU-3"));
        uni.cancel("STU-3");
        assertEquals("CANCELLED", uni.status("STU-3"));
    }

    @Test
    public void residenceAndTransportReturnIds() {
        assertTrue(new ResidenceLogic().assignRoom(new Dtos.AssignRequest("STU-4"))
                .getRoomNumber().startsWith("R-"));
        assertTrue(new TransportLogic().activatePass(new Dtos.ActivateRequest("STU-4"))
                .getPassId().startsWith("T-"));
    }

    @Test
    public void eventEnvelopeRoundTripsThroughJson() throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("studentId", "STU-5");
        data.put("approved", true);
        Event out = Event.of("PaymentDecided", "corr-1", "bank-service", data);

        Event in = Event.parse(out.toJson());
        assertEquals("PaymentDecided", in.getType());
        assertEquals("corr-1", in.getCorrelationId());
        assertEquals("bank-service", in.getSource());
        assertEquals("STU-5", in.str("studentId"));
        assertTrue(in.bool("approved"));
        assertNull(in.decimal("missing"));
    }
}
