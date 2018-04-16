package net.bncf.uol2010.banco.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import mx.randalf.configuration.Configuration;
import mx.randalf.configuration.exception.ConfigurationException;
import mx.randalf.hibernate.exception.HibernateUtilException;
import net.bncf.uol2010.banco.chiamate.Chiamate;
import net.bncf.uol2010.database.schema.servizi.dao.ModuliAmministrazioneDAO;
import net.bncf.uol2010.database.schema.servizi.entity.ModuliAmministrazione;
import net.bncf.uol2010.utility.validate.standard.StdModuliAuthentication;

/**
 * Servlet implementation class for Servlet: Banco
 * 
 */
public class Banco extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	/**
	 * Questa variabile viene utilizzata per eseguire l'aggiornamento del Server
	 * Chiamate
	 */
	public static Chiamate chiamate = null;

	/**
	 * Questa variabile viene utilizzata per gestire il log dell'applicazione
	 */
	private Logger log = Logger.getLogger(Banco.class);

	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Banco() {
		super();
	}

	/**
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		super.destroy();

		try {
			if (((String) Configuration.getValueDefault("banco.chiamate", "false")).equalsIgnoreCase("true"))
				chiamate.end();
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		esegui(request, response);
	}

	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		esegui(request, response);
	}

	/**
	 * Questo metodo viene utilizzato per la gestione del risultato della
	 * richiesta sia di tipo Get che Post
	 * 
	 * @param request
	 *            Questa variable indica tutte le informazioni ricevute dal
	 *            client
	 * @param response
	 *            Questa variabile viene utilizzata per dare le risposte al
	 *            client
	 * @throws ServletException
	 *             Eccezione di tipo Servlet
	 * @throws IOException
	 *             Eccezione di tipo IO
	 */
	@SuppressWarnings("rawtypes")
	private void esegui(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StdModuliAuthentication modulo = null;
		Class myClass = null;
		String className = "";
		String msgErr = null;

		try {
			className = readModuli((request.getParameter("modulo") == null ? "Home" : request.getParameter("modulo")));

			myClass = Class.forName(className);
			modulo = (StdModuliAuthentication) myClass.newInstance();
			modulo.setMsgErr(msgErr);
			modulo.initBanco((String) Configuration.getValueDefault("banco.authentication.archive", "intranet2010"));
			modulo.setUrlAuthentication((String) Configuration.getValue("urlAuthentication"));
			modulo.esegui(request, response,
					(String) Configuration.getValueDefault("banco.pathXsl", "./Xsl"));
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		}
	}

	private String readModuli(String idModuliAmministrazione) {
		String myClass = null;
		ModuliAmministrazioneDAO moduliAmministrazioneDAO = null;
		ModuliAmministrazione moduliAmministrazione = null;

		try {
			moduliAmministrazioneDAO = new ModuliAmministrazioneDAO();
			moduliAmministrazione = moduliAmministrazioneDAO.findById(idModuliAmministrazione);
			if (moduliAmministrazione != null)
				myClass = moduliAmministrazione.getClasseModulo();
			else
				myClass = "net.bncf.uol2010.banco.servlet.moduli.Home";
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			log.error(e.getMessage(), e);
		}
		return myClass;
	}

	/**
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		String pathProperties = "";
		String[] fileConf = null;
//		String[] tipoUtente = null;
		try {
			super.init();

			if (this.getServletContext().getInitParameter("nomeCatalogo") != null
					&& this.getServletContext().getInitParameter("nomeCatalogo").startsWith("file://"))
				pathProperties = this.getServletContext().getInitParameter("nomeCatalogo").replace("file:///", "");
			else {
				pathProperties = System.getProperty("catalina.base") + File.separator;
				if (this.getServletContext().getInitParameter("nomeCatalogo") == null)
					pathProperties += "conf/teca_digitale";
				else
					pathProperties += this.getServletContext().getInitParameter("nomeCatalogo");
			}

			fileConf = new String[] { "Uol2010Banco.properties", "Uol2010Storicizzazione.properties",
					"Uol2010Comune.properties" };
//		tipoUtente = new String[] { "Banco", "Storico" };
//		Configuration.init(pathProperties, fileConf, tipoUtente);
			Configuration.init(pathProperties, fileConf);

			if (((String) Configuration.getValueDefault("banco.chiamate", "false")).equalsIgnoreCase("true")) {
				chiamate = new Chiamate();
				chiamate.setServer((String) Configuration.getValue("banco.chiamate.indirizzoIP"));
				chiamate.setPort(Integer.parseInt((String) Configuration.getValue("banco.chiamate.porta")));
				chiamate.start();
			}
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		}
	}
}