package tp.bpel.choreography.university;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tp.bpel.core.Dtos;
import tp.bpel.core.Event;
import tp.bpel.core.UniversityLogic;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@EnableJms
public class UniversityEventAdapter {
    public static void main(String[] args) {
        SpringApplication.run(UniversityEventAdapter.class, args);
    }

    @Component
    public static class Listener {
        private final JmsTemplate jms;
        private final UniversityLogic university = new UniversityLogic();

        public Listener(JmsTemplate jms) { this.jms = jms; }

        @JmsListener(destination = "EnrollmentRequested")
        public void onEnrollmentRequested(String payload) throws Exception {
            Event in = Event.parse(payload);
            Dtos.RegisterResult r = university.register(
                    new Dtos.RegisterRequest(in.str("studentId"), in.str("program")));

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", r.getStudentId());
            data.put("program", in.str("program"));
            data.put("amount", in.decimal("amount"));

            jms.convertAndSend("StudentRegistered",
                    Event.of("StudentRegistered", in.getCorrelationId(), "university-service", data).toJson());
        }

        @JmsListener(destination = "PaymentDecided", selector = "approved = FALSE")
        public void onPaymentRefused(String payload) throws Exception {
            Event in = Event.parse(payload);
            university.cancel(in.str("studentId"));

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", in.str("studentId"));
            data.put("status", "REFUSED");
            jms.convertAndSend("EnrollmentCompleted",
                    Event.of("EnrollmentCompleted", in.getCorrelationId(), "university-service", data).toJson());
        }
    }
}
