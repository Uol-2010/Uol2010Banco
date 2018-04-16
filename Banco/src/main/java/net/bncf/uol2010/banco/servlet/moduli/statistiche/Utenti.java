/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.statistiche;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlPool;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione delle Statistiche dei
 * Movimenti
 * 
 * @author MRandazzo
 * 
 */
public class Utenti extends StdModuliBanco {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(Utenti.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * 
	 */
	public Utenti() {
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare per informazioni
	 * relative alla visualizzazione
	 * 
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init() {
		this.fileXsl = "StatisticheUtenti.xsl";
		datiXml.setTitle("Statistiche Utenti");
		datiXml.addStyleSheet("../style/StatisticheUtenti.css");

		datiXml.addJavaScript("../js/flot/excanvas.min.js#IE");
		datiXml.addJavaScript("http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js");
		datiXml.addJavaScript("../js/flot/jquery.flot.js");
		datiXml.addJavaScript("../js/flot/jquery.flot.orderBars.js");
		datiXml.addJavaScript("../js/StatisticheUtenti/StatisticheUtenti.js");

		element = new MessageElement();
		element.setName("statisticheUtenti");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException {
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException {
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException {
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#show(java.lang.String)
	 */
	@Override
	protected void show(String id) throws ServletException, IOException {
		MessageElement show = null;

		try {
			log.debug("Statistiche Utenti - show");
			super.show(id);
//			datiXml.addStyleSheet("../style/calendar/jscal2.css");
//			datiXml.addStyleSheet("../style/calendar/border-radius.css");
//			datiXml.addStyleSheet("../style/calendar/gold/gold.css");

			datiXml.addJavaScript("../js/calendar/verifyDataFormat.js");
//			datiXml.addJavaScript("../js/calendar/jscal2.js");
//			datiXml.addJavaScript("../js/calendar/lang/it.js");
			show = new MessageElement();
			show.setName("show");
			if (request.getParameter("Ric_dataStart") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStart",
						request.getParameter("Ric_dataStart"), false);
			if (request.getParameter("Ric_dataStop") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStop",
						request.getParameter("Ric_dataStop"), false);
			if ((request.getParameter("ricerca") != null && request
					.getParameter("ricerca").equals("Y"))
					|| (id != null && !id.trim().equals(""))) {
				getStatNuoviUtentiPerTipoTessera(show);
			}
			element.addChildElement(show);
		} catch (SOAPException e) {
			log.error(e);
		}
	}

	/**
	 * Questo metodo viene utilizzato per calcolare le statistiche degli
	 * ingressi per autorizzazione Utente
	 * 
	 * @param show
	 */
	private void getStatNuoviUtentiPerTipoTessera(MessageElement show) {
		MsSqlPool msp = null;
		String sql = "";
		ResultSet rsStatistiche = null;
		MessageElement autorizzazioniUtenti = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAtr = null;
		Vector<String> obbAttr = null;
		int totale = 0;

		try {
			msp = Configuration.poolUol2010.getConn();

			sql = "select utente.id_autorizzazioniutente, autorizzazioniute.descrizione, count(*) "
					+ "from utente, autorizzazioniute "
					+ "where datains between '"
					+ request.getParameter("Ric_dataStart")
					+ "' and '"
					+ request.getParameter("Ric_dataStop")
					+ "' and "
					+ "utente.id_autorizzazioniutente=autorizzazioniute.id_autorizzazioniutente "
					+ "group by utente.id_autorizzazioniutente, autorizzazioniute.descrizione "
					+ "order by autorizzazioniute.descrizione";

			autorizzazioniUtenti = new MessageElement();
			autorizzazioniUtenti.setName("nuoviUtentiPerTipoTessera");
			rsStatistiche = msp.StartSelect(sql);
			while (rsStatistiche.next()) {
				keyAttr = new Vector<String>();
				valueAtr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAtr.add(rsStatistiche.getString(1));
				obbAttr.add("true");

				keyAttr.add("numero");
				valueAtr.add(rsStatistiche.getString(3));
				obbAttr.add("true");

				totale += rsStatistiche.getInt(3);
				datiXml.getConvert().addChildElement(autorizzazioniUtenti,
						"nuovoUtentePerTipoTessera",
						rsStatistiche.getString(2), keyAttr, valueAtr, true,
						obbAttr);
			}
			keyAttr = new Vector<String>();
			valueAtr = new Vector<String>();
			obbAttr = new Vector<String>();

			keyAttr.add("id");
			valueAtr.add("");
			obbAttr.add("true");

			keyAttr.add("numero");
			valueAtr.add(Integer.toString(totale));
			obbAttr.add("true");
			datiXml.getConvert().addChildElement(autorizzazioniUtenti,
					"nuovoUtentePerTipoTessera", "Totale", keyAttr, valueAtr,
					true, obbAttr);
		} catch (SQLException e) {
			log.error(e);
		} catch (SOAPException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (rsStatistiche != null)
					rsStatistiche.close();
				if (msp != null) {
					msp.StopSelect();
					Configuration.poolStorico.releaseConn(msp);
				}
				show.addChildElement(autorizzazioniUtenti);
			} catch (SQLException e) {
				log.error(e);
			} catch (SOAPException e) {
				log.error(e);
			}
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException {
	}

}
