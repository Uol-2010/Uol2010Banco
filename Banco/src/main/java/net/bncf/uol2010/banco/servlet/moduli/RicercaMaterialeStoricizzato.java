/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlPool;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.exception.StdModuliException;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.banco.servlet.moduli.ricercaMaterialeStoricizzato.Show;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.Servizi;
import net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.StatoMovimento;

import org.apache.axis.message.MessageElement;

/**
 * @author massi
 *
 */
public class RicercaMaterialeStoricizzato extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(RicercaMaterialeStoricizzato.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * 
	 */
	public RicercaMaterialeStoricizzato()
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
		this.fileXsl = "RicercaMaterialeStoricizzato.xsl";
		datiXml.setTitle("Ricerca Materiale Storicizzato");
		datiXml.addStyleSheet("../style/RicercaMaterialeStoricizzato.css");

//		datiXml.addJavaScript("../js/mx/testJS.js");
//		datiXml.addJavaScript("../js/mx/div.js");
//		datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
		datiXml.addJavaScript("../js/RicercaMaterialeAttivo/RicercaMaterialeAttivo.js");

		element = new MessageElement();
		element.setName("ricercaMaterialeStoricizzato");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;

		try
		{
			log.debug("AnagraficaUtente - edit");
			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/RicercaMaterialeStoricizzato.css");

			datiXml.addJavaScript("../js/RicercaMaterialeStoricizzato/Edit.js");
			edit = new MessageElement();
			edit.setName("edit");
			edit=Show.show(request, edit, datiXml, request.getParameter("idRichieste"));
			element.addChildElement(edit);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}

		}
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
	protected void show(String idRichiesta) throws ServletException, IOException
	{
		MessageElement show = null;

		try
		{
			log.debug("RicercaMaterialeStoricizzato - show");
			super.show(idRichiesta);
//			datiXml.addStyleSheet("../style/calendar/jscal2.css");
//			datiXml.addStyleSheet("../style/calendar/border-radius.css");
//			datiXml.addStyleSheet("../style/calendar/gold/gold.css");

			datiXml.addJavaScript("../js/calendar/verifyDataFormat.js");
//			datiXml.addJavaScript("../js/calendar/jscal2.js");
//			datiXml.addJavaScript("../js/calendar/lang/it.js");
			show = new MessageElement();
			show.setName("show");

			if (request.getParameter("Ric_anno") != null)
				datiXml.getConvert().addChildElement(show, "Ric_anno", request.getParameter("Ric_anno"), false);
			if (request.getParameter("Ric_idRichieste") != null)
				datiXml.getConvert().addChildElement(show, "Ric_idRichieste", request.getParameter("Ric_idRichieste"), false);
			if (request.getParameter("Ric_idUtente") != null)
				datiXml.getConvert().addChildElement(show, "Ric_idUtente", request.getParameter("Ric_idUtente"), false);
			if (request.getParameter("Ric_cognomeKey") != null)
				datiXml.getConvert().addChildElement(show, "Ric_cognomeKey", request.getParameter("Ric_cognomeKey"), false);
			if (request.getParameter("Ric_nomeKey") != null)
				datiXml.getConvert().addChildElement(show, "Ric_nomeKey", request.getParameter("Ric_nomeKey"), false);
			if (request.getParameter("Ric_dataStart") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStart", request.getParameter("Ric_dataStart"), false);
			if (request.getParameter("Ric_dataStop") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStop", request.getParameter("Ric_dataStop"), false);
			if (request.getParameter("Ric_inventario") != null)
				datiXml.getConvert().addChildElement(show, "Ric_inventario", request.getParameter("Ric_inventario"), false);
			if (request.getParameter("Ric_collocazione") != null)
				datiXml.getConvert().addChildElement(show, "Ric_collocazione", request.getParameter("Ric_collocazione"), false);

			if (request.getParameter("idRichieste") != null)
				datiXml.getConvert().addChildElement(show, "Ric_idRichieste", request.getParameter("idRichieste"), false);
			if (request.getParameter("idUtente") != null)
				datiXml.getConvert().addChildElement(show, "Ric_idUtente", request.getParameter("idUtente"), false);
			if (request.getParameter("cognomeKey") != null)
				datiXml.getConvert().addChildElement(show, "Ric_cognomeKey", request.getParameter("cognomeKey"), false);
			if (request.getParameter("nomeKey") != null)
				datiXml.getConvert().addChildElement(show, "Ric_nomeKey", request.getParameter("nomeKey"), false);
			if (request.getParameter("dataStart") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStart", request.getParameter("dataStart"), false);
			if (request.getParameter("dataStop") != null)
				datiXml.getConvert().addChildElement(show, "Ric_dataStop", request.getParameter("dataStop"), false);

			genServizi(show, request.getParameter("Ric_idServizi"));
			genStatoMovimento(show, request.getParameter("Ric_idStatoMovimenti"));
			genListaAnni(show, request, datiXml);

			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || (idRichiesta != null && !idRichiesta.trim().equals("")))
				show = Show.show(request, show, datiXml, idRichiesta);
			element.addChildElement(show);
		}
		catch (SOAPException e)
		{
			log.error(e);
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

	/**
	 * Questo metodo viene utilizzato per calcolare la lista degli anno dello
	 * Storico
	 * @param show
	 * @throws ServletException
	 */
	public static void genListaAnni(MessageElement show, MxMultipartRequest request, DatiXml datiXml) throws ServletException
	{
		MsSqlPool msp = null;
		ResultSet rsTables = null;
		String querySql = null;
		MessageElement annate = null;


		try
		{
			msp = Configuration.poolStorico.getConn();

			if (msp.getTipoDb().equals(MsSqlPool.MAXDB))
				querySql = "select TABLENAME from tables where TYPE='TABLE' AND TABLENAME like 'storico%' order by TABLENAME desc";
			else if (msp.getTipoDb().equals(MsSqlPool.POSTGRES))
				querySql = "SELECT tablename FROM pg_tables where tablename like 'storico%' order by tablename desc";
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
							request.getParameter("Ric_anno").equals(rsTables.getString(1).toLowerCase().replace("storico_", "")))
						datiXml.getConvert().addChildElement(annate, "anno", rsTables.getString(1).toLowerCase().replace("storico_", ""), "selected", "selected", true, true);
					else
						datiXml.getConvert().addChildElement(annate, "anno", rsTables.getString(1).toLowerCase().replace("storico_", ""), true);
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
	 * 
	 * @param show
	 * @param idServizi
	 * @throws ServletException
	 */
	private void genServizi(MessageElement show, String idServizi) throws ServletException
	{
		Servizi servizi = null;
		ResultSet rsServizi = null;
		MessageElement meSerizi = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			servizi = new Servizi(poolTeca);
			servizi.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsServizi = servizi.startSelect();

			meSerizi = new MessageElement();
			meSerizi.setName("servizi");
			while(rsServizi.next())
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsServizi.getString("idServizi"));
				obbAttr.add("true");

				if (idServizi != null &&
						rsServizi.getString("idServizi").equals(idServizi))
				{
					keyAttr.add("selected");
					valueAttr.add("selected");
					obbAttr.add("true");
				}

				datiXml.getConvert().addChildElement(meSerizi, "servizio", rsServizi.getString("descrizione"), keyAttr, valueAttr, true, obbAttr);
			}
			show.addChildElement(meSerizi);
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
				if (rsServizi != null)
					rsServizi.close();
				if (servizi != null)
					servizi.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}

	private void genStatoMovimento(MessageElement show, String idStatoMovimento) throws ServletException
	{
		StatoMovimento statoMovimento = null;
		ResultSet rsStatoMovimento = null;
		MessageElement meStatoMovimento = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			statoMovimento = new StatoMovimento(poolTeca);
			statoMovimento.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsStatoMovimento = statoMovimento.startSelect();

			meStatoMovimento = new MessageElement();
			meStatoMovimento.setName("statoMovimenti");
			while(rsStatoMovimento.next())
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsStatoMovimento.getString("idStatoMovimenti"));
				obbAttr.add("true");

				if (idStatoMovimento != null &&
						rsStatoMovimento.getString("idStatoMovimenti").equals(idStatoMovimento))
				{
					keyAttr.add("selected");
					valueAttr.add("selected");
					obbAttr.add("true");
				}

				datiXml.getConvert().addChildElement(meStatoMovimento, "statoMovimento", rsStatoMovimento.getString("descrizione"), keyAttr, valueAttr, true, obbAttr);
			}
			show.addChildElement(meStatoMovimento);
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
				if (rsStatoMovimento != null)
					rsStatoMovimento.close();
				if (statoMovimento != null)
					statoMovimento.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}
}
