package tp.bpel.choreography.transport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tp.bpel.core.Dtos;
import tp.bpel.core.Event;
import tp.bpel.core.TransportLogic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableJms
public class TransportEventAdapter {
    public static void main(String[] args) {
        SpringApplication.run(TransportEventAdapter.class, args);
    }

    @Component
    public static class Listener {
        private final JmsTemplate jms;
        private final TransportLogic transport = new TransportLogic();

        public Listener(JmsTemplate jms) { this.jms = jms; }

        @JmsListener(destination = "PaymentDecided", selector = "approved = TRUE")
        public void onPaymentApproved(String payload) throws Exception {
            Event in = Event.parse(payload);
            Dtos.ActivateResult r = transport.activatePass(new Dtos.ActivateRequest(in.str("studentId")));

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("studentId", r.getStudentId());
            data.put("passId", r.getPassId());

            jms.convertAndSend("EnrollmentCompleted",
                    Event.of("PassAssigned", in.getCorrelationId(), "transport-service", data).toJson());
        }
    }

    @Component
    public static class CompletionAggregator {
        private final JmsTemplate jms;
        private final Map<String, Set<String>> recus = new ConcurrentHashMap<>();
        private final Map<String, Map<String, Object>> donnees = new ConcurrentHashMap<>();

        public CompletionAggregator(JmsTemplate jms) { this.jms = jms; }

        @JmsListener(destination = "EnrollmentCompleted")
        public void onCompletionEvent(String payload) throws Exception {
            Event in = Event.parse(payload);
            if (!"RoomAssigned".equals(in.getType()) && !"PassAssigned".equals(in.getType())) {
                return;
            }
            String cid = in.getCorrelationId();
            Set<String> types = recus.computeIfAbsent(cid, k -> ConcurrentHashMap.newKeySet());
            types.add(in.getType());
            donnees.computeIfAbsent(cid, k -> new ConcurrentHashMap<>()).putAll(in.getData());

            if (types.contains("RoomAssigned") && types.contains("PassAssigned")) {
                Map<String, Object> data = new LinkedHashMap<>(donnees.get(cid));
                data.put("status", "ENROLLED");
                jms.convertAndSend("EnrollmentCompleted",
                        Event.of("EnrollmentCompleted", cid, "completion-aggregator", data).toJson());
                recus.remove(cid);
                donnees.remove(cid);
            }
        }
    }
}
