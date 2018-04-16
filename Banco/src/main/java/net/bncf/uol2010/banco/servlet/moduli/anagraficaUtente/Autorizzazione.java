/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;

import mx.randalf.hibernate.FactoryDAO;
import mx.randalf.hibernate.exception.HibernateUtilException;
import mx.randalf.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.database.schema.servizi.dao.AutUteServiziDAO;
import net.bncf.uol2010.database.schema.servizi.dao.AutorizzazioneDAO;
import net.bncf.uol2010.database.schema.servizi.dao.AutorizzazioniUteDAO;
import net.bncf.uol2010.database.schema.servizi.dao.ServiziDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteDAO;
import net.bncf.uol2010.database.schema.servizi.entity.AutUteServizi;
import net.bncf.uol2010.database.schema.servizi.entity.AutorizzazioniUte;
import net.bncf.uol2010.database.schema.servizi.entity.Servizi;
import net.bncf.uol2010.database.schema.servizi.entity.Utente;
import net.bncf.uol2010.utility.xsd.BancoXsd;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente.Autorizzazioni.Diritto;

/**
 * Questa classe viene utilizzata per la gestione dell'autorizzazione
 * dell'anagrafica Utente
 * 
 * @author Massimiliano Randazzo
 *
 */
public class Autorizzazione {

	/**
	 * Questo metodo viene utilizzato per aggiornare l'autorizzazione
	 * dell'utente
	 * 
	 * @throws SOAPException
	 * @throws HibernateUtilException
	 */
	public static void addAutorizzazione(HttpServletRequest request, DatiXml datiXml, MessageElement element,
			BancoXsd bancoXsd) throws SOAPException, HibernateUtilException {
		AutorizzazioneDAO autorizzazioneDAO = null;
		UtenteDAO utenteDAO = null;
		ServiziDAO serviziDAO = null;
		Utente utente = null;
		Servizi servizi = null;
		net.bncf.uol2010.database.schema.servizi.entity.Autorizzazione autorizazione = null;
		List<net.bncf.uol2010.database.schema.servizi.entity.Autorizzazione> autorizzaziones = null;
		GregorianCalendar data = new GregorianCalendar();

		try {
			utenteDAO = new UtenteDAO();
			utente = utenteDAO.findById(request.getParameter("idUtente"));

			serviziDAO = new ServiziDAO();
			servizi = serviziDAO.findById(request.getParameter("idServizi"));

			autorizzazioneDAO = new AutorizzazioneDAO();

			if (request.getParameter("idAutorizzazione").equals("0")) {

				autorizzaziones = autorizzazioneDAO.find(utente, servizi, data, "N", null);

				if (autorizzaziones == null || autorizzaziones.size() == 0) {
					autorizazione = new net.bncf.uol2010.database.schema.servizi.entity.Autorizzazione();
					autorizazione.setCancellato("N");
					autorizazione.setData(new Date(data.getTimeInMillis()));
					autorizazione.setDataFin(FactoryDAO.convertDate(request.getParameter("dataFin")));
					autorizazione.setDataIni(FactoryDAO.convertDate(request.getParameter("dataIni")));
					autorizazione.setIdServizi(servizi);
					autorizazione.setIdUtente(utente);
					autorizzazioneDAO.save(autorizazione);
					element.addChildElement(getAutorizzazioni(request.getParameter("idUtente"),
							request.getParameter("idAutorizzazioniUtente"), datiXml, bancoXsd, false));
				}
			} else {
				autorizazione = autorizzazioneDAO.findById(new Integer(request.getParameter("idAutorizzazione")));

				autorizazione.setIdUtente(utente);
				autorizazione.setIdServizi(servizi);
				autorizazione.setCancellato("N");
				autorizazione.setDataFin(FactoryDAO.convertDate(request.getParameter("dataFin")));
				autorizazione.setDataIni(FactoryDAO.convertDate(request.getParameter("dataIni")));

				autorizzazioneDAO.update(autorizazione);
				element.addChildElement(getAutorizzazioni(request.getParameter("idUtente"),
						request.getParameter("idAutorizzazioniUtente"), datiXml, bancoXsd, false));
			}
		} catch (SOAPException e) {
			throw e;
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		} finally {
			datiXml.addElement(element);
		}
	}

