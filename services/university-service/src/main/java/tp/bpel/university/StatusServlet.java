package tp.bpel.university;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import tp.bpel.core.UniversityLogic;

@WebServlet(name = "StatusServlet", urlPatterns = "/status")
public class StatusServlet extends HttpServlet {

    private final UniversityLogic logic = new UniversityLogic();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>Université - registre</title></head><body>");
        out.println("<h1>Registre des étudiants</h1>");
        out.println("<table border='1' cellpadding='4'><tr><th>studentId</th><th>status</th></tr>");
        for (Map.Entry<String, String> e : logic.all().entrySet()) {
            out.println("<tr><td>" + e.getKey() + "</td><td>" + e.getValue() + "</td></tr>");
        }
        out.println("</table></body></html>");
    }
}
