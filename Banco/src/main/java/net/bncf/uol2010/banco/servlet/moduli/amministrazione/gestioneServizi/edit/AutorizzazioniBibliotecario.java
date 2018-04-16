/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.AutBib_Servizi;
import net.bncf.uol2010.database.table.amministrazione.AutorizzazioniBib;
import net.bncf.uol2010.database.viewer.amministrazione.ViewAutorizzazioneBibServizio;

import org.apache.axis.message.MessageElement;

/**
 * Questa classe viene utilizzata per la gestione della tabella dei Autorizzazione UTente
 * 
 * @author MRandazzo
 *
 */
public class AutorizzazioniBibliotecario
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(AutorizzazioniBibliotecario.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");
	
	public static void readAutiorizzazioniBibliotecario(String idServizio, MessageElement edit, DatiXml datiXml) throws ServletException
	{
		ViewAutorizzazioneBibServizio table = null;
		ResultSet rsTable = null;
		MessageElement autBibServizi = null;
		List<String> lista = new ArrayList<String>();
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			autBibServizi = new MessageElement();
			autBibServizi.setName("autBibServizi");
			table = new ViewAutorizzazioneBibServizio(Configuration.poolUol2010);
			table.setCampoValue("idServizi", idServizio);
			table.getCampo("descAutorizzazioniBib").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
			{
				lista.add(rsTable.getString("idAutorizzazioniBib"));

				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsTable.getString("idAutorizzazioniBib"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(autBibServizi, 
						"autBibServizio", 
						rsTable.getString("descAutorizzazioniBib"),
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
					listaAutiorizzazioniBibliotecario(autBibServizi, lista, datiXml);
					edit.addChild(autBibServizi);
				}
				catch (SOAPException e)
				{
					log.error(e);
					throw new ServletException(e.getMessage());
				}
			}
		}
	}

	private static void listaAutiorizzazioniBibliotecario(MessageElement autUteServizi, List<String> lista, DatiXml datiXml) throws ServletException
	{
		AutorizzazioniBib table = null;
		ResultSet rsTable = null;
		MessageElement elenco = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			table = new AutorizzazioniBib(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			elenco = new MessageElement();
			elenco.setName("elenco");
			while(rsTable.next())
			{

				if (!lista.contains(rsTable.getString("idAutorizzazioniBib")))
				{
					keyAttr = new Vector<String>();
					valueAttr = new Vector<String>();
					obbAttr = new Vector<String>();

					keyAttr.add("id");
					valueAttr.add(rsTable.getString("idAutorizzazioniBib"));
					obbAttr.add("true");

					datiXml.getConvert().addChildElement(elenco, 
							"autorizzazioniBib", 
							rsTable.getString("descrizione"), 
							keyAttr, 
							valueAttr, 
							true,
							obbAttr);
				}
			}
			autUteServizi.addChildElement(elenco);
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

	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		AutBib_Servizi table = null;
		ResultSet rs = null;
		int ris = 0;
		
		try
		{
			table = new AutBib_Servizi(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"));
			rs = table.startSelect();
			if (rs.next())
				ris = table.update();
			else
				ris = table.insert();

			if (ris >0)
			{
				datiXml.getConvert().addChildElement(element, "idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"), true);
				datiXml.getConvert().addChildElement(element, "descAutorizzazioniBibliotecario", request.getParameter("descAutorizzazioniBibliotecario"), true);
			}
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (table != null)
					table.stopSelect();
				datiXml.addElement(element);
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}

	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		AutBib_Servizi table = null;
		
		try
		{
			table = new AutBib_Servizi(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"));
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
