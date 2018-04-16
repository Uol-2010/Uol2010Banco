/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import mx.randalf.hibernate.FactoryDAO;
import mx.randalf.hibernate.exception.HibernateUtilException;
import mx.randalf.moduli.servlet.core.exception.StdModuliException;
import mx.randalf.tools.MD5Tools;
import net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente.Autorizzazione;
import net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente.Edit;
import net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente.Print;
import net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente.Show;
import net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente.Sospensione;
import net.bncf.uol2010.database.schema.servizi.dao.AutorizzazioniUteDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteCittadinanzaDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteProfessioneDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteProvenienzaDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteTipoDocumentoDAO;
import net.bncf.uol2010.database.schema.servizi.entity.AutorizzazioniUte;
import net.bncf.uol2010.database.schema.servizi.entity.Utente;
import net.bncf.uol2010.database.schema.servizi.entity.UtenteCittadinanza;
import net.bncf.uol2010.database.schema.servizi.entity.UtenteProfessione;
import net.bncf.uol2010.database.schema.servizi.entity.UtenteProvenienza;
import net.bncf.uol2010.database.schema.servizi.entity.UtenteTipoDocumento;

/**
 * @author massi
 *
 */
public class AnagraficaUtente extends StdModuliBanco {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(AnagraficaUtente.class);

	/**
	 * Questa variabile viene utilizzata per verificare se si sta' utilizzando
	 * una chiamata Ajax
	 */
	private boolean ajax = false;

	/**
	 * Costruttore
	 */
	public AnagraficaUtente() {
	}

