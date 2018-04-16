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
import mx.text.converter.ConvertFormat;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.ControlliServizio;
import net.bncf.uol2010.database.table.servizi.Controllo;
import net.bncf.uol2010.database.viewer.servizi.ViewControlliServizio;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Controlli
 * 
 * @author Massimiliano Randazzo
 *
 */
public class Controlli
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Controlli.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");

	public static void readControlli(String idServizio, DatiXml datiXml) throws ServletException
	{
		ViewControlliServizio viewControlliServizio = null;
		ResultSet rsViewControlliServizio = null;
		MessageElement controlli = null;
		MessageElement controllo = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		Vector<String> idControlli =null;

		try
		{
			controlli = new MessageElement();
			controlli.setName("controlli");
			viewControlliServizio = new ViewControlliServizio(Configuration.poolUol2010);
			viewControlliServizio.setCampoValue("idServizi", idServizio);
			viewControlliServizio.getCampo("sequenza").setOrderBy(Column.ORDERBY_CRES, 1);
			rsViewControlliServizio = viewControlliServizio.startSelect();
			idControlli = new Vector<String>();
			while(rsViewControlliServizio.next())
			{
				controllo = new MessageElement();
				controllo.setName("controllo");

				datiXml.getConvert().addChildElement(controllo, "sequenza", rsViewControlliServizio.getString("sequenza"), true);

				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsViewControlliServizio.getString("idControllo"));
				obbAttr.add("true");
				if (!rsViewControlliServizio.getString("idControllo").equals("18"))
					idControlli.add(rsViewControlliServizio.getString("idControllo"));

				keyAttr.add("classe");
				valueAttr.add(rsViewControlliServizio.getString("classeControllo"));
				obbAttr.add("true");

				keyAttr.add("tipoControllo");
				valueAttr.add(rsViewControlliServizio.getString("tipoControllo"));
				obbAttr.add("false");
				datiXml.getConvert().addChildElement(controllo, "controllo", rsViewControlliServizio.getString("descControllo"), keyAttr, valueAttr, true, obbAttr);

				datiXml.getConvert().addChildElement(controllo, "bloccante", rsViewControlliServizio.getString("bloccante"), true);
				datiXml.getConvert().addChildElement(controllo, "messaggio", rsViewControlliServizio.getString("messaggio"), false);

				controlli.addChildElement(controllo);
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
				if (rsViewControlliServizio != null)
					rsViewControlliServizio.close();
				if (viewControlliServizio != null)
					viewControlliServizio.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
			finally
			{
				listaControlli(controlli, datiXml, idControlli);
				datiXml.addElement(controlli);
			}
		}
	}

	private static void listaControlli(MessageElement controlli, DatiXml datiXml, Vector<String> idControlli) throws ServletException
	{
		Controllo controllo = null;
		ResultSet rsControllo = null;
		MessageElement elenco = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			controllo = new Controllo(Configuration.poolUol2010);
			controllo.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsControllo = controllo.startSelect();
			elenco = new MessageElement();
			elenco.setName("elenco");
			while(rsControllo.next())
			{
//				if (!idControlli.contains(rsControllo.getString("idControllo")))
//				{
  				keyAttr = new Vector<String>();
  				valueAttr = new Vector<String>();
  				obbAttr = new Vector<String>();
  
  				keyAttr.add("id");
  				valueAttr.add(rsControllo.getString("idControllo"));
  				obbAttr.add("true");
  
  				keyAttr.add("classe");
  				valueAttr.add(rsControllo.getString("classe"));
  				obbAttr.add("true");
  
  				keyAttr.add("tipoControllo");
  				valueAttr.add(rsControllo.getString("tipoControllo"));
  				obbAttr.add("false");
  				datiXml.getConvert().addChildElement(elenco, "controllo", rsControllo.getString("descrizione"), keyAttr, valueAttr, true,obbAttr);
//				}
			}
			controlli.addChildElement(elenco);
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
				if (rsControllo != null)
					rsControllo.close();
				if (controllo != null)
					controllo.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}

	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		ControlliServizio table = null;
		
		try
		{
			table = new ControlliServizio(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			if (request.getParameter("sequenza") != null &&
					!request.getParameter("sequenza").equals("0"))
				table.setCampoValue("sequenza", request.getParameter("sequenza"));
			table.setCampoValue("idControllo", request.getParameter("idControllo"));
			table.setCampoValue("bloccante", request.getParameter("bloccante"));
			table.setCampoValue("messaggio", ConvertFormat.Iso8859ToUtf8(request.getParameter("messaggio")));
			
			if (request.getParameter("sequenza") != null &&
					!request.getParameter("sequenza").equals("0"))
				table.update();
			else
				table.insert();

			readControlli(request.getParameter("idServizi"), datiXml);
/*
 			datiXml.getConvert().addChildElement(element, "idControllo", request.getParameter("idControllo"), true);
 	 	  datiXml.getConvert().addChildElement(element, "descControllo", request.getParameter("descControllo"), true);
			datiXml.getConvert().addChildElement(element, "sequenza", table.get("sequenza"), false);
			datiXml.getConvert().addChildElement(element, "bloccante", request.getParameter("bloccante"), true);
			datiXml.getConvert().addChildElement(element, "messaggio", request.getParameter("messaggio"), true);
			datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), false);
			*/
		}
		catch (MsSqlException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (ServletException e)
		{
			throw e;
		}
		finally
		{
			if (table != null)
				table.stopSelect();
//			datiXml.addElement(element);
		}
	}
	
	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		ControlliServizio controlliServizio = null;
		
		try
		{
			controlliServizio = new ControlliServizio(Configuration.poolUol2010);
			controlliServizio.setCampoValue("idServizi", request.getParameter("idServizi"));
			controlliServizio.setCampoValue("sequenza", request.getParameter("sequenza"));
			if (controlliServizio.delete()>0)
				readControlli( request.getParameter("idServizi"), datiXml);
//				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
		}
		catch (MsSqlException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (ServletException e)
		{
			throw e;
		}
//		finally
//		{
//			datiXml.addElement(element);
//		}
	}

	public static void sposta(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		int sequenza = 0;
		String azione = "";
		String idServizi = "";
		String sql = "";
		MsSqlPool msp = null;

		try
		{
			sequenza = Integer.parseInt(request.getParameter("sequenza"));
			azione = request.getParameter("sposta");
			idServizi = request.getParameter("idServizi");
			
			if (!idServizi.equals("") &&
					!azione.equals("") &&
					sequenza>0)
			{
  			sql = "update CONTROLLISERVIZIO set SEQUENZA=0 where ID_SERVIZI='"+idServizi+"' and SEQUENZA="+sequenza;
  			msp = Configuration.poolUol2010.getConn();
  			if (msp.esegui(sql)>0)
  			{
  				if (azione.equals("alto"))
  					sql = "update CONTROLLISERVIZIO set SEQUENZA="+sequenza+
  					" where ID_SERVIZI='"+idServizi+"' and SEQUENZA="+(sequenza-1);
  				else
  					sql = "update CONTROLLISERVIZIO set SEQUENZA="+sequenza+
  					" where ID_SERVIZI='"+idServizi+"' and SEQUENZA="+(sequenza+1);

  				msp.esegui(sql);
					if (azione.equals("alto"))
						sql = "update CONTROLLISERVIZIO set SEQUENZA="+(sequenza-1)+
						" where ID_SERVIZI='"+idServizi+"' and SEQUENZA=0";
					else
						sql = "update CONTROLLISERVIZIO set SEQUENZA="+(sequenza+1)+
						" where ID_SERVIZI='"+idServizi+"' and SEQUENZA=0";
					if (msp.esegui(sql)>0)
						readControlli(idServizi, datiXml);
					else
						throw new ServletException("problemi nella esecuzione della query "+sql);
  			}
  			else
  				throw new ServletException("problemi nella esecuzione della query "+sql);
			}
		}
		catch (NumberFormatException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (ServletException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		finally
		{
			if (msp != null)
				Configuration.poolUol2010.releaseConn(msp);
		}
	}
}
