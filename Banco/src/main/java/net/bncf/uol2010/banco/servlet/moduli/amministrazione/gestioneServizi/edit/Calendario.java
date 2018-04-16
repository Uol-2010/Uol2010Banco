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
import mx.database.MsSqlPool;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.calendario.CalendarioEccezioni;
import net.bncf.uol2010.database.table.servizi.calendario.CalendarioSettimanale;
import net.bncf.uol2010.database.table.servizi.calendario.CalendarioSospensioni;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Calendari
 * 
 * @author MRandazzo
 *
 */
public class Calendario
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Calendario.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");

	public static void readCalendarioSettimanale(String idServizio, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		String[][] giorniSet = new String[7][2];
		CalendarioSettimanale calendarioSettimanale = null;
		ResultSet rsCalendarioSettimanale = null;
		MessageElement calendariSettimanali = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			for (int x=0; x<7; x++)
			{
				giorniSet[x][0]="00:00";
				giorniSet[x][1]="00:00";
			}

			calendariSettimanali = new MessageElement();
			calendariSettimanali.setName("calendariSettimanali");
			calendarioSettimanale = new CalendarioSettimanale(Configuration.poolUol2010);
			calendarioSettimanale.setCampoValue("idServizi", idServizio);
			calendarioSettimanale.getCampo("giornoSettimanale").setOrderBy(Column.ORDERBY_CRES, 1);
			rsCalendarioSettimanale = calendarioSettimanale.startSelect();
			while(rsCalendarioSettimanale.next())
			{
				giorniSet[rsCalendarioSettimanale.getInt("giornoSettimanale")-1][0]=rsCalendarioSettimanale.getString("apertura");
				giorniSet[rsCalendarioSettimanale.getInt("giornoSettimanale")-1][1]=rsCalendarioSettimanale.getString("chiusura");
			}

			for (int x=0; x<7; x++)
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("apertura");
				valueAttr.add(giorniSet[x][0]);
				obbAttr.add("true");

				keyAttr.add("chiusura");
				valueAttr.add(giorniSet[x][1]);
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(calendariSettimanali, 
						"giornoSettimanale", 
						(x+1)+"",
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
				if (rsCalendarioSettimanale != null)
					rsCalendarioSettimanale.close();
				if (calendarioSettimanale != null)
					calendarioSettimanale.stopSelect();
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
					listaCalendario(calendariSettimanali, datiXml);
					edit.addChild(calendariSettimanali);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}

	private static void listaCalendario(MessageElement controlli, DatiXml datiXml) throws ServletException
	{
		MessageElement elenco = null;
		
		try
		{
			elenco = new MessageElement();
			elenco.setName("elenco");

			datiXml.getConvert().addChildElement(elenco, "giorno", "Domenica", "id", "1", true, true);
			datiXml.getConvert().addChildElement(elenco, "giorno", "Luned\u00EC", "id", "2", true, true);
			datiXml.getConvert().addChildElement(elenco, "giorno", "Marted\u00EC", "id", "3", true, true);
			datiXml.getConvert().addChildElement(elenco, "giorno", "Mercoled\u00EC", "id", "4", true, true);
			datiXml.getConvert().addChildElement(elenco, "giorno", "Gioved\u00EC", "id", "5", true, true);
			datiXml.getConvert().addChildElement(elenco, "giorno", "Venerd\u00EC", "id", "6", true, true);
			datiXml.getConvert().addChildElement(elenco, "giorno", "Sabato", "id", "7", true, true);

			for (int x=0; x<24; x++)
				datiXml.getConvert().addChildElement(elenco, "ore", (x<10?"0":"")+x, true);

			for (int x=0; x<60; x+=5)
				datiXml.getConvert().addChildElement(elenco, "minuti", (x<10?"0":"")+x, true);

			for (int x=1; x<32; x++)
				datiXml.getConvert().addChildElement(elenco, "giornoMese", (x<10?"0":"")+x, true);

			datiXml.getConvert().addChildElement(elenco, "mese", "Gennaio",   "id", "01", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Febbraio",  "id", "02", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Marzo",     "id", "03", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Aprile",    "id", "04", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Maggio",    "id", "05", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Giugno",    "id", "06", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Luglio",    "id", "07", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Agosto",    "id", "08", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Settembre", "id", "09", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Ottobre",   "id", "10", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Novembre",  "id", "11", true, true);
			datiXml.getConvert().addChildElement(elenco, "mese", "Dicembre",  "id", "12", true, true);

			controlli.addChildElement(elenco);
		}
		catch (SOAPException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		finally
		{
		}
	}

	public static void aggSet(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		String idServizi = null;
		String apertura = null;
		String chiusura = null;
		
		try
		{
			//http://banco.uol2010.randazzo.local/Banco/servlet/Banco?modulo=GestServ&azione=aggCalSet&idServizi=MU&Apertura_1=00:00&Chiusura_1=00:00&Apertura_2=08:15&Chiusura_2=14:30&Apertura_3=13:15&Chiusura_3=19:00&Apertura_4=08:15&Chiusura_4=14:00&Apertura_5=08:15&Chiusura_5=14:45&Apertura_6=08:15&Chiusura_6=14:00&Apertura_7=08:15&Chiusura_7=13:30
			idServizi = request.getParameter("idServizi");
			for (int x=1; x<8; x++)
			{
				apertura = "00:00";
				chiusura = "00:00";
				if (request.getParameter("Apertura_"+x) != null)
					apertura = request.getParameter("Apertura_"+x);
				if (request.getParameter("Chiusura_"+x) != null)
					chiusura = request.getParameter("Chiusura_"+x);
				aggSet(idServizi, x, apertura, chiusura);
			}
			datiXml.getConvert().addChildElement(element, "idServizi", request.getParameter("idServizi"), true);
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
			datiXml.addElement(element);
		}
	}

	private static void aggSet(String idServizi, int giornoSettimanale, String apertura, String chiusura) throws SQLException, MsSqlException
	{
		CalendarioSettimanale calendarioSettimanale = null;
		ResultSet rsCalendarioSettimanale = null;
		
		try
		{
			calendarioSettimanale = new CalendarioSettimanale(Configuration.poolUol2010);
			calendarioSettimanale.setCampoValue("idServizi", idServizi);
			calendarioSettimanale.setCampoValue("giornoSettimanale", giornoSettimanale);
			rsCalendarioSettimanale = calendarioSettimanale.startSelect();
			if (rsCalendarioSettimanale.next())
			{
				if (apertura.equals("00:00") && chiusura.equals("00:00"))
					calendarioSettimanale.delete();
				else
				{
					calendarioSettimanale.setCampoValue("apertura", apertura);
					calendarioSettimanale.setCampoValue("chiusura", chiusura);
					calendarioSettimanale.update();
				}
			}
			else
			{
				if (!(apertura.equals("00:00") && chiusura.equals("00:00")))
				{
					calendarioSettimanale.setCampoValue("apertura", apertura);
					calendarioSettimanale.setCampoValue("chiusura", chiusura);
					calendarioSettimanale.insert();
				}
			}
		}
		catch (SQLException e)
		{
			throw e;
		}
		catch (MsSqlException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (rsCalendarioSettimanale != null)
					rsCalendarioSettimanale.close();
				if (calendarioSettimanale != null)
					calendarioSettimanale.stopSelect();
			}
			catch (SQLException e)
			{
				throw e;
			}
		}
	}

	public static void writeSet(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		CalendarioSettimanale calendarioSettimanale = null;
		ResultSet rsCalendarioSettimanale = null;
		
		try
		{
			calendarioSettimanale = new CalendarioSettimanale(Configuration.poolUol2010);
			calendarioSettimanale.setCampoValue("idServizi", request.getParameter("idServizi"));
			calendarioSettimanale.setCampoValue("giornoSettimanale", request.getParameter("giornoSettimanale"));
			calendarioSettimanale.setCampoValue("apertura", request.getParameter("apertura"));
			calendarioSettimanale.setCampoValue("chiusura", request.getParameter("chiusura"));
			
			rsCalendarioSettimanale = calendarioSettimanale.startSelect();
			if (!rsCalendarioSettimanale.next())
			{
				calendarioSettimanale.insert();
				datiXml.getConvert().addChildElement(element, "giornoSettimanale", request.getParameter("giornoSettimanale"), true);
				datiXml.getConvert().addChildElement(element, "descGiornoSettimanale", request.getParameter("descGiornoSettimanale"), true);
				datiXml.getConvert().addChildElement(element, "apertura", request.getParameter("apertura"), true);
				datiXml.getConvert().addChildElement(element, "chiusura", request.getParameter("chiusura"), true);
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
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
				if (rsCalendarioSettimanale != null)
					rsCalendarioSettimanale.close();
				if (calendarioSettimanale != null)
					calendarioSettimanale.stopSelect();
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

	public static void deleteSet(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		CalendarioSettimanale table = null;
		
		try
		{
			table = new CalendarioSettimanale(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("giornoSettimanale", request.getParameter("giornoSettimanale"));
			table.setCampoValue("apertura", request.getParameter("apertura"));
			table.setCampoValue("chiusura", request.getParameter("chiusura"));
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
	 * Questo metodo viene utilizzato per leggere la lista delle eccezioni del Calendario
	 * 
	 * @param idServizio
	 * @param edit
	 * @param datiXml
	 * @throws ServletException
	 */
	public static void readCalendarioEccezioni(String idServizio, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		CalendarioEccezioni table = null;
		ResultSet rsTable = null;
		MessageElement calendariEccezioni = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			calendariEccezioni = new MessageElement();
			calendariEccezioni.setName("calendariEccezioni");
			table = new CalendarioEccezioni(Configuration.poolUol2010);
			table.setCampoValue("idServizi", idServizio);
			table.getCampo("giorno").setOrderBy(Column.ORDERBY_DESC, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
			{

				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("apertura");
				valueAttr.add(rsTable.getString("orarioApertura"));
				obbAttr.add("true");

				keyAttr.add("chiusura");
				valueAttr.add(rsTable.getString("orarioChiusura"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(calendariEccezioni, 
						"giornoSettimanale", 
						MsSqlPool.conveDateIta(rsTable.getTimestamp("giorno")),
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
					listaCalendario(calendariEccezioni, datiXml);
					edit.addChild(calendariEccezioni);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}
	
	public static void writeEcc(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		CalendarioEccezioni table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new CalendarioEccezioni(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("giorno", request.getParameter("giorno"));
			table.setCampoValue("orarioApertura", request.getParameter("orarioApertura"));
			table.setCampoValue("orarioChiusura", request.getParameter("orarioChiusura"));
			
			rsTable = table.startSelect();
			if (!rsTable.next())
			{
				table.insert();
				datiXml.getConvert().addChildElement(element, "giorno", request.getParameter("giorno"), true);
				datiXml.getConvert().addChildElement(element, "orarioApertura", request.getParameter("orarioApertura"), true);
				datiXml.getConvert().addChildElement(element, "orarioChiusura", request.getParameter("orarioChiusura"), true);
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
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
				if (rsTable != null)
					rsTable.close();
				if (table != null)
					table.stopSelect();
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

	public static void deleteEcc(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		CalendarioEccezioni table = null;
		
		try
		{
			table = new CalendarioEccezioni(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("giorno", request.getParameter("giorno"));
			table.setCampoValue("orarioApertura", request.getParameter("orarioApertura"));
			table.setCampoValue("orarioChiusura", request.getParameter("orarioChiusura"));
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

	public static void readCalendarioSospensioni(String idServizio, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		CalendarioSospensioni table = null;
		ResultSet rsTable = null;
		MessageElement calendariSospensioni = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			calendariSospensioni = new MessageElement();
			calendariSospensioni.setName("calendariSospensioni");
			table = new CalendarioSospensioni(Configuration.poolUol2010);
			table.setCampoValue("idServizi", idServizio);
			table.getCampo("mese").setOrderBy(Column.ORDERBY_CRES, 1);
			table.getCampo("giornoInizio").setOrderBy(Column.ORDERBY_CRES, 2);
			table.getCampo("giornoFine").setOrderBy(Column.ORDERBY_CRES, 3);
			rsTable = table.startSelect();
			while(rsTable.next())
			{

				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("giornoInizio");
				if (rsTable.getInt("giornoInizio")<10)
					valueAttr.add("0"+rsTable.getString("giornoInizio"));
				else
					valueAttr.add(rsTable.getString("giornoInizio"));
				obbAttr.add("true");

				keyAttr.add("giornoFine");
				if (rsTable.getInt("giornoFine")<10)
					valueAttr.add("0"+rsTable.getString("giornoFine"));
				else
					valueAttr.add(rsTable.getString("giornoFine"));
				obbAttr.add("true");

				keyAttr.add("mese");
				if (rsTable.getInt("mese")<10)
					valueAttr.add("0"+rsTable.getString("mese"));
				else
					valueAttr.add(rsTable.getString("mese"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(calendariSospensioni, 
						"calendarioSospensione", 
						rsTable.getString("descrizione"),
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
					listaCalendario(calendariSospensioni, datiXml);
					edit.addChild(calendariSospensioni);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}
	
	public static void writeSos(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		CalendarioSospensioni table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new CalendarioSospensioni(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("giornoInizio", request.getParameter("giornoInizio"));
			table.setCampoValue("giornoFine", request.getParameter("giornoFine"));
			table.setCampoValue("mese", request.getParameter("mese"));
			
			rsTable = table.startSelect();
			table.setCampoValue("descrizione", request.getParameter("descrizione"));
			if (!rsTable.next())
			{
				table.insert();
				datiXml.getConvert().addChildElement(element, "giornoInizio", request.getParameter("giornoInizio"), true);
				datiXml.getConvert().addChildElement(element, "giornoFine", request.getParameter("giornoFine"), true);
				datiXml.getConvert().addChildElement(element, "mese", request.getParameter("mese"), true);
				datiXml.getConvert().addChildElement(element, "descMese", request.getParameter("descMese"), true);
				datiXml.getConvert().addChildElement(element, "descrizione", request.getParameter("descrizione"), true);
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
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
				if (rsTable != null)
					rsTable.close();
				if (table != null)
					table.stopSelect();
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

	public static void deleteSos(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		CalendarioSospensioni table = null;
		
		try
		{
			table = new CalendarioSospensioni(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("giornoInizio", request.getParameter("giornoInizio"));
			table.setCampoValue("giornoFine", request.getParameter("giornoFine"));
			table.setCampoValue("mese", request.getParameter("mese"));
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
}
