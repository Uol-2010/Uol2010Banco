/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.edit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.AutBib_AutUte;
import net.bncf.uol2010.database.table.utenti.AutorizzazioniUte;
import net.bncf.uol2010.database.viewer.amministrazione.ViewAutorizzazioneBibAutorizzazioneUtente;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Fruibilita
 * 
 * @author Massimiliano Randazzo
 *
 */
public class AutBibAutUte
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(AutBibAutUte.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");
	
	public static void read(String idAutorizzazioniBib, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		ViewAutorizzazioneBibAutorizzazioneUtente view = null;
		ResultSet rsView = null;
		MessageElement autorizzazioniUtente = null;
		List<String> lista = new ArrayList<String>();

		try
		{
			autorizzazioniUtente = new MessageElement();
			autorizzazioniUtente.setName("autorizzazioniUtente");
			view = new ViewAutorizzazioneBibAutorizzazioneUtente(Configuration.poolUol2010);
			view.setCampoValue("idAutorizzazioniBib", idAutorizzazioniBib);
			view.getCampo("descAutorizzazioniUte").setOrderBy(Column.ORDERBY_CRES, 1);
			rsView = view.startSelect();
			while(rsView.next())
			{
				lista.add(rsView.getString("idAutorizzazioniUtente"));
				datiXml.getConvert().addChildElement(autorizzazioniUtente, "autorizzazioneUtente", rsView.getString("descAutorizzazioniUte"),"id",rsView.getString("idAutorizzazioniUtente"),true, true);
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
				if (rsView != null)
					rsView.close();
				if (view != null)
					view.stopSelect();
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
					lista(autorizzazioniUtente, lista, datiXml);
					edit.addChild(autorizzazioniUtente);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}

	public static void lista(MessageElement listaFruibilita, List<String> listaFrui, DatiXml datiXml) throws ServletException
	{
		AutorizzazioniUte table = null;
		ResultSet rsTable = null;
		MessageElement elenco = null;
		
		try
		{
			table = new AutorizzazioniUte(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			elenco = new MessageElement();
			elenco.setName("elenco");
			while(rsTable.next())
			{
				if (listaFrui == null || !listaFrui.contains(rsTable.getString("idAutorizzazioniUtente")))
					datiXml.getConvert().addChildElement(elenco, "autorizzazioniUte", rsTable.getString("descrizione"), "id", rsTable.getString("idAutorizzazioniUtente"), true,true);
			}
			listaFruibilita.addChildElement(elenco);
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
				if (rsTable != null)
					rsTable.close();
				if (table != null)
					table.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}

	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		AutBib_AutUte table = null;
		
		try
		{
			table = new AutBib_AutUte(Configuration.poolUol2010);
			table.setCampoValue("idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"));
			table.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
			if (table.delete()>0)
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

	/**
	 * Questo metodo viene utilizzato per aggiornare la fruibilita del servizio
	 */
	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		AutBib_AutUte fruibilita = null;
		ResultSet rsFruibilita = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		
		try
		{
			fruibilita = new AutBib_AutUte(Configuration.poolUol2010);
			fruibilita.setCampoValue("idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"));
			fruibilita.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
			
			rsFruibilita = fruibilita.startSelect();
			if (!rsFruibilita.next())
			{
				fruibilita.insert();
				datiXml.getConvert().addChildElement(element, nodes, "descAutorizzazioniUtente", request.getParameter("descAutorizzazioniUtente"), true);
				datiXml.getConvert().addChildElement(element, nodes, "idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"), true);
			}
		}
		catch (SQLException e)
		{
			log.error(e);
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
			try
			{
				if (rsFruibilita != null)
					rsFruibilita.close();
				if (fruibilita != null)
					fruibilita.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
			finally
			{
				datiXml.addElement(element);
			}
		}
	}

}
