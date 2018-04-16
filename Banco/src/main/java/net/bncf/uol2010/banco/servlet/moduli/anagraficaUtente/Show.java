/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;

import mx.randalf.configuration.Configuration;
import mx.randalf.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.database.schema.servizi.dao.UtenteDAO;
import net.bncf.uol2010.database.schema.servizi.entity.Utente;

/**
 * Questa classe viene utilizzata per gestire la visualizzazione dell'anagrafica
 * Utente
 * 
 * @author Massimiliano Randazzo
 *
 */
public class Show {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Show.class, "net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");

	/**
	 * Questo metodo viene utilizzato per eseguire la ricerca e visualizzare il
	 * risultato dell'archivio Utenti
	 * 
	 * @param request
	 * @param show
	 * @param datiXml
	 */
	@SuppressWarnings("unchecked")
	public static MessageElement show(HttpServletRequest request, MessageElement show, DatiXml datiXml,
			String idUtenteNew) {
		UtenteDAO utenteDAO = null;
		List<Utente> utentes = null;

		String idUtente = null;
		String cognome = null;
		String nome = null;
		DecimalFormat df7 = new DecimalFormat("0000000");
		MessageElement risultati = null;
		MessageElement risultato = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();

		try {
			if ((idUtenteNew != null && !idUtenteNew.trim().equals("")) ||
					(request.getParameter("pagIni")== null ||
					request.getParameter("pagNum")== null)){
				utenteDAO = new UtenteDAO();
			} else{
				utenteDAO = new UtenteDAO(new Integer(request.getParameter("pagIni")), 
						new Integer(request.getParameter("pagNum")));
			}

			if (idUtenteNew != null && !idUtenteNew.trim().equals(""))
				idUtente = idUtenteNew;
			else {
				if (request.getParameter("Ric_idUtente") != null && !request.getParameter("Ric_idUtente").equals("")) {
					idUtente = request.getParameter("Ric_idUtente").toUpperCase();

					if (idUtente.startsWith("CFU"))
						idUtente = " CF" + df7.format(new Integer(idUtente.substring(3)));
					else if (idUtente.startsWith("CF"))
						idUtente = " CF" + df7.format(new Integer(idUtente.substring(2)));
					else if (idUtente.startsWith(" CF"))
						idUtente = " CF" + df7.format(new Integer(idUtente.substring(3)));
					else
						idUtente = " CF" + df7.format(new Integer(idUtente));
				} else {
					if (request.getParameter("Ric_cognomeKey") != null
							&& !request.getParameter("Ric_cognomeKey").equals("")) {
						cognome = request.getParameter("Ric_cognomeKey");
					}
					if (request.getParameter("Ric_nomeKey") != null
							&& !request.getParameter("Ric_nomeKey").equals("")) {
						nome = request.getParameter("Ric_nomeKey");
					}
				}
			}

			utenteDAO.getsetPage(Integer
					.parseInt((String) Configuration.getValueDefault("anagraficaUtente.navigazione.numRec", "10")));
			utentes = utenteDAO.findAll()find(idUtente, cognome, nome);
			utente.setNumPagVisual(Integer
					.parseInt((String) Configuration.listaParametri.get("anagraficaUtente.navigazione.numPag", "10")));
			utente.setNumRecVisual(Integer
					.parseInt((String) Configuration.listaParametri.get("anagraficaUtente.navigazione.numRec", "10")));
			rsUtente = utente.startSelect();

			risultati = new MessageElement();
			risultati.setName("risultati");
			if (utente.getRecTot() > 0) {
				show.addChildElement(
						utenteDAO.viewNavigatore(datiXml.getConvert(), datiXml.getConvertUri(), "AnaUte", "show"));

				while (rsUtente.next()) {
					if (rsUtente.getRow() <= utente.getRecFin()) {
						risultato = new MessageElement();
						risultato.setName("risultato");
						datiXml.getConvert().addChildElement(risultato, nodes, "idUtente",
								rsUtente.getString("idUtente"), true);
						datiXml.getConvert().addChildElement(risultato, nodes, "numRecord", rsUtente.getRow(), true);
						datiXml.getConvert().addChildElement(risultato, nodes, "cognome", rsUtente.getString("cognome"),
								false);
						datiXml.getConvert().addChildElement(risultato, nodes, "nome", rsUtente.getString("nome"),
								false);
						datiXml.getConvert().addChildElement(risultato, nodes, "indirizzo",
								rsUtente.getString("residenzaindirizzo"), false);
						datiXml.getConvert().addChildElement(risultato, nodes, "citta",
								rsUtente.getString("residenzacitta"), false);
						datiXml.getConvert().addChildElement(risultato, nodes, "autorizzazioneUte",
								rsUtente.getString("descAutorizzazioneUte"), false);
						if (rsUtente.getTimestamp("dataNascita") != null)
							datiXml.getConvert().addChildElement(risultato, nodes, "dataNascita",
									MsSql.conveDateIta(rsUtente.getTimestamp("dataNascita")), false);
						datiXml.getConvert().addChildElement(risultato, nodes, "luogoNascita",
								rsUtente.getString("luogoNascita"), false);

						risultati.addChildElement(risultato);
					} else
						break;
				}

			}
			show.addChildElement(risultati);
		} catch (NumberFormatException e) {
			log.error(e);
		} catch (SOAPException e) {
			log.error(e);
		} catch (SQLException e) {
			log.error(e);
		} finally {
			try {
				if (rsUtente != null)
					rsUtente.close();
				if (utente != null)
					utente.stopSelect();
			} catch (SQLException e) {
				log.error(e);
			}
		}
		return show;
	}

}
