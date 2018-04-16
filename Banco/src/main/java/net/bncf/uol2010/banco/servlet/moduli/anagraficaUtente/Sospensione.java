/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente;

import java.util.ArrayList;
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
import net.bncf.uol2010.database.schema.servizi.dao.ServiziDAO;
import net.bncf.uol2010.database.schema.servizi.dao.SospensioniDAO;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteDAO;
import net.bncf.uol2010.database.schema.servizi.entity.Servizi;
import net.bncf.uol2010.database.schema.servizi.entity.Sospensioni;
import net.bncf.uol2010.database.schema.servizi.entity.Utente;
import net.bncf.uol2010.utility.xsd.BancoXsd;

/**
 * @author massi
 *
 */
public class Sospensione {

	/**
	 * Questo metodo viene utilizzato per aggiornare la sospensione dell'utente
	 * 
	 * @throws SOAPException
	 * @throws HibernateUtilException
	 */
	public static void addSospensione(HttpServletRequest request, DatiXml datiXml, MessageElement element,
			BancoXsd bancoXsd) throws SOAPException, HibernateUtilException {
		SospensioniDAO sospensioniDAO = null;
		UtenteDAO utenteDAO = null;
		ServiziDAO serviziDAO = null;
		List<Sospensioni> sospensionis = null;
		Sospensioni sospensioni = null;
		Utente utente = null;
		Servizi servizi = null;

		try {
			utenteDAO = new UtenteDAO();
			utente = utenteDAO.findById(request.getParameter("idUtente"));

			if (!request.getParameter("idServizi").equals("")) {
				serviziDAO = new ServiziDAO();
				servizi = serviziDAO.findById(request.getParameter("idServizi"));
			}
			sospensioniDAO = new SospensioniDAO();
			sospensionis = sospensioniDAO.find(utente, servizi, new GregorianCalendar(), "N", null);

			if (sospensionis == null || sospensionis.size() == 0) {
				sospensioni = new Sospensioni();
				sospensioni.setCancellato("N");
				sospensioni.setDataFin(FactoryDAO.convertTimestamp(request.getParameter("dataFin")));
				sospensioni.setDataIni(FactoryDAO.convertTimestamp(request.getParameter("dataIni")));
				sospensioni.setIdServizi(servizi);
				sospensioni.setIdUtente(utente);
				sospensioni.setNote(request.getParameter("note"));
				sospensioniDAO.save(sospensioni);
				element.addChildElement(getSospensioni(request.getParameter("idUtente"),
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
	 * @throws SOAPException, NumberFormatException 
	 * @throws HibernateUtilException 
	 */
	public static void delSospensione(HttpServletRequest request, DatiXml datiXml, MessageElement element,
			BancoXsd bancoXsd) throws SOAPException, NumberFormatException, HibernateUtilException {
		SospensioniDAO sospensioniDAO = null;
		Sospensioni sospensioni = null;

		try {
			sospensioniDAO = new SospensioniDAO();
			sospensioni = sospensioniDAO.findById(new Integer(request.getParameter("idSospensione")));
			if (sospensioni != null) {
				sospensioni.setCancellato("S");
				sospensioniDAO.update(sospensioni);
				element.addChildElement(getSospensioni(request.getParameter("idUtente"),
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
	 * Questo metodo restituisce la lista delle sospensioni disponibili
	 * 
	 * @param idUtente
	 *            Identificativo dell'utente
	 * @return
	 * @throws SOAPException 
	 * @throws HibernateUtilException 
	 */
	public static MessageElement getSospensioni(String idUtente, String idAutorizzazioniUtente, DatiXml datiXml,
			BancoXsd bancoXsd, boolean showAll) throws SOAPException, HibernateUtilException {
		SospensioniDAO sospensioniDAO = null;
		UtenteDAO utenteDAO = null;
		List<Sospensioni> sospensionis = null;
		Utente utente = null;
		List<Order> orders = null;

		MessageElement sospensioni = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		Vector<String> keyAttr = new Vector<String>();
		Vector<String> valueAttr = new Vector<String>();
		Vector<String> obbAttr = new Vector<String>();
		List<String> listaServ = new ArrayList<String>();

		try {
			sospensioni = new MessageElement();
			sospensioni.setName("sospensioni");

			utenteDAO = new UtenteDAO();
			utente = utenteDAO.findById(idUtente);
			sospensioniDAO = new SospensioniDAO();

			orders = new Vector<Order>();
			orders.add(Order.asc("servizi.descrizione"));
			orders.add(Order.desc("dataIni"));

			if (!showAll) {
				sospensionis = sospensioniDAO.find(utente, new GregorianCalendar(), "N", orders);
			} else {
				sospensionis = sospensioniDAO.find(utente, null, null, orders);
			}

			if (sospensionis != null && sospensionis.size() > 0) {
				for (Sospensioni sospensioni2 : sospensionis){
					keyAttr.removeAllElements();
					valueAttr.removeAllElements();
					obbAttr.removeAllElements();

					FactoryDAO.initialize(sospensioni2.getIdServizi());
					listaServ.add(sospensioni2.getIdServizi().getId() == null ? "null"
							: sospensioni2.getIdServizi().getId());
					keyAttr.add("idServizi");
					valueAttr.add(sospensioni2.getIdServizi().getId());
					obbAttr.add("false");

					keyAttr.add("idSospensione");
					valueAttr.add(sospensioni2.getId()+"");
					obbAttr.add("false");

					keyAttr.add("dataIni");
					valueAttr.add(FactoryDAO.converDateIta(sospensioni2.getDataIni()));
					obbAttr.add("true");

					keyAttr.add("dataFin");
					valueAttr.add(FactoryDAO.converDateIta(sospensioni2.getDataFin()));
					obbAttr.add("true");

					keyAttr.add("note");
					valueAttr.add(sospensioni2.getNote());
					obbAttr.add("false");

					keyAttr.add("cancellato");
					valueAttr.add(sospensioni2.getCancellato());
					obbAttr.add("false");

					datiXml.getConvert().addChildElement(sospensioni, nodes, "sospensione",
							sospensioni2.getIdServizi().getDescrizione(), keyAttr, valueAttr, false, obbAttr);
				}
			}
			sospensioni.addChildElement(
					Autorizzazione.getServizi(idAutorizzazioniUtente, false, listaServ, datiXml, bancoXsd));
		} catch (SOAPException e) {
			throw e;
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		}
		return sospensioni;
	}

}
