import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


 
@WebServlet("/Servlet3")
public class Servlet3 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Servlet3() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Servlet1.sinCache(response);

		HttpSession session = request.getSession(false);
		if (session != null) {
			String n = null;
			try {
				n = (String) session.getAttribute("uname");
			} catch (IllegalStateException e) {
				
			}

			
			if (n != null) {
				Set<HttpSession> sesiones = Servlet1.SESIONES.remove(n);
				if (sesiones != null) {
					for (HttpSession s : sesiones) {
						try {
							s.invalidate();
						} catch (IllegalStateException e) {
							
						}
					}
				}
			}

			
			try {
				session.invalidate();
			} catch (IllegalStateException e) {
				
			}
		}

		response.sendRedirect("index.html?msg=bye");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}