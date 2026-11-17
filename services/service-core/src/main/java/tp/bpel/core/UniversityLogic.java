package tp.bpel.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UniversityLogic {
    private static final Map<String, String> ETUDIANTS = new ConcurrentHashMap<>();

    public Dtos.RegisterResult register(Dtos.RegisterRequest req) {
        ETUDIANTS.put(req.getStudentId(), "ENROLLED");
        return new Dtos.RegisterResult(req.getStudentId());
    }

    public void cancel(String studentId) {
        ETUDIANTS.put(studentId, "CANCELLED");
    }

    public String status(String studentId) {
        return ETUDIANTS.getOrDefault(studentId, "UNKNOWN");
    }

    public Map<String, String> all() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(ETUDIANTS));
    }
}
