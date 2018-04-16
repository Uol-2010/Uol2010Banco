/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampante.edit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import mx.normalize.ConvertText;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.edit.AutBibAutUte;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.segnature.GruppoSegnature;
import net.bncf.uol2010.database.viewer.amministrazione.stampanti.ViewAutorizzazioneStampanti;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Fruibilita
 * 
 * @author Massimiliano Randazzo
 *
 */
public class StampantiAutorizzazioni
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(StampantiAutorizzazioni.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");
	
	public static void read(String idStampante, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		ViewAutorizzazioneStampanti view = null;
		ResultSet rsView = null;
		MessageElement stampantiAutorizzazioni = null;
		MessageElement stampanteAutorizzazione = null;

		try
		{
			stampantiAutorizzazioni = new MessageElement();
			stampantiAutorizzazioni.setName("stampantiAutorizzazioni");
			view = new ViewAutorizzazioneStampanti(Configuration.poolUol2010);
			view.setCampoValue("idStampante", idStampante);
			view.getCampo("descrizioneAutorizzazioniUtente").setOrderBy(Column.ORDERBY_CRES, 1);
			view.getCampo("descrizioneServizi").setOrderBy(Column.ORDERBY_CRES, 2);
			view.getCampo("segnaturaStart").setOrderBy(Column.ORDERBY_CRES, 3);
			rsView = view.startSelect();
			while(rsView.next())
			{
				stampanteAutorizzazione = new MessageElement();
				stampanteAutorizzazione.setName("stampanteAutorizzazione");
				
				datiXml.getConvert().addChildElement(stampanteAutorizzazione, "autorizzazioniUtente", rsView.getString("descrizioneAutorizzazioniUtente"), "id", rsView.getString("idAutorizzazioniUtente"), true, true);
				datiXml.getConvert().addChildElement(stampanteAutorizzazione, "servizi", rsView.getString("descrizioneServizi"), "id", rsView.getString("idServizi"), true, true);
				datiXml.getConvert().addChildElement(stampanteAutorizzazione, "gruppoSegnature", rsView.getString("segnaturaStart")+" - "+rsView.getString("segnaturaStop"), "id", rsView.getString("idGruppoSegnature"), true, true);
				
				stampantiAutorizzazioni.addChildElement(stampanteAutorizzazione);
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
					AutBibAutUte.lista(stampantiAutorizzazioni, null, datiXml);
					edit.addChild(stampantiAutorizzazioni);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}

	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		net.bncf.uol2010.database.table.amministrazione.stampanti.StampantiAutorizzazioni table = null;
		
		try
		{
			table = new net.bncf.uol2010.database.table.amministrazione.stampanti.StampantiAutorizzazioni(Configuration.poolUol2010);
			table.setCampoValue("idStampante", request.getParameter("idStampante"));
			table.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioneUtente"));
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("idGruppoSegnature", request.getParameter("idGruppoSegnature"));

			if (table.delete()>0)
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
		}
		catch (MsSqlException e)
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
			datiXml.addElement(element);
		}
	}

	private static int getIdStampante(String gruppoSegnature) throws ServletException
	{
		String[] st = null;
		GruppoSegnature table = null;
		ResultSet rs = null;
		int ris = 999999999;
		
		try
		{
			if (gruppoSegnature != null && !gruppoSegnature.equals(""))
			{
				st = gruppoSegnature.split("-");

				if (st.length==2)
				{
					table = new GruppoSegnature(Configuration.poolUol2010);
					table.setCampoValue("segnaturaStartKey", ConvertText.conveSegna(st[0]));
					table.setCampoValue("segnaturaStopKey", ConvertText.conveSegna(st[1]));

					rs = table.startSelect();
					if (rs.next())
						ris = rs.getInt("idGruppoSegnature");
				}
			}
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
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per aggiornare la fruibilita del servizio
	 * @throws ServletException 
	 */
	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element) throws ServletException
	{
		net.bncf.uol2010.database.table.amministrazione.stampanti.StampantiAutorizzazioni fruibilita = null;
		ResultSet rsFruibilita = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		int idGruppoSegnature = 0;
		String  gruppoSegnature = null;
		
		try
		{
			fruibilita = new net.bncf.uol2010.database.table.amministrazione.stampanti.StampantiAutorizzazioni(Configuration.poolUol2010);
			fruibilita.setCampoValue("idStampante", request.getParameter("idStampante"));
			fruibilita.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioneUtente"));
			fruibilita.setCampoValue("idServizi", request.getParameter("idServizi"));
			if (request.getParameter("gruppoSegnature") == null ||
					request.getParameter("gruppoSegnature").trim().equals(""))
				gruppoSegnature = "ALL-ALL";
			else
				gruppoSegnature = request.getParameter("gruppoSegnature");
			idGruppoSegnature = getIdStampante(gruppoSegnature);
			fruibilita.setCampoValue("idGruppoSegnature", idGruppoSegnature);
			
			rsFruibilita = fruibilita.startSelect();
			if (!rsFruibilita.next())
			{
				fruibilita.insert();
				datiXml.getConvert().addChildElement(element, nodes, "idAutorizzazioniUtente", request.getParameter("idAutorizzazioneUtente"), true);
				datiXml.getConvert().addChildElement(element, nodes, "descAutorizzazioneUtente", request.getParameter("descAutorizzazioneUtente"), true);
				datiXml.getConvert().addChildElement(element, nodes, "idServizi", request.getParameter("idServizi"), true);
				datiXml.getConvert().addChildElement(element, nodes, "descServizi", request.getParameter("descServizi"), true);
				datiXml.getConvert().addChildElement(element, nodes, "idGruppoSegnature", idGruppoSegnature, true);
				datiXml.getConvert().addChildElement(element, nodes, "gruppoSegnature", gruppoSegnature, true);
			}
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (MsSqlException e)
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
			finally
			{
				datiXml.addElement(element);
			}
		}
	}

}
