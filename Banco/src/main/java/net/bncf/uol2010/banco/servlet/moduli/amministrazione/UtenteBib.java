/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.utenteBib.Show;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.AutorizzazioniBib;

import org.apache.axis.message.MessageElement;

import com.twmacinta.util.MD5;

/**
 * @author MRandazzo
 *
 */
public class UtenteBib extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(UtenteBib.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Questa variabile viene utilizzata per verificare se si sta' utilizzando una chiamata Ajax
	 */
	private boolean ajax = false;

	public UtenteBib()
	{
		
	}
	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/UtenteBib.xsl";
		datiXml.setTitle("Amministrazione - Utente Bibliotecario");

		element = new MessageElement();
		element.setName("utenteBib");
		modulo = "Utente";
		super.init();
	}

	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;
		Show editor = null;

		try
		{
			log.debug("CalendarioFesgtivita - edit");

			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/amministrazione/UtenteBib/Edit.css");

			datiXml.addJavaScript("../js/mx/testJS.js");
			datiXml.addJavaScript("../js/mx/div.js");
			datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
			datiXml.addJavaScript("../js/amministrazione/UtenteBib/Edit.js");
			datiXml.addJavaScript("../js/amministrazione/UtenteBib/ResetPwd.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			if (request.getParameter("idUtenteBib").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idUtenteBib", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("idUtenteBib"), edit, modulo);
			readAutorizzazioniBib(edit);
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

	private void readAutorizzazioniBib(MessageElement risultati) throws ServletException
	{
		AutorizzazioniBib table = null;
		ResultSet rsTable = null;
		MessageElement autorizzazioniBib = null;

		try
		{
			autorizzazioniBib = new MessageElement();
			autorizzazioniBib.setName("autorizzazioniBib");

			table = new AutorizzazioniBib(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			while(rsTable.next())
			{
				datiXml.getConvert().addChildElement(autorizzazioniBib, "autorizzazioneBib", rsTable.getString("descrizione"), "id", rsTable.getString("idAutorizzazioniBib"), true, true);
			}
			risultati.addChildElement(autorizzazioniBib);
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
			else if (request.getParameter("azione").equals("resetPwd"))
			{
				ajax = true;
				resetPwd();
			}
		}
		log.debug("Authority - extend End");
	}

	private void delete() throws ServletException
	{
	  net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.UtenteBib table = null;

		try
		{
			if (request.getParameter("idUtenteBib") != null &&
					!request.getParameter("idUtenteBib").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.UtenteBib(Configuration.poolUol2010);
				table.setCampoValue("idUtenteBib",  request.getParameter("idUtenteBib"));
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

			datiXml.addJavaScript("../js/amministrazione/UtenteBib/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idUtenteBib", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idUtenteBib", request.getParameter("Ric_idUtenteBib"), false);
			datiXml.getConvert().addChildElement(show, "Ric_login", request.getParameter("Ric_login"), false);
			datiXml.getConvert().addChildElement(show, "Ric_cognome", request.getParameter("Ric_cognome"), false);
			datiXml.getConvert().addChildElement(show, "Ric_nome", request.getParameter("Ric_nome"), false);

			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || 
					(id != null && !id.trim().equals("")))
			{
  			showing = new Show(datiXml, banco);
  			show.addChild(showing.show(request, show, id, modulo));
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.UtenteBib table = null;
		MD5 md5 = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.UtenteBib(Configuration.poolUol2010);
			table.setCampoValue("idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"));
			table.setCampoValue("login", request.getParameter("login"));
			table.setCampoValue("cognome", request.getParameter("cognome"));
			table.setCampoValue("nome", request.getParameter("nome"));
			table.setCampoValue("indirizzoIP", request.getParameter("indirizzoIP"));

			if (request.getParameter("idUtenteBib").equals("0"))
			{
				md5 = new MD5();
	  		md5.Update("welcome".getBytes());
	  		table.setCampoValue("password", md5.asHex());
				table.insert();
			}
			else
			{
				table.setCampoValue("idUtenteBib", request.getParameter("idUtenteBib"));
				table.update();
			}
			log.debug("AuthorityFiles - write End");
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		finally
		{
			show(table.get("idUtenteBib"));
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per aggiornare la password dell'utente
	 */
	private void resetPwd()
	{
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.UtenteBib utente = null;
		ResultSet rsUtente = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		MD5 md5 = null; 
		
		try
		{
			utente = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.UtenteBib(poolGestionale);
			utente.setCampoValue("idUtenteBib", request.getParameter("idUtenteBib"));
			utente.setCampoValue("password", request.getParameter("oldPwd"));
			rsUtente = utente.startSelect();
			if (rsUtente.next())
			{
				md5 = new MD5();
	  		md5.Update("welcome".getBytes());
				utente.setCampoValue("password", md5.asHex());
				utente.update();
				datiXml.getConvert().addChildElement(element, nodes, "ResetPwd", "welcome", true);
			}
		}
		catch (SQLException e)
		{
			log.error(e);
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
			try
			{
				if (rsUtente != null)
					rsUtente.close();
				if (utente != null)
					utente.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
			finally
			{
				datiXml.addElement(element);
			}
		}
	}

	@Override
	protected void endEsegui(String pathXsl) throws ServletException
	{
		
		if (getSqlErr() != null || ! ajax)
			super.endEsegui(pathXsl);
		else
		{
			try
			{
				response.setContentType("text/xml");
				response.setCharacterEncoding("utf-8");

				datiXml.printOutputStream(response.getOutputStream());
			}
			catch (SOAPException e)
			{
				log.error(e);
			}
			catch (IOException e)
			{
				log.error(e);
			}
		}
	}
}
