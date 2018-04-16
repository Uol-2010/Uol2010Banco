/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.ricercaMaterialeStoricizzato;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import mx.database.MsSql;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.xml.DatiXml;
import mx.normalize.ConvertText;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.databaseStorico.table.Storico;

import org.apache.axis.message.MessageElement;

/**
 * @author massi
 *
 */
public class Show
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Show.class, "net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");

	@SuppressWarnings("unchecked")
	public static MessageElement show(HttpServletRequest request, MessageElement show, DatiXml datiXml, String idRichiestaNew)
	{
		Storico storico = null;
		ResultSet rsStorico = null;
		MessageElement risultati = null;
		MessageElement risultato = null;
		String idUtente = "";
		String[] st = null;

		try
		{
			if (idRichiestaNew != null && !idRichiestaNew.trim().equals(""))
				storico = new Storico(Configuration.poolStorico, "_"+request.getParameter("Ric_anno"));
			else
				storico = new Storico(Configuration.poolStorico, "_"+request.getParameter("Ric_anno"), request.getParameterMap());
			storico.setCampoValue("Ric_anno", request.getParameter("Ric_anno"));
			if (idRichiestaNew != null && !idRichiestaNew.trim().equals(""))
				storico.setCampoValue("idRichiesta", idRichiestaNew);
			else
			{
				if (request.getParameter("Ric_idRichieste") != null && 
						!request.getParameter("Ric_idRichieste").equals(""))
				{
					storico.setCampoValue("idRichiesta", request.getParameter("Ric_idRichieste"));
				}
				else
				{
					if (request.getParameter("Ric_idUtente") != null && 
							!request.getParameter("Ric_idUtente").equals(""))
					{
						idUtente = request.getParameter("Ric_idUtente").toUpperCase();
						
						if (idUtente.startsWith("CFU"))
							idUtente = " CF"+ConvertText.mettiZeri(idUtente.substring(3), 7);
						else if (idUtente.startsWith("CF"))
							idUtente = " CF"+ConvertText.mettiZeri(idUtente.substring(2), 7);
						else if (idUtente.startsWith(" CF"))
							idUtente = " CF"+ConvertText.mettiZeri(idUtente.substring(3), 7);
						else
							idUtente = " CF"+ConvertText.mettiZeri(idUtente, 7);
						storico.setCampoValue("idUtente", idUtente);
					}
					else if (request.getParameter("idUtente") != null && 
							!request.getParameter("idUtente").equals(""))
					{
						storico.setCampoValue("idUtente", request.getParameter("idUtente"));
					}
					else
					{
						if (request.getParameter("Ric_cognomeKey") != null &&
								!request.getParameter("Ric_cognomeKey").equals(""))
						{
							storico.setCampoValue("utenteCognomeKey", request.getParameter("Ric_cognomeKey"));
						}
						else if (request.getParameter("utenteCognomeKey") != null &&
								!request.getParameter("utenteCognomeKey").equals(""))
						{
							storico.setCampoValue("utenteCognomeKey", request.getParameter("utenteCognomeKey"));
						}
						if (request.getParameter("Ric_nomeKey") != null &&
								!request.getParameter("Ric_nomeKey").equals(""))
						{
							storico.setCampoValue("utenteNomeKey", request.getParameter("Ric_nomeKey"));
						}
						else if (request.getParameter("utenteNomeKey") != null &&
								!request.getParameter("utenteNomeKey").equals(""))
						{
							storico.setCampoValue("utenteNomeKey", request.getParameter("utenteNomeKey"));
						}
					}
					
					if (request.getParameter("Ric_dataStart") != null &&
							!request.getParameter("Ric_dataStart").equals(""))
					{
						storico.setCampoValue("Ric_dataStart", request.getParameter("Ric_dataStart"));
						if (request.getParameter("Ric_dataStop") != null &&
								!request.getParameter("Ric_dataStop").equals(""))
						{
							storico.setCampoValue("Ric_dataStop", request.getParameter("Ric_dataStop"));
  						storico.addWhere("DATA_INS BETWEEN " +
  								"'"+request.getParameter("Ric_dataStart")+" 00:00:00' AND " +
  								"'"+request.getParameter("Ric_dataStop")+" 23:59:59'");
						}
						else
						{
							st = request.getParameter("Ric_dataStart").split("/");
  						storico.addWhere("DATA_INS > " +
  								"'"+st[2]+"-"+st[1]+"-"+st[0]+" 00:00:00'");
						}
							
					}
					
					if (request.getParameter("Ric_idServizi") != null &&
							!request.getParameter("Ric_idServizi").equals(""))
						storico.setCampoValue("idServizi", request.getParameter("Ric_idServizi"));
					else if (request.getParameter("idServizi") != null &&
							!request.getParameter("idServizi").equals(""))
						storico.setCampoValue("idServizi", request.getParameter("idServizi"));
					
					if (request.getParameter("Ric_idStatoMovimenti") != null &&
							!request.getParameter("Ric_idStatoMovimenti").equals(""))
						storico.setCampoValue("idStatoMovimenti", request.getParameter("Ric_idStatoMovimenti"));
					else if (request.getParameter("idStatoMovimenti") != null &&
							!request.getParameter("idStatoMovimenti").equals(""))
						storico.setCampoValue("idStatoMovimenti", request.getParameter("idStatoMovimenti"));
					
					if (request.getParameter("Ric_inventario") != null &&
							!request.getParameter("Ric_inventario").equals(""))
						storico.setCampoValue("inventario", request.getParameter("Ric_inventario"));
					else if (request.getParameter("inventario") != null &&
							!request.getParameter("inventario").equals(""))
						storico.setCampoValue("inventario", request.getParameter("inventario"));
					
					if (request.getParameter("Ric_collocazione") != null &&
							!request.getParameter("Ric_collocazione").equals(""))
						storico.setCampoValue("segnaturaKey", request.getParameter("Ric_collocazione"));
					else if (request.getParameter("segnaturaKey") != null &&
							!request.getParameter("segnaturaKey").equals(""))
						storico.setCampoValue("segnaturaKey", request.getParameter("segnaturaKey"));
				}
			}

			storico.setNumPagVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numPag", "10")));
			storico.setNumRecVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numRec", "10")));
			rsStorico = storico.startSelect();

			risultati = new MessageElement();
			risultati.setName("risultati");
			if (storico.getRecTot()>0)
			{
				show.addChildElement(storico.viewNavigatore(datiXml.getConvert(), datiXml.getConvertUri(), "GestStor", "show"));

				while (rsStorico.next())
				{
				  if (rsStorico.getRow()<=storico.getRecFin())
				  {
						risultato = new MessageElement();
						risultato.setName("risultato");
						datiXml.getConvert().addChildElement(risultato, "idStorico", rsStorico.getString("idStorico"), true);
						datiXml.getConvert().addChildElement(risultato, "idRichieste", rsStorico.getString("idRichiesta"), true);
						datiXml.getConvert().addChildElement(risultato, "numRecord", rsStorico.getRow(), true);
						datiXml.getConvert().addChildElement(risultato, "utenteCognome", rsStorico.getString("utenteCognome"), "id", rsStorico.getString("idUtente"), false, false);
						datiXml.getConvert().addChildElement(risultato, "utenteNome", rsStorico.getString("utenteNome"), false);
						datiXml.getConvert().addChildElement(risultato, "statoMovimento", rsStorico.getString("statoMovimentoDescrizione"), "id", rsStorico.getString("idStatoMovimenti"), false, false);
						datiXml.getConvert().addChildElement(risultato, "servizio", rsStorico.getString("serviziDescrizione"), "id", rsStorico.getString("idServizi"), false, false);
						datiXml.getConvert().addChildElement(risultato, "progIter", rsStorico.getString("progIter"), false);
						if (rsStorico.getString("idFruibilita") != null)
							datiXml.getConvert().addChildElement(risultato, "fruibilita", rsStorico.getString("fruibilitaDescrizione"), "id", rsStorico.getString("idFruibilita"), false, false);
						datiXml.getConvert().addChildElement(risultato, "dataIns", MsSql.conveDateTimeIta(rsStorico.getTimestamp("dataIns")), false);
						datiXml.getConvert().addChildElement(risultato, "dataMod", MsSql.conveDateTimeIta(rsStorico.getTimestamp("dataMod")), false);
						datiXml.getConvert().addChildElement(risultato, "bid", rsStorico.getString("bid"), false);
						datiXml.getConvert().addChildElement(risultato, "autore", rsStorico.getString("autore"), false);
						datiXml.getConvert().addChildElement(risultato, "titolo", rsStorico.getString("titolo"), false);
						datiXml.getConvert().addChildElement(risultato, "pubblicazione", rsStorico.getString("pubblicazione"), false);
						datiXml.getConvert().addChildElement(risultato, "inventario", rsStorico.getString("inventario"), false);
						datiXml.getConvert().addChildElement(risultato, "precInv1", rsStorico.getString("precInv1"), false);
						datiXml.getConvert().addChildElement(risultato, "precInv2", rsStorico.getString("precInv2"), false);
						datiXml.getConvert().addChildElement(risultato, "segnatura", rsStorico.getString("segnatura"), "key", rsStorico.getString("segnaturaKey"), false, false);
						datiXml.getConvert().addChildElement(risultato, "annoPeriodico", rsStorico.getString("annoPeriodico"), false);
						datiXml.getConvert().addChildElement(risultato, "natura", rsStorico.getString("natura"), false);
						datiXml.getConvert().addChildElement(risultato, "annata", rsStorico.getString("annata"), false);
						datiXml.getConvert().addChildElement(risultato, "fascicoli", rsStorico.getString("fascicoli"), false);
						datiXml.getConvert().addChildElement(risultato, "dataIniPrev", MsSql.conveDateIta(rsStorico.getTimestamp("dataIniPrev")), false);
						datiXml.getConvert().addChildElement(risultato, "dataFinPrev", MsSql.conveDateIta(rsStorico.getTimestamp("dataFinPrev")), false);
						if (rsStorico.getTimestamp("dataIniEff") != null)
							datiXml.getConvert().addChildElement(risultato, "dataIniEff", MsSql.conveDateIta(rsStorico.getTimestamp("dataIniEff")), false);
						if (rsStorico.getTimestamp("dataFinEff") != null)
							datiXml.getConvert().addChildElement(risultato, "dataFinEff", MsSql.conveDateIta(rsStorico.getTimestamp("dataFinEff")), false);
						datiXml.getConvert().addChildElement(risultato, "noteUte", rsStorico.getString("noteUte"), false);
						datiXml.getConvert().addChildElement(risultato, "noteBib", rsStorico.getString("noteBib"), false);
						if (rsStorico.getString("emailStatoRichiesta") != null)
							datiXml.getConvert().addChildElement(risultato, "emailStatoRichiesta", rsStorico.getString("emailStatoRichiesta"), "notifica", rsStorico.getString("notificaStatoRichiesta"), false, false);
						datiXml.getConvert().addChildElement(risultato, "numRinnovi", rsStorico.getString("numRinnovi"), false);
						datiXml.getConvert().addChildElement(risultato, "indirizzoIp", rsStorico.getString("indirizzoIp"), false);
						risultati.addChildElement(risultato);
			    }
				  else
				  	break;
				}

			}
			show.addChildElement(risultati);
		}
		catch (NumberFormatException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsStorico != null)
					rsStorico.close();
				if (storico != null)
					storico.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return show;
	}
}
