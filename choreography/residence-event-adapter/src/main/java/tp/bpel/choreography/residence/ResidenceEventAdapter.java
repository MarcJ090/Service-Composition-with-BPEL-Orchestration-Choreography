package tp.bpel.choreography.residence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tp.bpel.core.Dtos;
import tp.bpel.core.Event;
import tp.bpel.core.ResidenceLogic;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@EnableJms
public class ResidenceEventAdapter {
    public static void main(String[] args) {
        SpringApplication.run(ResidenceEventAdapter.class, args);
    }

    @Component
    public static class Listener {
        private final JmsTemplate jms;
        private final ResidenceLogic residence = new ResidenceLogic();

        public Listener(JmsTemplate jms) { this.jms = jms; }

        @JmsListener(destination = "PaymentDecided", selector = "approved = TRUE")
        public void onPaymentApproved(String payload) throws Exception {
            Event in = Event.parse(payload);
            Dtos.AssignResult r = residence.assignRoom(new Dtos.AssignRequest(in.str("studentId")));

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", r.getStudentId());
            data.put("roomNumber", r.getRoomNumber());

            jms.convertAndSend("EnrollmentCompleted",
                    Event.of("RoomAssigned", in.getCorrelationId(), "residence-service", data).toJson());
        }
    }
}
