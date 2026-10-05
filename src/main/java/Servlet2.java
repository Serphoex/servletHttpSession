import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Servlet2")
public class Servlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Servlet2() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Servlet1.sinCache(response);

		HttpSession session = request.getSession(false);
		String n = null;
		if (session != null) {
			try {
				n = (String) session.getAttribute("uname");
			} catch (IllegalStateException e) {
				
			}
		}

		if (n == null) {
			response.sendRedirect("index.html?msg=login");
			return;
		}

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.print(Servlet1.abrir("Visit", "visit"));
		out.print("<h3>Hello " + Servlet1.escapar(n) + "</h3>");
		out.print("<p>Esta sección solo se ve con la sesión iniciada.</p>");
		out.print(Servlet1.cerrar());
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}