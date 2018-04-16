/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.stdModuli;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.navigator.TableNavigator;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class AuthorityFiles extends StdModuliBanco
{

	private Logger log = new Logger(AuthorityFiles.class, "net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Tabella da utilizzare per la ricerca
	 */
	protected TableNavigator table = null;

	/**
	 * Nome della chiave primaria
	 */
	protected String primaryKey = null;

	/**
	 * Variabile utilizzata per indicare il nome del modulo che stiamo utilizzando
	 * 
	 */
	protected String modulo = null;

	/**
	 * Costruttore
	 */
	public AuthorityFiles()
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
		this.fileXsl = "AuthorityFiles.xsl";
//		datiXml.setTitle("Anagrafica Utente");

//		datiXml.addJavaScript("../js/mx/testJS.js");
//		datiXml.addJavaScript("../js/mx/div.js");
//		datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
//		datiXml.addJavaScript("../js/anagraficaUtente/AnagraficaUtente.js");

		element = new MessageElement();
		element.setName("authorityFiles");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;
		ResultSet rs = null;

		try
		{
			log.debug("AutorityFiles - edit Start");
			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/authorityFiles/Edit.css");

			datiXml.addJavaScript("../js/authorityFiles/Edit.js");

			edit = new MessageElement();
			edit.setName("edit");
			datiXml.getConvert().setAttribute(edit, "modulo", modulo, true);
			datiXml.getConvert().addChildElement(edit, "id", request.getParameter("id"), true);

			table.resetAll();
			table.setCampoValue(primaryKey, request.getParameter("id"));
			rs = table.startSelect();
			if (rs.next())
			{
				datiXml.getConvert().addChildElement(edit, "descrizione", rs.getString("descrizione"), true);
			}
			element.addChildElement(edit);
			log.debug("AutorityFiles - edit End");
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
			}
			catch (SQLException e)
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
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException
	{
		log.debug("Authority - extend Start");
		if (request.getParameter("azione")!=null)
		{
			if (request.getParameter("azione").equals("delete"))
			{
				delete();
			}
		}
		log.debug("Authority - extend End");
	}

	private void delete() throws ServletException
	{
		try
		{
			if (request.getParameter("id") != null &&
					!request.getParameter("id").trim().equals(""))
			{
				table.resetAll();
				table.setCampoValue(primaryKey, request.getParameter("id"));
				if (table.delete()>0)
				{
					show(null);
					if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
					{
						log.debug("AddElement");
						datiXml.addElement(element);
					}
				}
				else
					throw new ServletException("Problemi nella cancellazione del record");
			}
			else
				throw new ServletException("E' necessario indicare l'identificativo da cancellare");
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (IOException e)
		{
			log.error(e);
		}
		
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
			log.debug("AutorityFiles - show Start");
			super.show(id);
			datiXml.addStyleSheet("../style/authorityFiles/Show.css");
			
			datiXml.addJavaScript("../js/authorityFiles/Show.js");

			show = new MessageElement();
			show.setName("show");
			datiXml.getConvert().setAttribute(show, "modulo", modulo, true);
			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_id", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_id", request.getParameter("Ric_id"), false);
			datiXml.getConvert().addChildElement(show, "Ric_descrizione", request.getParameter("Ric_descrizione"), false);

			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || 
					(id != null && !id.trim().equals("")))
				show = show(show, id);
			element.addChildElement(show);
			log.debug("AutorityFiles - show End");
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
	}

	private MessageElement show(MessageElement show, String id)
	{
		ResultSet rsTable = null;
		MessageElement risultati = null;
		MessageElement risultato = null;
		
		try
		{
			table.resetAll();
			table.setNavigatorMap(request.getParameterMap());
			if (id != null && !id.trim().equals(""))
				table.setCampoValue(primaryKey, id);
			else
			{
				if (request.getParameter("Ric_id") != null && 
						!request.getParameter("Ric_id").equals(""))
					table.setCampoValue(primaryKey, request.getParameter("Ric_id"));
				else if (request.getParameter("Ric_descrizione") != null && 
						!request.getParameter("Ric_descrizione").equals(""))
				{
					table.setCampoValue("descrizione", "%"+request.getParameter("Ric_descrizione")+"%");
					table.getCampo("descrizione").setTipoRicerca("like");
				}
			}
			
			table.setNumPagVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numPag", "10")));
			table.setNumRecVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numRec", "10")));
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();

			risultati = new MessageElement();
			risultati.setName("risultati");
			if (table.getRecTot()>0)
			{
				show.addChildElement(table.viewNavigatore(datiXml.getConvert(), datiXml.getConvertUri(), modulo, "show"));

				while (rsTable.next())
				{
				  if (rsTable.getRow()<=table.getRecFin())
				  {
						risultato = new MessageElement();
						risultato.setName("risultato");
						datiXml.getConvert().addChildElement(risultato, "id", rsTable.getString(primaryKey), true);
						datiXml.getConvert().addChildElement(risultato, "numRecord", rsTable.getRow(), true);
						datiXml.getConvert().addChildElement(risultato, "descrizione", rsTable.getString("descrizione"), false);
						risultati.addChildElement(risultato);
			    }
				  else
				  	break;
				}

			}
			show.addChildElement(risultati);
		}
		catch (NumberFormatException e)
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
				if (rsTable != null)
					rsTable.close();
				if (table != null)
					table.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return show;
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
		ResultSet rs = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table.resetAll();
			table.setCampoValue(primaryKey, request.getParameter("id"));
			rs = table.startSelect();
			table.setCampoValue("descrizione", request.getParameter("descrizione"));
			if (rs.next())
				table.update();
			else
				table.insert();
			
			log.debug("AuthorityFiles - write End");
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (MsSqlException e)
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
			}
			catch (SQLException e)
			{
				log.error(e);
			}
			finally
			{
				show(request.getParameter("id"));
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
				{
					log.debug("AddElement");
					datiXml.addElement(element);
				}
			}
		}
	}

}
