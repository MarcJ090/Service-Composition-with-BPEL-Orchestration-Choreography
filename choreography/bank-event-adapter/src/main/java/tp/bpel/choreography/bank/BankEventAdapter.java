package tp.bpel.choreography.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tp.bpel.core.BankLogic;
import tp.bpel.core.Dtos;
import tp.bpel.core.Event;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@EnableJms
public class BankEventAdapter {
    public static void main(String[] args) {
        SpringApplication.run(BankEventAdapter.class, args);
    }

    @Component
    public static class Listener {
        private final JmsTemplate jms;
        private final BankLogic bank = new BankLogic();

        public Listener(JmsTemplate jms) { this.jms = jms; }

        @JmsListener(destination = "StudentRegistered")
        public void onStudentRegistered(String payload) throws Exception {
            Event in = Event.parse(payload);
            Dtos.PayResult r = bank.pay(new Dtos.PayRequest(in.str("studentId"), in.decimal("amount")));

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", in.str("studentId"));
            data.put("approved", r.isApproved());
            data.put("receipt", r.getReceipt());

            final boolean approved = r.isApproved();
            jms.convertAndSend("PaymentDecided",
                    Event.of("PaymentDecided", in.getCorrelationId(), "bank-service", data).toJson(),
                    m -> { m.setBooleanProperty("approved", approved); return m; });
        }
    }
}
