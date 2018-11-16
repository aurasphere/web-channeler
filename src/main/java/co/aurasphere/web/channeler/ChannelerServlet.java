package co.aurasphere.web.channeler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Servlet which acts as a portal by showing a requested web page. The web page
 * to be showed must be passed as the {@value #REQUESTED_URL_PARAMETER_NAME}
 * using the GET verb.
 * 
 * The servlet will reply by showing a modified version of the page with all the
 * links rewritten to point to this same servlet.
 * 
 * @author Donato Rimenti
 */
@WebServlet("/*")
public class ChannelerServlet extends HttpServlet {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter name of the requested URL.
	 */
	private static final String REQUESTED_URL_PARAMETER_NAME = "url";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Gets the requested page.
		String url = req.getParameter(REQUESTED_URL_PARAMETER_NAME);
		Response response = Jsoup.connect(url).execute();
		Document body = response.parse();

		// Rewrites the links in the page by adding a reference to this server.
		String rewritePrefix = req.getRequestURL().append("?").append(REQUESTED_URL_PARAMETER_NAME).append("=")
				.toString();
		rewriteAttributeUrls(body, "href", rewritePrefix);
		rewriteAttributeUrls(body, "action", rewritePrefix);

		// Writes the response back.
		resp.getWriter().append(body.toString());
	}

	/**
	 * Rewrites the urls for all the HTML elements which have a given attribute
	 * to point to this same servlet.
	 *
	 * @param body
	 *            the document body
	 * @param attribute
	 *            the attribute of the elements to change
	 * @param rewritePrefix
	 *            the prefix to prepend to the links
	 */
	private void rewriteAttributeUrls(Document body, String attribute, String rewritePrefix) {
		Elements elementsToRewrite = body.getElementsByAttribute(attribute);
		elementsToRewrite.forEach(
				e -> e.attr(attribute, rewritePrefix + e.attr(attribute).replaceAll("\n", "").replace(" ", "")));
	}

}