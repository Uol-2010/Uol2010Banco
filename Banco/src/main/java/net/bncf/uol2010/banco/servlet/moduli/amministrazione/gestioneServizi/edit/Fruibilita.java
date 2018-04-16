/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit;

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
import net.bncf.uol2010.database.table.servizi.ServiziFruibilita;
import net.bncf.uol2010.database.viewer.servizi.ViewServiziFruibilita;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Fruibilita
 * 
 * @author Massimiliano Randazzo
 *
 */
public class Fruibilita
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Fruibilita.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");
	
	public static void readFruibilita(String idServizio, DatiXml datiXml) throws ServletException
	{
		ViewServiziFruibilita viewServiziFruibilita = null;
		ResultSet rsViewServiziFruibilita = null;
		MessageElement listaFruibilita = null;
		List<String> listaFrui = new ArrayList<String>();

		try
		{
			listaFruibilita = new MessageElement();
			listaFruibilita.setName("listaFruibilita");
			viewServiziFruibilita = new ViewServiziFruibilita(Configuration.poolUol2010);
			viewServiziFruibilita.setCampoValue("idServizi", idServizio);
			viewServiziFruibilita.getCampo("descFruibilita").setOrderBy(Column.ORDERBY_CRES, 1);
			rsViewServiziFruibilita = viewServiziFruibilita.startSelect();
			while(rsViewServiziFruibilita.next())
			{
				listaFrui.add(rsViewServiziFruibilita.getString("idFruibilita"));
				datiXml.getConvert().addChildElement(listaFruibilita, "fruibilita", rsViewServiziFruibilita.getString("descFruibilita"),"id",rsViewServiziFruibilita.getString("idFruibilita"),true, true);
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
				if (rsViewServiziFruibilita != null)
					rsViewServiziFruibilita.close();
				if (viewServiziFruibilita != null)
					viewServiziFruibilita.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
			finally
			{
				listaFruibilita(listaFruibilita, listaFrui, datiXml);
				datiXml.addElement(listaFruibilita);
			}
		}
	}

	public static void listaFruibilita(MessageElement listaFruibilita, List<String> listaFrui, DatiXml datiXml) throws ServletException
	{
		net.bncf.uol2010.banco.servlet.moduli.net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.Fruibilita fruibilita = null;
		ResultSet rsFruibilita = null;
		MessageElement elenco = null;
		
		try
		{
			fruibilita = new net.bncf.uol2010.banco.servlet.moduli.net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.Fruibilita(Configuration.poolUol2010);
			fruibilita.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsFruibilita = fruibilita.startSelect();
			elenco = new MessageElement();
			elenco.setName("elenco");
			while(rsFruibilita.next())
			{
				if (listaFrui ==null ||
						!listaFrui.contains(rsFruibilita.getString("idFruibilita")))
					datiXml.getConvert().addChildElement(elenco, "fruibilita", rsFruibilita.getString("descrizione"), "id", rsFruibilita.getString("idFruibilita"), true,true);
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
				if (rsFruibilita != null)
					rsFruibilita.close();
				if (fruibilita != null)
					fruibilita.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}

	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		ServiziFruibilita serviziFruibilita = null;
		
		try
		{
			serviziFruibilita = new ServiziFruibilita(Configuration.poolUol2010);
			serviziFruibilita.setCampoValue("idFruibilita", request.getParameter("idFruibilita"));
			serviziFruibilita.setCampoValue("idServizi", request.getParameter("idServizi"));
			if (serviziFruibilita.delete()>0)
				readFruibilita(request.getParameter("idServizi"), datiXml);
//				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (ServletException e)
		{
			throw e;
		}
		finally
		{
			datiXml.addElement(element);
		}
	}

	/**
	 * Questo metodo viene utilizzato per aggiornare la fruibilita del servizio
	 * @throws ServletException 
	 */
//	@SuppressWarnings("unchecked")
	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		ServiziFruibilita fruibilita = null;
		ResultSet rsFruibilita = null;
//		ArrayList nodes = new ArrayList();
		
		try
		{
			fruibilita = new ServiziFruibilita(Configuration.poolUol2010);
			fruibilita.setCampoValue("idFruibilita", request.getParameter("idFruibilita"));
			fruibilita.setCampoValue("idServizi", request.getParameter("idServizi"));
			
			rsFruibilita = fruibilita.startSelect();
			if (!rsFruibilita.next())
			{
				fruibilita.insert();
//				datiXml.getConvert().addChildElement(element, nodes, "descFruibilita", request.getParameter("descFruibilita"), true);
//				datiXml.getConvert().addChildElement(element, nodes, "idFruibilita", request.getParameter("idFruibilita"), true);
			}
			readFruibilita(request.getParameter("idServizi"), datiXml);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (ServletException e)
		{
			throw e;
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
//			finally
//			{
//				datiXml.addElement(element);
//			}
		}
	}

}
