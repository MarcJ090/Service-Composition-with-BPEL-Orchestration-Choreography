package tp.bpel.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String eventId;
    private String correlationId;
    private String timestamp;
    private String source;
    private String type;
    private Map<String, Object> data = new LinkedHashMap<>();

    public static Event of(String type, String correlationId, String source, Map<String, Object> data) {
        Event e = new Event();
        e.eventId = UUID.randomUUID().toString();
        e.correlationId = correlationId;
        e.timestamp = Instant.now().toString();
        e.source = source;
        e.type = type;
        e.data = new LinkedHashMap<>(data);
        return e;
    }

    public static Event parse(String json) throws Exception {
        return MAPPER.readValue(json, Event.class);
    }

    public String toJson() throws Exception {
        return MAPPER.writeValueAsString(this);
    }

    public String str(String key) {
        Object v = data.get(key);
        return v == null ? null : String.valueOf(v);
    }

    public boolean bool(String key) {
        Object v = data.get(key);
        return v instanceof Boolean ? (Boolean) v : Boolean.parseBoolean(String.valueOf(v));
    }

    public BigDecimal decimal(String key) {
        Object v = data.get(key);
        return v == null ? null : new BigDecimal(String.valueOf(v));
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