	/**
	 * Questo metodo viene utilizzato per aggiornare l'autorizzazione
	 * dell'utente
	 * 
	 * @throws SOAPException
	 * @throws HibernateUtilException
	 */
	public static void delAutorizzazione(HttpServletRequest request, DatiXml datiXml, MessageElement element,
			BancoXsd bancoXsd) throws SOAPException, NumberFormatException, HibernateUtilException {
		AutorizzazioneDAO autorizzazioneDAO = null;
		net.bncf.uol2010.database.schema.servizi.entity.Autorizzazione autorizzazione = null;

		try {
			autorizzazioneDAO = new AutorizzazioneDAO();
			autorizzazione = autorizzazioneDAO.findById(new Integer(request.getParameter("idAutorizzazione")));
			if (autorizzazione != null) {

				autorizzazione.setCancellato("S");
				autorizzazioneDAO.update(autorizzazione);
				element.addChildElement(getAutorizzazioni(request.getParameter("idUtente"),
						request.getParameter("idAutorizzazioniUtente"), datiXml, bancoXsd, false));
			}
		} catch (SOAPException e) {
			throw e;
		} catch (NumberFormatException e) {
			throw e;
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		} finally {
			datiXml.addElement(element);
		}
	}

	/**
	 * Questo metodo restituisce la lista delle autorizzazioni disponibili
	 * 
	 * @param idUtente
	 * @param idAutorizzazioniUtente
	 * @return
	 * @throws SOAPException 
	 * @throws HibernateUtilException 
	 */
	public static MessageElement getAutorizzazioni(String idUtente, String idAutorizzazioniUtente, DatiXml datiXml,
			BancoXsd bancoXsd, boolean showAll) throws SOAPException, HibernateUtilException {
		AutorizzazioneDAO autorizzazioneDAO = null;
		UtenteDAO utenteDAO = null;
		List<net.bncf.uol2010.database.schema.servizi.entity.Autorizzazione> autorizzaziones = null;
		Utente utente = null;
		List<Order> orders = null;

		MessageElement autorizzazioni = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		Vector<String> keyAttr = new Vector<String>();
		Vector<String> valueAttr = new Vector<String>();
		Vector<String> obbAttr = new Vector<String>();
		List<String> listaServ = new ArrayList<String>();

		try {
			autorizzazioni = new MessageElement();
			autorizzazioni.setName("autorizzazioni");

			utenteDAO = new UtenteDAO();
			utente = utenteDAO.findById(idUtente);

			autorizzazioneDAO = new AutorizzazioneDAO();
			orders = new Vector<Order>();
			orders.add(Order.asc("servizio.descrizione"));
			orders.add(Order.desc("dataIni"));
			if (!showAll) {
				autorizzaziones = autorizzazioneDAO.find(utente, new GregorianCalendar(), "N", orders);
			} else {
				autorizzaziones = autorizzazioneDAO.find(utente, null, null, orders);
			}

			if (autorizzaziones != null && autorizzaziones.size() > 0) {
				for (net.bncf.uol2010.database.schema.servizi.entity.Autorizzazione autorizzazione : autorizzaziones) {
					keyAttr.removeAllElements();
					valueAttr.removeAllElements();
					obbAttr.removeAllElements();

					FactoryDAO.initialize(autorizzazione.getIdServizi());

					listaServ.add(autorizzazione.getIdServizi().getId());

					keyAttr.add("idServizi");
					valueAttr.add(autorizzazione.getIdServizi().getId());
					obbAttr.add("true");

					keyAttr.add("idAutorizzazione");
					valueAttr.add(autorizzazione.getId() + "");
					obbAttr.add("true");

					keyAttr.add("dataIni");
					valueAttr.add(FactoryDAO.converDateIta(autorizzazione.getDataIni()));
					obbAttr.add("true");

					keyAttr.add("dataFin");
					valueAttr.add(FactoryDAO.converDateIta(autorizzazione.getDataFin()));
					obbAttr.add("true");

					keyAttr.add("cancellato");
					valueAttr.add(autorizzazione.getCancellato());
					obbAttr.add("true");

					datiXml.getConvert().addChildElement(autorizzazioni, nodes, "autorizzazione",
							autorizzazione.getIdServizi().getDescrizione(), keyAttr, valueAttr, true, obbAttr);
				}
			}
			autorizzazioni.addChildElement(getServizi(idAutorizzazioniUtente, true, listaServ, datiXml, bancoXsd));
		} catch (SOAPException e) {
			throw e;
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		}

		return autorizzazioni;
	}

