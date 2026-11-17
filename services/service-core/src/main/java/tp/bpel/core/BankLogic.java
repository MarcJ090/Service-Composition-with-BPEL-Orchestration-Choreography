package tp.bpel.core;

import java.math.BigDecimal;
import java.util.UUID;

public final class BankLogic {
    private static final BigDecimal LIMITE = new BigDecimal("500000");

    public Dtos.PayResult pay(Dtos.PayRequest req) {
        boolean ok = req.getAmount() != null && req.getAmount().compareTo(LIMITE) <= 0;
        return new Dtos.PayResult(ok, ok ? "RCT-" + UUID.randomUUID() : "N/A");
    }
}
