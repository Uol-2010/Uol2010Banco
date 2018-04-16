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
 * Questa classe viene utilizzata per la gestione delle Statistiche dei Movimenti
 * 
 * @author MRandazzo
 *
 */
public class Ingressi extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(Ingressi.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * 
	 */
	public Ingressi()
	{
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare per informazioni relative alla visualizzazione
	 * 
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "StatisticheIngressi.xsl";
		datiXml.setTitle("Statistiche Ingressi");
		datiXml.addStyleSheet("../style/StatisticheIngressi.css");

//		datiXml.addJavaScript("../js/mx/testJS.js");
//		datiXml.addJavaScript("../js/mx/div.js");
//		datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
	  datiXml.addJavaScript("../js/flot/excanvas.min.js#IE");
	  datiXml.addJavaScript("http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js");
	  datiXml.addJavaScript("../js/flot/jquery.flot.js");
	  datiXml.addJavaScript("../js/flot/jquery.flot.orderBars.js");
		datiXml.addJavaScript("../js/StatisticheIngressi/StatisticheIngressi.js");

		element = new MessageElement();
		element.setName("statisticheIngressi");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#show(java.lang.String)
	 */
	@Override
	protected void show(String id) throws ServletException, IOException
	{
		MessageElement show = null;

		try
		{
			log.debug("Statistiche Ingressi - show");
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
				datiXml.getConvert().addChildElement(show, "Ric_dataStart", request.getParameter("Ric_dataStart"), false);
			if (request.getParameter("Ric_dataStop") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStop", request.getParameter("Ric_dataStop"), false);
			genListaAnni(show);
			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || (id != null && !id.trim().equals("")))
			{
				getStatAutorizzazioniUtente(show);
				getStatAutorizzazioniUtenteOre(show);
			}
			element.addChildElement(show);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
	}

	/**
	 * Questo metodo viene utilizzato per calcolare le statistiche degli ingressi per autorizzazione Utente
	 * @param show
	 */
	private void getStatAutorizzazioniUtente(MessageElement show)
  {
  	MsSqlPool msp = null;
  	String sql = "";
  	ResultSet rsStatistiche = null;
  	MessageElement autorizzazioniUtenti = null;
  	Vector<String> keyAttr = null;
  	Vector<String> valueAtr = null;
  	Vector<String> obbAttr = null;
  	int totale = 0;

  	try
		{
			msp = Configuration.poolStorico.getConn();
			sql = "select id_autorizzazioneutente, desc_autorizzazioneUtente, count(*) "+
			        "from ingressi_"+request.getParameter("Ric_anno")+" "+
			       "where data between '"+request.getParameter("Ric_dataStart")+"' and '"+request.getParameter("Ric_dataStop")+"' "+
			    "group by id_autorizzazioneutente, desc_autorizzazioneUtente "+
			    "order by desc_autorizzazioneUtente";

			autorizzazioniUtenti = new MessageElement();
			autorizzazioniUtenti.setName("autorizzazioniUtenti");
			rsStatistiche = msp.StartSelect(sql);
			while(rsStatistiche.next())
			{
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
				datiXml.getConvert().addChildElement(autorizzazioniUtenti, "autorizzazioneUtente", rsStatistiche.getString(2), keyAttr, valueAtr, true, obbAttr);
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
			datiXml.getConvert().addChildElement(autorizzazioniUtenti, "autorizzazioneUtente", "Totale", keyAttr, valueAtr, true, obbAttr);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsStatistiche != null)
					rsStatistiche.close();
				if (msp != null)
				{
					msp.StopSelect();
					Configuration.poolStorico.releaseConn(msp);
				}
				show.addChildElement(autorizzazioniUtenti);
			}
			catch (SQLException e)
			{
				log.error(e);
			}
			catch (SOAPException e)
			{
				log.error(e);
			}
		}
  }

	/**
	 * Questo metodo viene utilizzato per calcolare le statistiche degli ingressi per autorizzazione Utente
	 * @param show
	 */
	private void getStatAutorizzazioniUtenteOre(MessageElement show)
  {
  	MsSqlPool msp = null;
  	String sql = "";
  	ResultSet rsStatistiche = null;
  	MessageElement autorizzazioniUtentiOre = null;
  	MessageElement autorizzazioneUtenteOra = null;
  	String idAutorizzazione = "";

  	try
		{
			msp = Configuration.poolStorico.getConn();
			sql = "select id_autorizzazioneutente, desc_autorizzazioneUtente, extract(hour from ora_ingresso), count(*) "+
			        "from ingressi_"+request.getParameter("Ric_anno")+" "+
			       "where data between '"+request.getParameter("Ric_dataStart")+"' and '"+request.getParameter("Ric_dataStop")+"' "+
			    "group by id_autorizzazioneutente, desc_autorizzazioneUtente, extract(hour from ora_ingresso) "+
			    "order by 2,3";

			autorizzazioniUtentiOre = new MessageElement();
			autorizzazioniUtentiOre.setName("autorizzazioniUtentiOre");
			rsStatistiche = msp.StartSelect(sql);
			while(rsStatistiche.next())
			{
				if (!idAutorizzazione.equals(rsStatistiche.getString(1)))
				{
					if (autorizzazioneUtenteOra != null)
						autorizzazioniUtentiOre.addChildElement(autorizzazioneUtenteOra);
					autorizzazioneUtenteOra = new MessageElement();
					autorizzazioneUtenteOra.setName("autorizzazioneUtenteOra");

					datiXml.getConvert().setAttribute(autorizzazioneUtenteOra, "id", rsStatistiche.getString(1), true);
					datiXml.getConvert().setAttribute(autorizzazioneUtenteOra, "desc", rsStatistiche.getString(2), true);
					idAutorizzazione = rsStatistiche.getString(1);
				}

				datiXml.getConvert().addChildElement(autorizzazioneUtenteOra, "autorizzazioneUtente", rsStatistiche.getString(3), "numero", rsStatistiche.getString(4), true, true);
			}
			if (autorizzazioneUtenteOra != null)
				autorizzazioniUtentiOre.addChildElement(autorizzazioneUtenteOra);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsStatistiche != null)
					rsStatistiche.close();
				if (msp != null)
				{
					msp.StopSelect();
					Configuration.poolStorico.releaseConn(msp);
				}
				show.addChildElement(autorizzazioniUtentiOre);
			}
			catch (SQLException e)
			{
				log.error(e);
			}
			catch (SOAPException e)
			{
				log.error(e);
			}
		}
  }
  
	/**
	 * Questo metodo viene utilizzato per calcolare la lista degli anno dello
	 * Storico
	 * @param show
	 * @throws ServletException
	 */
	private void genListaAnni(MessageElement show) throws ServletException
	{
		MsSqlPool msp = null;
		ResultSet rsTables = null;
		String querySql = null;
		MessageElement annate = null;


		try
		{
			msp = Configuration.poolStorico.getConn();

			if (msp.getTipoDb().equals(MsSqlPool.MAXDB))
				querySql = "select TABLENAME from tables where TYPE='TABLE' AND TABLENAME like 'ingressi%' order by TABLENAME desc";
			else if (msp.getTipoDb().equals(MsSqlPool.POSTGRES))
				querySql = "SELECT tablename FROM pg_tables where tablename like 'ingressi%' order by tablename desc";
			if (querySql != null &&
					!querySql.equals(""))
			{
				annate = new MessageElement();
				annate.setName("annate");
				rsTables = msp.StartSelect(querySql);
				while(rsTables.next())
				{
					if (request.getParameter("Ric_anno") != null &&
							!request.getParameter("Ric_anno").equals("") &&
							request.getParameter("Ric_anno").equals(rsTables.getString(1).toLowerCase().replace("ingressi_", "")))
						datiXml.getConvert().addChildElement(annate, "anno", rsTables.getString(1).toLowerCase().replace("ingressi_", ""), "selected", "selected", true, true);
					else
						datiXml.getConvert().addChildElement(annate, "anno", rsTables.getString(1).toLowerCase().replace("ingressi_", ""), true);
				}
				show.addChildElement(annate);
			}
			else
				throw new ServletException("Questa tipologia di Database ["+msp.getTipoDb()+"] non \u00E8 supportata per lo Storico");
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (ServletException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsTables != null)
					rsTables.close();
				if (msp != null)
				{
					msp.StopSelect();
					Configuration.poolStorico.releaseConn(msp);
				}
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
	}

}