	/**
	 * Questo metodo viene utilizzato per ricavare la lista dei servizi relativi
	 * all'autorizzazione
	 * 
	 * @param idAutorizzazioniUtente
	 *            Identificativo dell'autorizzazione dell'utente
	 * @param checkRinnovoAutomatco
	 *            Viene utilizzato per indicare se testare il rinnovo automatico
	 * @return
	 * @throws SOAPException 
	 * @throws HibernateUtilException 
	 */
	public static MessageElement getServizi(String idAutorizzazioniUtente, boolean checkRinnovoAutomatco,
			List<String> listaServ, DatiXml datiXml, BancoXsd bancoXsd) throws SOAPException, HibernateUtilException {
		MessageElement servizi = null;
		AutorizzazioniUteDAO autorizzazioniUteDAO = null;
		AutorizzazioniUte autorizzazioniUte = null;
		AutUteServiziDAO autUteServiziDAO = null;
		List<AutUteServizi> autUteServizis = null;

		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		GregorianCalendar data = new GregorianCalendar();

		try {
			servizi = new MessageElement();
			servizi.setName("servizi");
			datiXml.getConvert().addChildElement(servizi, nodes, "dataIni", FactoryDAO.converDateIta(data), true);
			data.add(Calendar.YEAR, 1);
			datiXml.getConvert().addChildElement(servizi, nodes, "dataFin", FactoryDAO.converDateIta(data), true);

			if ((listaServ == null || !listaServ.contains("null")) && !checkRinnovoAutomatco)
				datiXml.getConvert().addChildElement(servizi, nodes, "servizio", "Tutti i servizi", "idServizi", "",
						true, false);

			autorizzazioniUteDAO = new AutorizzazioniUteDAO();
			autorizzazioniUte = autorizzazioniUteDAO.findById(idAutorizzazioniUtente);

			autUteServiziDAO = new AutUteServiziDAO();
			autUteServizis = autUteServiziDAO.find(autorizzazioniUte, null);

			if (autUteServizis != null && autUteServizis.size() > 0) {
				for (AutUteServizi autUteServizi : autUteServizis) {
					FactoryDAO.initialize(autUteServizi.getIdServizi());
					if ((listaServ == null || !listaServ.contains(autUteServizi.getIdServizi().getId()))
							&& checkDiritti(autUteServizi.getIdServizi().getId(), bancoXsd)) {
						if (!checkRinnovoAutomatco || autUteServizi.getRinnovoAutomatico() == 0) {
							datiXml.getConvert().addChildElement(servizi, nodes, "servizio",
									autUteServizi.getIdServizi().getDescrizione(), "idServizi",
									autUteServizi.getIdServizi().getId(), true, true);
						}
					}
				}
			}
		} catch (SOAPException e) {
			throw e;
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(),e);
		} catch (HibernateUtilException e) {
			throw e;
		}
		return servizi;
	}

	/**
	 * Questo metodo vine utilizzato per testare i diritti dell'utente
	 * 
	 * @param diritto
	 * @return
	 */
	public static boolean checkDiritti(String diritto, BancoXsd bancoXsd) {
		boolean ris = false;
		List<Diritto> diritti = null;

		if (bancoXsd != null && bancoXsd.getBanco() != null && bancoXsd.getBanco().getUtente() != null
				&& bancoXsd.getBanco().getUtente().getAutorizzazioni() != null
				&& bancoXsd.getBanco().getUtente().getAutorizzazioni().getDiritto() != null) {
			diritti = bancoXsd.getBanco().getUtente().getAutorizzazioni().getDiritto();
			for (int x = 0; x < diritti.size(); x++) {
				if (diritti.get(x).getID().equals(diritto)) {
					ris = true;
					break;
				}
			}
		}

		return ris;
	}
}