	/**
	 * Questo metodo viene utilizzato per gestire la sezione di modifica
	 * 
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException {
		MessageElement edit = null;
		Edit editor = null;

		try {
			log.debug("AnagraficaUtente - edit");
			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/AnagraficaUtente.css");
			// datiXml.addStyleSheet("../style/calendar/jscal2.css");
			// datiXml.addStyleSheet("../style/calendar/border-radius.css");
			// datiXml.addStyleSheet("../style/calendar/gold/gold.css");

			datiXml.addJavaScript("../js/calendar/verifyDataFormat.js");
			// datiXml.addJavaScript("../js/calendar/jscal2.js");
			// datiXml.addJavaScript("../js/calendar/lang/it.js");
			datiXml.addJavaScript("../js/anagraficaUtente/Autorizzazioni.js");
			datiXml.addJavaScript("../js/anagraficaUtente/Sospensione.js");
			datiXml.addJavaScript("../js/anagraficaUtente/ResetPwd.js");
			datiXml.addJavaScript("../js/anagraficaUtente/Edit.js");
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Edit(datiXml, banco);
			editor.edit(request.getParameter("idUtente"), edit);
			element.addChildElement(edit);
		} catch (SOAPException e) {
			log.error(e);
		} finally {
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null) {
				log.debug("AddElement");
				datiXml.addElement(element);
			}

		}
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare per informazioni
	 * relative alla visualizzazione
	 * 
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init() {
		this.fileXsl = "AnagraficaUtente.xsl";
		datiXml.setTitle("Anagrafica Utente");
		datiXml.addStyleSheet("../style/AnagraficaUtente.css");

		datiXml.addJavaScript("../js/mx/testJS.js");
		datiXml.addJavaScript("../js/mx/div.js");
		datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
		datiXml.addJavaScript("../js/anagraficaUtente/AnagraficaUtente.js");

		element = new MessageElement();
		element.setName("anagraficaUtente");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException {
		Print print = null;
		UtenteDAO utenteDAO = null;
		Utente utente = null;

		try {
			log.debug("AnagraficaUtente - extend");
			if (request.getParameter("azione") != null) {
				if (request.getParameter("azione").equals("addAutorizzazione")) {
					ajax = true;
					Autorizzazione.addAutorizzazione(request, datiXml, element, banco);
				} else if (request.getParameter("azione").equals("delAutorizzazione")) {
					ajax = true;
					Autorizzazione.delAutorizzazione(request, datiXml, element, banco);
				} else if (request.getParameter("azione").equals("addSospensione")) {
					ajax = true;
					Sospensione.addSospensione(request, datiXml, element, banco);
				} else if (request.getParameter("azione").equals("delSospensione")) {
					ajax = true;
					Sospensione.delSospensione(request, datiXml, element, banco);
				} else if (request.getParameter("azione").equals("resetPwd")) {
					ajax = true;
					resetPwd();
				} else if (request.getParameter("azione").equals("print")) {
					ajax = true;
					print = new Print();
					utenteDAO = new UtenteDAO();
					utente = utenteDAO.findById(request.getParameter("idUtente"));
					print.print(utente);
				} else if (request.getParameter("azione").equals("popSosAttive")) {
					ajax = true;
					element.addChildElement(Sospensione.getSospensioni(request.getParameter("idUtente"),
							request.getParameter("idAutorizzazioniUtente"), datiXml, banco,
							(request.getParameter("showAll") != null
									&& request.getParameter("showAll").equals("true"))));
					datiXml.addElement(element);
				} else if (request.getParameter("azione").equals("popAutAttive")) {
					ajax = true;
					element.addChildElement(Autorizzazione.getAutorizzazioni(request.getParameter("idUtente"),
							request.getParameter("idAutorizzazioniUtente"), datiXml, banco,
							(request.getParameter("showAll") != null
									&& request.getParameter("showAll").equals("true"))));
					datiXml.addElement(element);
				}
			}
		} catch (SOAPException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		} catch (HibernateUtilException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(),e);
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException {
		log.debug("AnagraficaUtente - result");
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#show(java.lang.String)
	 */
	@Override
	protected void show(String idUtente) throws ServletException, IOException {
		MessageElement show = null;
		// ArrayList nodes = new ArrayList();

		try {
			log.debug("AnagraficaUtente - show");
			super.show(idUtente);

			show = new MessageElement();
			show.setName("show");
			if (idUtente != null && !idUtente.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idUtente", idUtente, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idUtente", request.getParameter("Ric_idUtente"), false);
			datiXml.getConvert().addChildElement(show, "Ric_cognomeKey", request.getParameter("Ric_cognomeKey"), false);
			datiXml.getConvert().addChildElement(show, "Ric_nomeKey", request.getParameter("Ric_nomeKey"), false);

			if ((request.getParameter("ricerca") != null && request.getParameter("ricerca").equals("Y"))
					|| (idUtente != null && !idUtente.trim().equals("")))
				show = Show.show(request, show, datiXml, idUtente);
			element.addChildElement(show);
		} catch (SOAPException e) {
			log.error(e);
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() 
			 throws ServletException, IOException, StdModuliException 
	{
		UtenteDAO utenteDAO = null;
		AutorizzazioniUteDAO autorizzazioniUteDAO = null;
		UtenteProfessioneDAO utenteProfessioneDAO = null;
		UtenteProvenienzaDAO utenteProvenienzaDAO = null;
		UtenteCittadinanzaDAO utenteCittadinanzaDAO = null;
		UtenteTipoDocumentoDAO utenteTipoDocumentoDAO = null;
		Utente utente = null;
		AutorizzazioniUte autorizzazioniUte = null;
		UtenteProfessione utenteProfessione = null;
		UtenteProvenienza utenteProvenienza = null;
		UtenteCittadinanza utenteCittadinanza = null;
		UtenteTipoDocumento utenteTipoDocumento = null;

		Print print = null;

			try {
				log.debug("AnagraficaUtente - write");

				if (request.getParameter("idAutorizzazioniUtente") != null
						&& !request.getParameter("idAutorizzazioniUtente").trim().equals("")) {
					autorizzazioniUteDAO = new AutorizzazioniUteDAO();
					autorizzazioniUte = autorizzazioniUteDAO.findById(request.getParameter("idAutorizzazioniUtente"));
				}

				if (request.getParameter("idUtenteProfessione") != null
						&& !request.getParameter("idUtenteProfessione").trim().equals("")) {
					utenteProfessioneDAO = new UtenteProfessioneDAO();
					utenteProfessione = utenteProfessioneDAO.findById(request.getParameter("idUtenteProfessione"));
				}

				if (request.getParameter("idUtenteProvenienza") != null
						&& !request.getParameter("idUtenteProvenienza").trim().equals("")) {
					utenteProvenienzaDAO = new UtenteProvenienzaDAO();
					utenteProvenienza = utenteProvenienzaDAO.findById(request.getParameter("idUtenteProvenienza"));
				}

				if (request.getParameter("idUtenteCittadinanza") != null
						&& !request.getParameter("idUtenteCittadinanza").trim().equals("")) {
					utenteCittadinanzaDAO = new UtenteCittadinanzaDAO();
					utenteCittadinanza = utenteCittadinanzaDAO.findById(request.getParameter("idUtenteCittadinanza"));
				}

				if (request.getParameter("idUtenteTipoDocumento") != null
						&& !request.getParameter("idUtenteTipoDocumento").trim().equals("")) {
					utenteTipoDocumentoDAO = new UtenteTipoDocumentoDAO();
					utenteTipoDocumento = utenteTipoDocumentoDAO.findById(request.getParameter("idUtenteTipoDocumento"));
				}

				utenteDAO = new UtenteDAO();

				utente = utenteDAO.write(request.getParameter("idUtente"), autorizzazioniUte, utenteProfessione,
						utenteProvenienza, utenteCittadinanza, utenteTipoDocumento, request.getParameter("cognome"),
						request.getParameter("nome"), request.getParameter("sesso"),
						request.getParameter("residenzaIndirizzo"), request.getParameter("residenzaCitta"),
						request.getParameter("residenzaCap"), request.getParameter("domicilioIndirizzo"),
						request.getParameter("domicilioCitta"), request.getParameter("domicilioCap"),
						request.getParameter("telefono"), request.getParameter("cellulare"),
						request.getParameter("luogoNascita"), FactoryDAO.convertDate(request.getParameter("dataNascita")),
						request.getParameter("numeroDocumento"), request.getParameter("autoritaRilascio"),
						request.getParameter("codiceFiscale"), request.getParameter("note"), request.getParameter("email"),
						new Integer(request.getParameter("limiteTraffico")));

				if (request.getParameter("print") != null && request.getParameter("print").equals("true")) {
					print = new Print();
					print.print(utente);
				}
				if (request.getParameter("close") != null && request.getParameter("close").equals("true"))
					show(null);
				else
					edit();
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null) {
					log.debug("AddElement");
					if (request.getParameter("close") != null && request.getParameter("close").equals("true"))
						datiXml.addElement(element);
				}
			} catch (NumberFormatException e) {
				throw new ServletException(e.getMessage(),e);
			} catch (HibernateException e) {
				throw new ServletException(e.getMessage(),e);
			} catch (HibernateUtilException e) {
				throw new ServletException(e.getMessage(),e);
			} catch (ServletException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			}
	}
//
//	/**
//	 * Questo metodo viene utilizzato per ricavare la password dell'utente
//	 * Criptata
//	 * 
//	 * @param idUtente
//	 * @return
//	 * @throws HibernateUtilException 
//	 */
//	private void initUtente(Utente datiUtenti) throws HibernateException, HibernateUtilException {
//		UtenteDAO utenteDAO = null;
//		Utente utente = null;
//
//		try {
//			utenteDAO = new UtenteDAO();
//			utente = utenteDAO.findById(datiUtenti.getId());
//			if (utente != null) {
//				datiUtenti.setPassword(utente.getPassword());
//				datiUtenti.setDataIns(utente.getDataIns());
//				datiUtenti.setDataMod(new Timestamp(new GregorianCalendar().getTimeInMillis()));
//			}
//		} catch (HibernateException e) {
//			throw e;
//		} catch (HibernateUtilException e) {
//			throw e;
//		}
//	}

	/**
	 * Questo metodo viene utilizzato per aggiornare la password dell'utente
	 * @throws HibernateException 
	 * @throws HibernateUtilException 
	 * @throws SOAPException 
	 * @throws NoSuchAlgorithmException 
	 */
	private void resetPwd() throws HibernateException, HibernateUtilException, SOAPException, NoSuchAlgorithmException {
		UtenteDAO utenteDAO = null;
		Utente utente = null;
		// ArrayList nodes = new ArrayList();

		try {
			utenteDAO = new UtenteDAO();
			utente = utenteDAO.findById(request.getParameter("idUtente"));
			if (utente != null &&
					utente.getPassword().equals(request.getParameter("oldPwd"))){
				utente.setPassword(MD5Tools.checkSum("welcome".getBytes()));
				utenteDAO.update(utente);
				datiXml.getConvert().addChildElement(element, "ResetPwd", "welcome", true);
			}
		} catch (HibernateException e) {
			throw e;
		} catch (HibernateUtilException e) {
			throw e;
		} catch (SOAPException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
	}

	@Override
	protected void endEsegui(String pathXsl) throws ServletException {
		if (getSqlErr() != null || !ajax)
			super.endEsegui(pathXsl);
		else {
			try {
				response.setContentType("text/xml");
				response.setCharacterEncoding("utf-8");

				datiXml.printOutputStream(response.getOutputStream());
			} catch (SOAPException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}
