/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioneUtente.Show;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.utenti.AutorizzazioniUte;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class AutorizzazioneUtente extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(AutorizzazioneUtente.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public AutorizzazioneUtente()
	{
		
	}
	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/AutorizzazioneUtente.xsl";
		datiXml.setTitle("Amministrazione - Autorizzazioni Utente");
//		datiXml.addStyleSheet("../style/amministrazione/GestioneServizi.css");

		element = new MessageElement();
		element.setName("autorizzazioneUtente");
		modulo = "AutUte";
		super.init();
	}

	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;
		Show editor = null;

		try
		{
			log.debug("AutorizzazioneUtente - edit");

			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/authorityFiles/Edit.css");

			datiXml.addJavaScript("../js/amministrazione/AutorizzazioneUtente/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			if (request.getParameter("idAutorizzazioniUtente").equals("0"))
				datiXml.getConvert().addChildElement(edit, "idAutorizzazioniUtente", "0", true);
			else
				editor.show(request, edit, request.getParameter("idAutorizzazioniUtente"), edit);
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
		AutorizzazioniUte table = null;
		try
		{
			if (request.getParameter("idAutorizzazioniUtente") != null &&
					!request.getParameter("idAutorizzazioniUtente").trim().equals(""))
			{
				table = new AutorizzazioniUte(Configuration.poolUol2010);
				table.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
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

	@Override
	protected void result() throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void show(String id) throws ServletException, IOException
	{
		MessageElement show = null;
		Show showing = null;

		try
		{
			super.show(id);
			datiXml.addStyleSheet("../style/authorityFiles/Show.css");

			datiXml.addJavaScript("../js/amministrazione/AutorizzazioneUtente/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idAutorizzazioniUtente", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idAutorizzazioniUtente", request.getParameter("Ric_idAutorizzazioniUtente"), false);
			datiXml.getConvert().addChildElement(show, "Ric_descrizione", request.getParameter("Ric_descrizione"), false);

			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || 
					(id != null && !id.trim().equals("")))
			{
  			showing = new Show(datiXml, banco);
  			show.addChild(showing.show(request, show, id));
			}
			element.addChildElement(show);
		}
		catch (SOAPException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
	}

	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
		AutorizzazioniUte table = null;
		ResultSet rs = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new AutorizzazioniUte(Configuration.poolUol2010);
			table.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
			rs = table.startSelect();
			table.setCampoValue("descrizione", request.getParameter("descrizione"));
			table.setCampoValue("autorizzazioneDef", request.getParameter("autorizzazioneDef"));
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
				show(request.getParameter("idAutorizzazioniUtente"));
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
				{
					log.debug("AddElement");
					datiXml.addElement(element);
				}
			}
		}
	}
}
