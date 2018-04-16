/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.IterServizio;
import net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.CodiceAttivita;
import net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.GestioneChiamate;
import net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.GestioneDeposito;
import net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.StatoIter;
import net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.StatoMovimento;
import net.bncf.uol2010.database.viewer.servizi.ViewIterServizio;
import net.bncf.uol2010.database.viewer.servizi.ViewListaAttivita;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Fruibilita
 * 
 * @author Massimiliano Randazzo
 *
 */
public class IterServ
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(IterServ.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");
	
	public static void readIterServizio(String idServizio, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		ViewIterServizio table = null;
		ResultSet rsTable = null;
		MessageElement iterServizi = null;
		MessageElement iterServizio = null;

		try
		{
			iterServizi = new MessageElement();
			iterServizi.setName("iterServizi");
			table = new ViewIterServizio(Configuration.poolUol2010);
			table.setCampoValue("idServizi", idServizio);
			table.getCampo("progIter").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
			{

				iterServizio = new MessageElement();
				iterServizio.setName("iterServizio");

				datiXml.getConvert().setAttribute(iterServizio, "progIter", rsTable.getInt("progIter"), true);
				datiXml.getConvert().addChildElement(iterServizio, "statoMovimento", rsTable.getString("descStatoMovimenti"), "id", rsTable.getString("idStatoMovimenti"), true, true);
				datiXml.getConvert().addChildElement(iterServizio, "statoIter", rsTable.getString("descStatoIter"), "id", rsTable.getString("idStatoIter"), true, true);
				datiXml.getConvert().addChildElement(iterServizio, "attivita", rsTable.getString("descAttivita"), "id", rsTable.getString("idAttivita"), true, true);
				datiXml.getConvert().addChildElement(iterServizio, "stampaMovimento", rsTable.getString("stampaMovimento"), true);
				datiXml.getConvert().addChildElement(iterServizio, "rinnovo", rsTable.getString("rinnovo"), true);
				readIterServizioLegami(iterServizio, idServizio, rsTable.getInt("progIter"), datiXml);
				iterServizi.addChildElement(iterServizio);
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
			finally
			{
				try
				{
					listaIterServizio(iterServizi, idServizio, datiXml);
					edit.addChild(iterServizi);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}

	private static void readIterServizioLegami(MessageElement iterServizio, String idServizio, int progIterPar,DatiXml datiXml) throws ServletException
	{
		MessageElement legami = null;
		MessageElement myIterServizio = null;
		ViewListaAttivita table = null;
		ResultSet rs = null;
		
		try
		{
			legami = new MessageElement();
			legami.setName("legami");

			table = new ViewListaAttivita(Configuration.poolUol2010);
			table.setCampoValue("idServizi", idServizio);
			table.setCampoValue("progIterPar", progIterPar);
			table.getCampo("progIterArr").setOrderBy(Column.ORDERBY_CRES, 1);
			rs = table.startSelect();
			while(rs.next())
			{
				myIterServizio = new MessageElement();
				myIterServizio.setName("iterServizio");
				datiXml.getConvert().setAttribute(myIterServizio, "progIterPar", rs.getString("progIterPar"), true);
				datiXml.getConvert().setAttribute(myIterServizio, "progIterArr", rs.getString("progIterArr"), true);
				datiXml.getConvert().addChildElement(myIterServizio, "attivita", rs.getString("descrizioneAttivita"), "id", rs.getString("idAttivita"), true,true);
				datiXml.getConvert().addChildElement(myIterServizio, "gestioneChiamate", rs.getString("descrizioneGestioneChiamate"), "id", rs.getString("idGestioneChiamate"), true,true);
				datiXml.getConvert().addChildElement(myIterServizio, "gestioneDeposito", rs.getString("descrizioneGestioneDeposito"), "id", rs.getString("idGestioneDeposito"), true,true);
				datiXml.getConvert().addChildElement(myIterServizio, "tipoServizio", rs.getString("tipoServizio"), false);
				legami.addChildElement(myIterServizio);
			}
			iterServizio.addChildElement(legami);
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
				if (rs != null)
					rs.close();
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

	private static void listaIterServizio(MessageElement listaFruibilita, String idServizio, DatiXml datiXml) throws ServletException
	{
		MessageElement elenco = null;
		ViewIterServizio table = null;
		ResultSet rsTable = null;

		try
		{
			elenco = new MessageElement();
			elenco.setName("elenco");

			listaStatoMovimento(elenco, datiXml);
			listaStatoIter(elenco, datiXml);
			listaAttivita(elenco, datiXml);
			listaGestioneChiamate(elenco, datiXml);
			listaGestioneDeposito(elenco, datiXml);

			table = new ViewIterServizio(Configuration.poolUol2010);
			table.setCampoValue("idServizi", idServizio);
			table.getCampo("descAttivita").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
			{
				datiXml.getConvert().addChildElement(elenco, 
						"iterServizio", 
						rsTable.getString("descAttivita"),
						"id",
						rsTable.getString("progIter"),
						true, 
						true);
			}
			
			listaFruibilita.addChildElement(elenco);
		}
		catch (SOAPException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
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

	private static void listaGestioneDeposito(MessageElement elenco, DatiXml datiXml) throws ServletException
	{
		GestioneDeposito table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new GestioneDeposito(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
				datiXml.getConvert().addChildElement(elenco, "gestioneDeposito", rsTable.getString("descrizione"), "id", rsTable.getString("idGestioneDeposito"), true,true);
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

	private static void listaGestioneChiamate(MessageElement elenco, DatiXml datiXml) throws ServletException
	{
		GestioneChiamate table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new GestioneChiamate(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
				datiXml.getConvert().addChildElement(elenco, "gestioneChiamate", rsTable.getString("descrizione"), "id", rsTable.getString("idGestioneChiamate"), true,true);
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

	private static void listaStatoMovimento(MessageElement elenco, DatiXml datiXml) throws ServletException
	{
		StatoMovimento table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new StatoMovimento(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
				datiXml.getConvert().addChildElement(elenco, "statoMovimento", rsTable.getString("descrizione"), "id", rsTable.getString("idStatoMovimenti"), true,true);
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

	private static void listaAttivita(MessageElement elenco, DatiXml datiXml) throws ServletException
	{
		CodiceAttivita table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new CodiceAttivita(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
				datiXml.getConvert().addChildElement(elenco, "attivita", rsTable.getString("descrizione"), "id", rsTable.getString("idAttivita"), true,true);
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

	private static void listaStatoIter(MessageElement elenco, DatiXml datiXml) throws ServletException
	{
		StatoIter table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new StatoIter(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
				datiXml.getConvert().addChildElement(elenco, "statoIter", rsTable.getString("descrizione"), "id", rsTable.getString("idStatoIter"), true,true);
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
		IterServizio table = null;
		
		try
		{
			table = new IterServizio(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("progIter", request.getParameter("progIter"));
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
		IterServizio table = null;
		
		try
		{
			table = new IterServizio(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("progIter", request.getParameter("progIter"));
			
			table.setCampoValue("idStatoMovimenti", request.getParameter("idStatoMovimento"));
			table.setCampoValue("idStatoIter", request.getParameter("idStatoIter"));
			table.setCampoValue("idAttivita", request.getParameter("idAttivita"));
			table.setCampoValue("stampaMovimento", request.getParameter("stampaMovimento"));
			table.setCampoValue("rinnovo", request.getParameter("rinnovo"));
			if (request.getParameter("progIter").equals("0"))
				table.insert();
			else
				table.update();
				
			datiXml.getConvert().addChildElement(element, "progIter", table.get("progIter"), true);
			datiXml.getConvert().addChildElement(element, "statoMovimenti", request.getParameter("descStatoMovimento"), "id", request.getParameter("idStatoMovimento"), true, true);
			datiXml.getConvert().addChildElement(element, "statoIter", request.getParameter("descStatoIter"), "id", request.getParameter("idStatoIter"), true, true);
			datiXml.getConvert().addChildElement(element, "attivita", request.getParameter("descAttivita"), "id", request.getParameter("idAttivita"), true, true);
			datiXml.getConvert().addChildElement(element, "stampaMovimento", request.getParameter("stampaMovimento"), true);
			datiXml.getConvert().addChildElement(element, "rinnovo", request.getParameter("rinnovo"), true);
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
			if (table != null)
				table.stopSelect();
			datiXml.addElement(element);
		}
	}

}
