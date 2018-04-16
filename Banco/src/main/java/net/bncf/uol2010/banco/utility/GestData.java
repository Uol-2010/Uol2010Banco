package net.bncf.uol2010.banco.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import mx.database.MsSql;
import mx.log4j.Logger;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.ControlliServizio;
import net.bncf.uol2010.servizi.controlli.implement.ControlloFasciaOrariaPerGiorniLavorativi;

// Questa classe serve per gestire la data.

public class GestData extends GregorianCalendar
{

	/**
   * Questa variabile viene utilizzata per loggare l'applicazione
   */
	private Logger log = new Logger(GestData.class,"net.bncf.uol2010.banco.utility");

	/**
   * 
   */
	private static final long serialVersionUID = 5263367899480861576L;

	public GestData()
	{
		super();
		// add(HOUR,1);
		/*
     * 19 ottobre 2001 Randazzo Massimiliano Tolto tutta questa parte di codice
     * e aggiunto il comando "add" perche' non funzionava piu' il test sul Time
     * Zone l'orologio era in dietro di 1 ora // get the supported ids for
     * GMT-08:00 (Pacific Standard Time) //String[] ids =
     * TimeZone.getAvailableIDs(0 * 60 * 60 * 1000); String[] ids =
     * TimeZone.getDefault().getAvailableIDs(); // if no ids were returned,
     * something is wrong. get out. if (ids.length == 0) System.exit(0);
     * 
     * 
     */
		// create a Pacific Standard Time time zone
		// SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, ids[0]);
		SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, TimeZone
				.getDefault().getID());

		// set up rules for daylight savings time
		pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		pdt.setEndRule(Calendar.NOVEMBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

		setTimeZone((TimeZone) pdt);
	}

	public GestData(int year, int month, int date)
	{
		super(year, month, date);
		/*
     * add(HOUR,1); 19 ottobre 2001 Randazzo Massimiliano Tolto tutta questa
     * parte di codice e aggiunto il comando "add" perche' non funzionava piu'
     * il test sul Time Zone l'orologio era in dietro di 1 ora
     *  // get the supported ids for GMT-08:00 (Pacific Standard Time) String[]
     * ids = TimeZone.getAvailableIDs(1 * 60 * 60 * 1000); // if no ids were
     * returned, something is wrong. get out. if (ids.length == 0)
     * System.exit(0);
     * 
     * 
     */
		// create a Pacific Standard Time time zone
		// SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, ids[0]);
		SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, TimeZone
				.getDefault().getID());

		// set up rules for daylight savings time
		pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		pdt.setEndRule(Calendar.NOVEMBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

		setTimeZone((TimeZone) pdt);
	}

	public GestData(TimeZone TZ)
	{
		super(TZ);
	}

	/**
   * Estraggo la data in formato TimeStamp
   * 
   * @return
   */
	public Timestamp getTimestamp()
	{
		return new Timestamp(getTimeInMillis());
	}

	/**
   * Estrazione data in formato java.sql.Date
   */
	public java.sql.Date getSqlDate()
	{
		return new java.sql.Date(getTimeInMillis());
	}

	/**
   * Formattazione Data Italiana
   */
	public String getDateIT(long millis)
	{
		String Risulta = "";
		try
		{
			super.setTimeInMillis(millis);
			Risulta = getDateIT();
		}
		catch (Exception e)
		{
			log.error(e);
		}
		return Risulta;
	}

	/**
   * Formattazione Data Italiana
   */
	public String getDateIT()
	{
		String Risulta = "";
		try
		{
			Risulta = Integer.toString(get(Calendar.DATE));
			Risulta += "/" + Integer.toString(get(Calendar.MONTH) + 1);
			Risulta += "/" + Integer.toString(get(Calendar.YEAR));
		}
		catch (Exception e)
		{
			log.error(e);
		}
		return Risulta;
	}

	/**
   * Formattazione Data e Ora Italiana
   */
	public String getDateTimeIT(long millis)
	{
		String Risulta = "";
		try
		{
			super.setTimeInMillis(millis);
			Risulta = Integer.toString(get(Calendar.DATE));
			Risulta += "/" + Integer.toString(get(Calendar.MONTH) + 1);
			Risulta += "/" + Integer.toString(get(Calendar.YEAR));
			Risulta += " " + Integer.toString(get(Calendar.HOUR));
			Risulta += ":" + Integer.toString(get(Calendar.MINUTE));
		}
		catch (Exception e)
		{
			log.error(e);
		}
		return Risulta;
	}

	/**
   * Formattazione Data e Ora Italiana
   */
	public String getDateTimeIT()
	{
		String Risulta = "";
		try
		{
			Risulta = Integer.toString(get(Calendar.DATE));
			Risulta += "/" + Integer.toString(get(Calendar.MONTH) + 1);
			Risulta += "/" + Integer.toString(get(Calendar.YEAR));
			Risulta += " " + Integer.toString(get(Calendar.HOUR));
			Risulta += ":" + Integer.toString(get(Calendar.MINUTE));
		}
		catch (Exception e)
		{
			log.error(e);
		}
		return Risulta;
	}

	/**
   * Agginge i giorni alla data controllando la disponibilit� del servizio
   * 
   * @param field Tipo di periodo da aggiornare
   * @param amount Numero giorni /ore da aggiungere
   * @param conn Pool di connessione con il database
   * @param cod_tipo_serv codice tipo Servizio
   * @param cod_bib Codice della biblioteca
   * @throws SQLException
   */
	public void add(int amount, 
			String cod_tipo_serv) throws SQLException
	{
		int conta = 0;
		ResultSet rsControlliServizio = null;
		ControlliServizio controlliServizio = null;
		ControlloFasciaOrariaPerGiorniLavorativi controllo16 = null;

		try
		{
			controlliServizio = new ControlliServizio(Configuration.poolUol2010);
			controlliServizio.setCampoValue("idServizi", cod_tipo_serv);
			controlliServizio.setCampoValue("idControllo", "16");
			rsControlliServizio = controlliServizio.startSelect();
			conta = controlliServizio.getRecTot();

			log.debug("Data Att.: "+MsSql.conveDateIta(this));
			if (conta == 0)
			{
				log.debug("field: "+Calendar.DAY_OF_MONTH);
				log.debug("amount: "+amount);
				add(Calendar.DAY_OF_MONTH, amount);
			}
			else
			{
				controllo16 = new ControlloFasciaOrariaPerGiorniLavorativi();
				this.setTimeInMillis(controllo16.checkDataLav(amount, cod_tipo_serv, this, Configuration.poolUol2010).getTimeInMillis());
//				addCont16(field, amount);
			}
			log.debug("Data New: "+MsSql.conveDateIta(this));
		}
		catch (Exception Exc)
		{
			log.error(Exc);
		}
		finally
		{
			if (rsControlliServizio != null)
				rsControlliServizio.close();
			if (controlliServizio != null)
				controlliServizio.stopSelect();

		}
	}
}
