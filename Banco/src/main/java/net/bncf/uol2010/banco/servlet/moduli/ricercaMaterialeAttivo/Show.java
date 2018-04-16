/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.ricercaMaterialeAttivo;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;


import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import mx.randalf.moduli.standard.xml.DatiXml;

/**
 * @author massi
 *
 */
public class Show
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = Logger.getLogger(Show.class);

	@SuppressWarnings("unchecked")
	public static MessageElement show(HttpServletRequest request, MessageElement show, DatiXml datiXml, String idRichiestaNew)
	{
		ViewRichieste viewRichieste = null;
		ResultSet rsViewRichieste = null;
		MessageElement risultati = null;
		MessageElement risultato = null;
		String idUtente = "";
		String inventario = "";
		String[] st = null;

		try
		{
			if (idRichiestaNew != null && !idRichiestaNew.trim().equals("") && !idRichiestaNew.trim().equals("-1"))
				viewRichieste = new ViewRichieste(Configuration.poolUol2010);
			else
				viewRichieste = new ViewRichieste(Configuration.poolUol2010, request.getParameterMap());
			if (idRichiestaNew != null && !idRichiestaNew.trim().equals("") && !idRichiestaNew.trim().equals("-1"))
				viewRichieste.setCampoValue("idRichieste", idRichiestaNew);
			else
			{
				if (idRichiestaNew != null && idRichiestaNew.trim().equals("-1"))
					viewRichieste.setCampoValue("idRichieste", null);
				if (request.getParameter("Ric_idRichieste") != null && 
						!request.getParameter("Ric_idRichieste").equals(""))
				{
					viewRichieste.setCampoValue("idRichieste", request.getParameter("Ric_idRichieste"));
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
						viewRichieste.setCampoValue("idUtente", idUtente);
					}
					else
					{
						if (request.getParameter("Ric_cognomeKey") != null &&
								!request.getParameter("Ric_cognomeKey").equals(""))
						{
							viewRichieste.setCampoValue("utenteCognomeKey", request.getParameter("Ric_cognomeKey"));
						}
						if (request.getParameter("Ric_nomeKey") != null &&
								!request.getParameter("Ric_nomeKey").equals(""))
						{
							viewRichieste.setCampoValue("utenteNomeKey", request.getParameter("Ric_nomeKey"));
						}
					}
					
					if (request.getParameter("Ric_dataStart") != null &&
							!request.getParameter("Ric_dataStart").equals(""))
					{
						if (request.getParameter("Ric_dataStop") != null &&
								!request.getParameter("Ric_dataStop").equals(""))
  						viewRichieste.addWhere(" AND RICHIESTE.DATA_INS BETWEEN " +
  								"'"+request.getParameter("Ric_dataStart")+" 00:00:00' AND " +
  								"'"+request.getParameter("Ric_dataStop")+" 23:59:59'");
						else
						{
							st = request.getParameter("Ric_dataStart").split("/");
  						viewRichieste.addWhere(" AND RICHIESTE.DATA_INS > " +
  								"'"+st[2]+"-"+st[1]+"-"+st[0]+" 00:00:00'");
						}
							
					}
					
					if (request.getParameter("Ric_idServizi") != null &&
							!request.getParameter("Ric_idServizi").equals(""))
						viewRichieste.setCampoValue("idServizi", request.getParameter("Ric_idServizi"));
					
					if (request.getParameter("Ric_idStatoMovimenti") != null &&
							!request.getParameter("Ric_idStatoMovimenti").equals(""))
						viewRichieste.setCampoValue("idStatoMovimenti", request.getParameter("Ric_idStatoMovimenti"));
//					else
//						viewRichieste.addWhere(" AND (NOT RICHIESTE.ID_STATOMOVIMENTI IN ('S', 'E', 'C'))");
					
					if (request.getParameter("Ric_inventario") != null &&
							!request.getParameter("Ric_inventario").equals(""))
					{
						inventario = request.getParameter("Ric_inventario").trim().toUpperCase();
						if (inventario.startsWith("CF"))
							inventario = "CF"+ConvertText.mettiZeri(inventario.substring(2), 9);
						else
							inventario = "CF"+ConvertText.mettiZeri(inventario, 9);
							
						viewRichieste.setCampoValue("inventario", inventario);
					}
					
					if (request.getParameter("Ric_collocazione") != null &&
							!request.getParameter("Ric_collocazione").equals(""))
						viewRichieste.setCampoValue("segnaturaKey", request.getParameter("Ric_collocazione").trim());
					
					if (request.getParameter("Ric_bid") != null &&
							!request.getParameter("Ric_bid").equals(""))
						viewRichieste.setCampoValue("bid", request.getParameter("Ric_bid").trim().toUpperCase());
				}
			}

			viewRichieste.setNumPagVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numPag", "10")));
			viewRichieste.setNumRecVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numRec", "10")));
			viewRichieste.getCampo("dataIns").setOrderBy(Column.ORDERBY_CRES, 1);
			viewRichieste.getCampo("statoMovimentoDescrizione").setOrderBy(Column.ORDERBY_CRES, 2);
			viewRichieste.getCampo("descrizioneServizi").setOrderBy(Column.ORDERBY_CRES, 3);
			rsViewRichieste = viewRichieste.startSelect();

			risultati = new MessageElement();
			risultati.setName("risultati");
			if (viewRichieste.getRecTot()>0)
			{
				show.addChildElement(viewRichieste.viewNavigatore(datiXml.getConvert(), datiXml.getConvertUri(), "Utility", "show"));

				while (rsViewRichieste.next())
				{
				  if (rsViewRichieste.getRow()<=viewRichieste.getRecFin())
				  {
						risultato = new MessageElement();
						risultato.setName("risultato");
						datiXml.getConvert().addChildElement(risultato, "idRichieste", rsViewRichieste.getString("idRichieste"), true);
						datiXml.getConvert().addChildElement(risultato, "numRecord", rsViewRichieste.getRow(), true);
						datiXml.getConvert().addChildElement(risultato, "utenteCognome", rsViewRichieste.getString("utenteCognome"), "id", rsViewRichieste.getString("idUtente"), false, false);
						datiXml.getConvert().addChildElement(risultato, "utenteNome", rsViewRichieste.getString("utenteNome"), false);
						datiXml.getConvert().addChildElement(risultato, "statoMovimento", rsViewRichieste.getString("statoMovimentoDescrizione"), "id", rsViewRichieste.getString("idStatoMovimenti"), false, false);
						datiXml.getConvert().addChildElement(risultato, "servizio", rsViewRichieste.getString("descrizioneServizi"), "id", rsViewRichieste.getString("idServizi"), false, false);
						datiXml.getConvert().addChildElement(risultato, "progIter", rsViewRichieste.getString("progIter"), false);
						if (rsViewRichieste.getString("idFruibilita") != null)
							datiXml.getConvert().addChildElement(risultato, "fruibilita", rsViewRichieste.getString("fruibilitaDescrizione"), "id", rsViewRichieste.getString("idFruibilita"), false, false);
						datiXml.getConvert().addChildElement(risultato, "dataIns", MsSql.conveDateTimeIta(rsViewRichieste.getTimestamp("dataIns")), false);
						datiXml.getConvert().addChildElement(risultato, "dataMod", MsSql.conveDateTimeIta(rsViewRichieste.getTimestamp("dataMod")), false);
						datiXml.getConvert().addChildElement(risultato, "bid", rsViewRichieste.getString("bid"), false);
						datiXml.getConvert().addChildElement(risultato, "autore", rsViewRichieste.getString("autore"), false);
						datiXml.getConvert().addChildElement(risultato, "titolo", rsViewRichieste.getString("titolo"), false);
						datiXml.getConvert().addChildElement(risultato, "pubblicazione", rsViewRichieste.getString("pubblicazione"), false);
						datiXml.getConvert().addChildElement(risultato, "inventario", rsViewRichieste.getString("inventario"), false);
						datiXml.getConvert().addChildElement(risultato, "precInv1", rsViewRichieste.getString("precInv1"), false);
						datiXml.getConvert().addChildElement(risultato, "precInv2", rsViewRichieste.getString("precInv2"), false);
						datiXml.getConvert().addChildElement(risultato, "segnatura", rsViewRichieste.getString("segnatura"), "key", rsViewRichieste.getString("segnaturaKey"), false, false);
						datiXml.getConvert().addChildElement(risultato, "annoPeriodico", rsViewRichieste.getString("annoPeriodico"), false);
						datiXml.getConvert().addChildElement(risultato, "natura", rsViewRichieste.getString("natura"), false);
						datiXml.getConvert().addChildElement(risultato, "annata", rsViewRichieste.getString("annata"), false);
						datiXml.getConvert().addChildElement(risultato, "fascicoli", rsViewRichieste.getString("fascicoli"), false);
						datiXml.getConvert().addChildElement(risultato, "dataIniPrev", MsSql.conveDateIta(rsViewRichieste.getTimestamp("dataIniPrev")), false);
						datiXml.getConvert().addChildElement(risultato, "dataFinPrev", MsSql.conveDateIta(rsViewRichieste.getTimestamp("dataFinPrev")), false);
						if (rsViewRichieste.getTimestamp("dataIniEff") != null)
							datiXml.getConvert().addChildElement(risultato, "dataIniEff", MsSql.conveDateIta(rsViewRichieste.getTimestamp("dataIniEff")), false);
						if (rsViewRichieste.getTimestamp("dataFinEff") != null)
							datiXml.getConvert().addChildElement(risultato, "dataFinEff", MsSql.conveDateIta(rsViewRichieste.getTimestamp("dataFinEff")), false);
						datiXml.getConvert().addChildElement(risultato, "noteUte", rsViewRichieste.getString("noteUte"), false);
						datiXml.getConvert().addChildElement(risultato, "noteBib", rsViewRichieste.getString("noteBib"), false);
						datiXml.getConvert().addChildElement(risultato, "emailStatoRichiesta", rsViewRichieste.getString("emailStatoRichiesta"), "notifica", rsViewRichieste.getString("notificaStatoRichiesta"), false, false);
						datiXml.getConvert().addChildElement(risultato, "numRinnovi", rsViewRichieste.getString("numRinnovi"), false);
						datiXml.getConvert().addChildElement(risultato, "indirizzoIp", rsViewRichieste.getString("indirizzoIp"), false);
						datiXml.getConvert().addChildElement(risultato, "stampaMovimento", rsViewRichieste.getString("stampaMovimento"), false);
						datiXml.getConvert().addChildElement(risultato, "descAttivita", rsViewRichieste.getString("descAttivita"), false);
						listaServizi(risultato, datiXml, rsViewRichieste.getString("idUtente"), rsViewRichieste.getString("idFruibilita"), rsViewRichieste.getString("idServizi"));
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
				if (rsViewRichieste != null)
					rsViewRichieste.close();
				if (viewRichieste != null)
					viewRichieste.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return show;
	}

	private static void listaServizi(MessageElement risultato, DatiXml datiXml, String idUtente, String idFruibilita, String idServizi)
	{
		ViewerListaServizi listaSer = null;
		ResultSet rs = null;

		try
		{
			if (idFruibilita != null)
			{
				listaSer = new ViewerListaServizi(Configuration.poolUol2010);
				listaSer.setCampoValue("idUtente", idUtente);
				listaSer.setCampoValue("idFruibilita", idFruibilita);
				rs = listaSer.startSelect();
				while (rs.next())
				{
					if (!rs.getString("idServizi").equals(idServizi))
						datiXml.getConvert().addChildElement(risultato, "listaServizi", rs.getString("descrizione"), "id", rs.getString("idServizi"), true, true);
				}
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (listaSer != null)
					listaSer.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}
}
