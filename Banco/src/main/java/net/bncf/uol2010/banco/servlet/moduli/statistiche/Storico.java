/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.statistiche;
        
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlPool;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.banco.servlet.moduli.RicercaMaterialeStoricizzato;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.DOMException;

/**
 * Questa classe viene utilizzata per la gestione delle Statistiche dei Movimenti
 * 
 * @author MRandazzo
 *
 */
public class Storico extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(Storico.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * 
	 */
	public Storico()
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
		this.fileXsl = "StatisticheStorico.xsl";
		datiXml.setTitle("Statistiche Storico");
		datiXml.addStyleSheet("../style/StatisticheStorico.css");

//		datiXml.addJavaScript("../js/mx/testJS.js");
//		datiXml.addJavaScript("../js/mx/div.js");
//		datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
	  datiXml.addJavaScript("../js/flot/excanvas.min.js#IE");
	  datiXml.addJavaScript("http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js");
	  datiXml.addJavaScript("../js/flot/jquery.flot.js");
	  datiXml.addJavaScript("../js/flot/jquery.flot.orderBars.js");
		datiXml.addJavaScript("../js/StatisticheStorico/StatisticheStorico.js");

		element = new MessageElement();
		element.setName("statisticheStorico");
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
			RicercaMaterialeStoricizzato.genListaAnni(show, request, datiXml);
			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || (id != null && !id.trim().equals("")))
				getStatMovimenti(show);
			element.addChildElement(show);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
	}

	/**
	 * Questo metodo viene utilizzato per calcolare le informazoni dello Stato dei Modimenti
	 * s
	 * @param show
	 */
	private void getStatMovimenti(MessageElement show)
	{
		List<datiStatMovimenti> dati = null;
		MessageElement statisticheMovimenti = null;
		
		try
		{
			dati = getStatMovimentiPerServizio();
			getStatMovimentiPerServizio(dati);
			getStatMovimentiPerServizioPerTipoRichiesta(dati, "false");
			getStatMovimentiPerServizioPerTipoRichiesta(dati, false);
			getStatMovimentiPerServizioPerTipoRichiesta(dati, "true");
			getStatMovimentiPerServizioPerTipoRichiesta(dati, true);

			statisticheMovimenti = new MessageElement();
			statisticheMovimenti.setName("statisticheMovimenti");
			for (int x=0; x<dati.size(); x++)
				statisticheMovimenti.addChildElement(dati.get(x).write(datiXml));
			show.addChildElement(statisticheMovimenti);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
	}

	/**
	 * Questo metodo viene utilizzato per catturare la lista dei Movimenti per Servizio per Tipo Richiesta
	 * 
	 * @param dati
	 * @param richiestaInterna
	 */
	private void getStatMovimentiPerServizioPerTipoRichiesta(List<datiStatMovimenti> dati, String richiestaInterna)
	{
		MsSqlPool msp = null;
		String sql = "";
		ResultSet rsStatistiche = null;
		int totale = 0;

		try
		{
			sql = "select id_servizi, servizi_descrizione, count(*) as Conta "+
			        "from storico_"+request.getParameter("Ric_anno")+" "+
			       "where DATA_INS between '"+request.getParameter("Ric_dataStart")+" 00:00:00' and '"+request.getParameter("Ric_dataStop")+" 23:59:59' and ";
			sql += "richiestainterna = '"+richiestaInterna+"' "+
			    "group by id_servizi, servizi_descrizione "+
			    "order by servizi_descrizione";

			msp = Configuration.poolStorico.getConn();

			rsStatistiche = msp.StartSelect(sql);
			while(rsStatistiche.next())
			{
				for (int x=0; x<dati.size(); x++)
				{
					if (dati.get(x).equals(rsStatistiche.getString("id_Servizi")))
					{
						if (richiestaInterna.equals("false"))
							dati.get(x).setNumMovimentiEsterni(rsStatistiche.getInt("conta"));
						else
							dati.get(x).setNumMovimentiInterni(rsStatistiche.getInt("conta"));
						totale += rsStatistiche.getInt("conta");
					}
				}
			}
			
			for (int x=0; x<dati.size(); x++)
			{
				if (dati.get(x).equals(""))
				{
					if (richiestaInterna.equals("false"))
						dati.get(x).setNumMovimentiEsterni(totale);
					else
						dati.get(x).setNumMovimentiInterni(totale);
				}
			}
		}
		catch (SQLException e)
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
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per catturare la lista dei Movimenti per Servizio per Tipo Richiesta
	 * 
	 * @param dati
	 * @param richiestaInterna
	 */
	private void getStatMovimentiPerServizioPerTipoRichiesta(List<datiStatMovimenti> dati, boolean richiestaInterna)
	{
		MsSqlPool msp = null;
		String sql = "";
		ResultSet rsStatistiche = null;
		int totale = 0;

		try
		{
			sql = "select richieste.id_servizi, servizi.descrizione, count(*) as Conta "+ 
  		        "from richieste, "+
		               "servizi "+
		         "where richieste.id_servizi=servizi.id_servizi AND (";
			if (!richiestaInterna)
				sql += "NOT ";
			sql += "(richieste.indirizzoip like '192.168.%' OR "+
		          "richieste.indirizzoip like '193.206.206.%' OR "+
		          "richieste.indirizzoip like '193.206.207.%' OR "+
		          "richieste.indirizzoip like '127.0.0.1' OR "+
		          "richieste.indirizzoip is null OR "+
		          "richieste.indirizzoip = '')) AND "+
			        "richieste.data_ins between '"+request.getParameter("Ric_dataStart")+" 00:00:00' and " +
			        		                       "'"+request.getParameter("Ric_dataStop")+" 23:59:59' ";
			sql += "group by richieste.id_servizi, servizi.descrizione "+
			    "order by servizi.descrizione";

			msp = Configuration.poolUol2010.getConn();

			rsStatistiche = msp.StartSelect(sql);
			while(rsStatistiche.next())
			{
				for (int x=0; x<dati.size(); x++)
				{
					if (dati.get(x).equals(rsStatistiche.getString("id_Servizi")))
					{
						if (!richiestaInterna)
							dati.get(x).addNumMovimentiEsterni(rsStatistiche.getInt("conta"));
						else
							dati.get(x).addNumMovimentiInterni(rsStatistiche.getInt("conta"));
						totale += rsStatistiche.getInt("conta");
					}
				}
			}
			
			for (int x=0; x<dati.size(); x++)
			{
				if (dati.get(x).equals(""))
				{
					if (!richiestaInterna)
						dati.get(x).addNumMovimentiEsterni(totale);
					else
						dati.get(x).addNumMovimentiInterni(totale);
				}
			}
		}
		catch (SQLException e)
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
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}
	
	/**
	 * Questo metodo viene utilizzato per catturare la lista dei Movimenti per Servizio
	 * 
	 * @return
	 */
	private List<datiStatMovimenti> getStatMovimentiPerServizio()
	{
		List<datiStatMovimenti> dati = null;
		MsSqlPool msp = null;
		String sql = "";
		ResultSet rsStatistiche = null;
		int totale = 0;

		try
		{
			sql = "select id_servizi, servizi_descrizione, count(*) "+
			        "from storico_"+request.getParameter("Ric_anno")+" "+
			       "where DATA_INS between '"+request.getParameter("Ric_dataStart")+" 00:00:00' and '"+request.getParameter("Ric_dataStop")+" 23:59:59' "+
			    "group by id_servizi, servizi_descrizione "+
			    "order by servizi_descrizione";
			dati = new ArrayList<datiStatMovimenti>();

			msp = Configuration.poolStorico.getConn();

			rsStatistiche = msp.StartSelect(sql);
			while(rsStatistiche.next())
			{
				dati.add(new datiStatMovimenti(rsStatistiche.getString("id_Servizi"), rsStatistiche.getString("servizi_descrizione"), rsStatistiche.getInt(3)));
				totale += rsStatistiche.getInt(3);
			}
			dati.add(new datiStatMovimenti("", "Totale", totale));
		}
		catch (SQLException e)
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
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return dati;
	}
	
	/**
	 * Questo metodo viene utilizzato per catturare la lista dei Movimenti per Servizio
	 * 
	 * @return
	 */
	private void getStatMovimentiPerServizio(List<datiStatMovimenti> dati)
	{
		MsSqlPool msp = null;
		String sql = "";
		ResultSet rsStatistiche = null;
		int totale = 0;
		boolean trovato = false;

		try
		{
			sql = "select richieste.id_servizi, servizi.descrizione, count(*) " +
					    "from richieste, " +
					         "servizi " +
			       "where richieste.id_servizi=servizi.id_servizi and " +
			             "richieste.data_ins between '"+request.getParameter("Ric_dataStart")+" 00:00:00' and " +
			             		                        "'"+request.getParameter("Ric_dataStop")+" 23:59:59' "+
			    "group by richieste.id_servizi, servizi.descrizione "+
			    "order by servizi.descrizione";
//			dati = new ArrayList<datiStatMovimenti>();

			msp = Configuration.poolUol2010.getConn();

			rsStatistiche = msp.StartSelect(sql);
			while(rsStatistiche.next())
			{
				trovato = false;
				for (int x=0; x<dati.size(); x++)
				{
					if (dati.get(x).equals(rsStatistiche.getString("id_Servizi")))
					{
						dati.get(x).addNumMovimenti(rsStatistiche.getInt(3));
						trovato = true;
					}
				}
				if (!trovato)
					dati.add(new datiStatMovimenti(rsStatistiche.getString("id_Servizi"), rsStatistiche.getString("descrizione"), rsStatistiche.getInt(3)));
				totale += rsStatistiche.getInt(3);
			}
			for (int x=0; x<dati.size(); x++)
			{
				if (dati.get(x).equals(""))
					dati.get(x).addNumMovimenti(totale);
			}
		}
		catch (SQLException e)
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

/**
 * Questa classe viene utilizzata per indicare i dati delle Statistiche dei Movimenti
 * 
 * @author Massimiliano Randazzo
 *
 */
class datiStatMovimenti
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(datiStatMovimenti.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Questa variabile viene utilizzata per indicare l'identificativo del Servizio
	 */
	private String idServizio = null;

	/**
	 * Questa variabile viene utilizzata per indicare la descrizione del Servizio
	 */
	private String descServizio = null;

	/**
	 * Questa variabile viene utilizzata per indicare il numero totali dei Movimenti
	 */
	private int numMovimenti = 0;

	/**
	 * Questa variabile viene utilizzata per indicare il numero di movimenti Interni
	 */
	private int numMovimentiInterni = 0;

	/**
	 * Questa variabile viene utilizzata per indicare il numero di movimenti Esterni
	 */
	private int numMovimentiEsterni = 0;

	/**
	 * Costruttore
	 * 
	 * @param idServizio
	 * @param descServizio
	 * @param numMovimenti
	 */
	public datiStatMovimenti (String idServizio, String descServizio, int numMovimenti)
	{
		this.idServizio = idServizio;
		this.descServizio = descServizio;
		this.numMovimenti = numMovimenti;
	}

	/**
	 * Questo metodo viene utilizzato per testare l'uguaglianza delle informazioni
	 *  
	 * @param idServizio
	 * @return
	 */
	public boolean equals(String idServizio)
	{
		return this.idServizio.equals(idServizio);
	}

	/**
	 * Questo metodo viene utilizzato per indicare i Movimenti Interni
	 * 
	 * @param numMovimentiInterni the numMovimentiInterni to set
	 */
	public void setNumMovimentiInterni(int numMovimentiInterni)
	{
		this.numMovimentiInterni = numMovimentiInterni;
	}

	/**
	 * Questo metodo viene utilizzato per indicare i Movimenti Esterni
	 * 
	 * @param numMovimentiEsterni the numMovimentiEsterni to set
	 */
	public void setNumMovimentiEsterni(int numMovimentiEsterni)
	{
		this.numMovimentiEsterni = numMovimentiEsterni;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere dei record hai modimenti presenti
	 * @param numMovimenti
	 */
	public void addNumMovimenti(int numMovimenti)
	{
		this.numMovimenti += numMovimenti;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere dei record hai modimenti presenti
	 * @param numMovimenti
	 */
	public void addNumMovimentiInterni(int numMovimentiInterni)
	{
		this.numMovimentiInterni += numMovimentiInterni;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere dei record hai modimenti presenti
	 * @param numMovimenti
	 */
	public void addNumMovimentiEsterni(int numMovimentiEsterni)
	{
		this.numMovimentiEsterni += numMovimentiEsterni;
	}
	
	public MessageElement write(DatiXml datiXml)
	{
		MessageElement statisticaMovimento = null;

		try
		{
			statisticaMovimento = new MessageElement();
			statisticaMovimento.setName("statisticaMovimento");
			statisticaMovimento.setAttribute("id", idServizio);

			datiXml.getConvert().addChildElement(statisticaMovimento, "descrizione", descServizio, true);
			datiXml.getConvert().addChildElement(statisticaMovimento, "numMovimenti", numMovimenti+"", true);
			datiXml.getConvert().addChildElement(statisticaMovimento, "numMovimentiEsterni", numMovimentiEsterni+"", false);
			datiXml.getConvert().addChildElement(statisticaMovimento, "numMovimentiInterni", numMovimentiInterni+"", false);
		}
		catch (DOMException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		return statisticaMovimento;
	}
}
