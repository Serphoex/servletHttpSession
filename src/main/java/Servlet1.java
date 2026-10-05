import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Servlet1")
public class Servlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Pattern SOLO_LETRAS = Pattern.compile("^\\p{L}{2,40}$");

	static final Map<String, Set<HttpSession>> SESIONES = new ConcurrentHashMap<>();

	public Servlet1() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		sinCache(response);
		response.setContentType("text/html; charset=UTF-8");

		String n = request.getParameter("userName");
		n = (n == null) ? "" : n.trim();

		PrintWriter out = response.getWriter();

		if (!SOLO_LETRAS.matcher(n).matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print(abrir("Error", "login"));
			out.print("<h3>Nombre inválido</h3>");
			out.print("<p>Solo se permiten letras (sin espacios, números ni símbolos), entre 2 y 40 caracteres.</p>");
			out.print("<a href='index.html'>Volver</a>");
			out.print(cerrar());
			out.close();
			return;
		}

		HttpSession anterior = request.getSession(false);
		if (anterior != null) {
			anterior.invalidate();
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("uname", n);
		SESIONES.computeIfAbsent(n, k -> ConcurrentHashMap.newKeySet()).add(session);

		out.print(abrir("Log in", "login"));
		out.print("<h3>Welcome " + escapar(n) + "</h3>");
		out.print("<p>Sesión iniciada. Ya puedes entrar a <a href='Servlet2'>Visit</a>.</p>");
		out.print(cerrar());
		out.close();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("index.html");
	}

	static void sinCache(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	static String escapar(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}

	static String abrir(String titulo, String activo) {
		return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>"
				+ "<meta name='viewport' content='width=device-width, initial-scale=1'>"
				+ "<title>" + titulo + "</title>"
				+ "<style>"
				+ "body{font-family:Arial,sans-serif;max-width:500px;margin:30px auto;padding:0 16px}"
				+ "nav{border-bottom:1px solid #ccc;padding-bottom:8px;margin-bottom:20px}"
				+ "nav a{color:#000;margin-right:6px}"
				+ "nav a.activo{font-weight:bold;text-decoration:none}"
				+ "</style></head><body>"
				+ "<nav>"
				+ enlace("index.html", "Log in", "login".equals(activo)) + " | "
				+ enlace("Servlet2", "Visit", "visit".equals(activo)) + " | "
				+ enlace("Servlet3", "Log out", "logout".equals(activo))
				+ "</nav>";
	}

	private static String enlace(String url, String texto, boolean activo) {
		return "<a href='" + url + "'" + (activo ? " class='activo'" : "") + ">" + texto + "</a>";
	}

	static String cerrar() {
		return "<script>"
				+ "window.addEventListener('pageshow',function(e){if(e.persisted){location.reload();}});"
				+ "</script></body></html>";
	}
}