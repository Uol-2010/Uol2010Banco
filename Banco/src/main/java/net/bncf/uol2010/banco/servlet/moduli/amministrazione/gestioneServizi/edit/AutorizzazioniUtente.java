/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.utenti.AutUte_Servizi;
import net.bncf.uol2010.database.table.utenti.AutorizzazioniUte;
import net.bncf.uol2010.database.viewer.utenti.ViewAutUteServizi;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Autorizzazione UTente
 * 
 * @author MRandazzo
 *
 */
public class AutorizzazioniUtente
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(AutorizzazioniUtente.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");
	
	public static void readAutiorizzazioniUtente(String idServizio, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		ViewAutUteServizi viewAutUteServizi = null;
		ResultSet rsViewAutUteServizi = null;
		MessageElement autUteServizi = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			autUteServizi = new MessageElement();
			autUteServizi.setName("autUteServizi");
			viewAutUteServizi = new ViewAutUteServizi(Configuration.poolUol2010);
			viewAutUteServizi.setCampoValue("idServizi", idServizio);
			viewAutUteServizi.getCampo("descAutorizzazioneUtente").setOrderBy(Column.ORDERBY_CRES, 1);
			rsViewAutUteServizi = viewAutUteServizi.startSelect();
			while(rsViewAutUteServizi.next())
			{

				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsViewAutUteServizi.getString("idAutorizzazioniUtente"));
				obbAttr.add("true");

				keyAttr.add("rinnovoAutomatico");
				valueAttr.add(rsViewAutUteServizi.getString("rinnovoAutomatico"));
				obbAttr.add("true");

				keyAttr.add("autorizzazioneDef");
				valueAttr.add(rsViewAutUteServizi.getString("autorizzazioneDef"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(autUteServizi, 
						"autUteServizio", 
						rsViewAutUteServizi.getString("descAutorizzazioneUtente"),
						keyAttr,
						valueAttr,
						true, 
						obbAttr);
			}
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (SOAPException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		finally
		{
			try
			{
				if (rsViewAutUteServizi != null)
					rsViewAutUteServizi.close();
				if (viewAutUteServizi != null)
					viewAutUteServizi.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
			finally
			{
				try
				{
					listaAutiorizzazioniUtente(autUteServizi, datiXml);
					edit.addChild(autUteServizi);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}

	private static void listaAutiorizzazioniUtente(MessageElement autUteServizi, DatiXml datiXml) throws ServletException
	{
		AutorizzazioniUte autorizzazioniUte = null;
		ResultSet rsAutorizzazioniUte = null;
		MessageElement elenco = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			autorizzazioniUte = new AutorizzazioniUte(Configuration.poolUol2010);
			autorizzazioniUte.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsAutorizzazioniUte = autorizzazioniUte.startSelect();
			elenco = new MessageElement();
			elenco.setName("elenco");
			while(rsAutorizzazioniUte.next())
			{

				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsAutorizzazioniUte.getString("idAutorizzazioniUtente"));
				obbAttr.add("true");

				keyAttr.add("autorizzazioneDef");
				valueAttr.add(rsAutorizzazioniUte.getString("autorizzazioneDef"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(elenco, 
						"autorizzazioniUte", 
						rsAutorizzazioniUte.getString("descrizione"), 
						keyAttr, 
						valueAttr, 
						true,
						obbAttr);
			}
			autUteServizi.addChildElement(elenco);
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (SOAPException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		finally
		{
			try
			{
				if (rsAutorizzazioniUte != null)
					rsAutorizzazioniUte.close();
				if (autorizzazioniUte != null)
					autorizzazioniUte.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}

	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		AutUte_Servizi table = null;
		ResultSet rs = null;
		int ris = 0;
		
		try
		{
			table = new AutUte_Servizi(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
			rs = table.startSelect();
			table.setCampoValue("rinnovoAutomatico", request.getParameter("rinnovoAutomatico"));
			if (rs.next())
				ris = table.update();
			else
				ris = table.insert();

			if (ris >0)
			{
				datiXml.getConvert().addChildElement(element, "idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"), true);
				datiXml.getConvert().addChildElement(element, "descAutorizzazioniUtente", request.getParameter("descAutorizzazioniUtente"), true);
				datiXml.getConvert().addChildElement(element, "rinnovoAutomatico", table.get("rinnovoAutomatico"), true);
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), false);
			}
		}
		catch (MsSqlException e)
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
				if (rs != null)
					rs.close();
				if (table != null)
					table.stopSelect();
				datiXml.addElement(element);
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}

	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		AutUte_Servizi autUteServizi = null;
		
		try
		{
			autUteServizi = new AutUte_Servizi(Configuration.poolUol2010);
			autUteServizi.setCampoValue("idServizi", request.getParameter("idServizi"));
			autUteServizi.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
			if (autUteServizi.delete()>0)
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			datiXml.addElement(element);
		}
	}
	
}
