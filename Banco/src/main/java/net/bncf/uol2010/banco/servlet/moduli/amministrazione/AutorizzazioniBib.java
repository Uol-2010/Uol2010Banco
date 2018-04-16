/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.Edit;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.Show;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.edit.AutBibAutUte;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.edit.AutBibModAmm;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class AutorizzazioniBib extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(AutorizzazioniBib.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Questa variabile viene utilizzata per verificare se si sta' utilizzando una chiamata Ajax
	 */
	private boolean ajax = false;

	public AutorizzazioniBib()
	{
		
	}
	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/AutorizzazioniBib.xsl";
		datiXml.setTitle("Amministrazione - Autorizzazioni Bibliotecario");

		element = new MessageElement();
		element.setName("autorizzazioniBib");
		modulo = "TipAut";
		super.init();
	}

	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;
		Edit editor = null;

		try
		{
			log.debug("CalendarioFesgtivita - edit");

			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/authorityFiles/Edit.css");
			datiXml.addStyleSheet("../style/stdGraph/edit/GestFolder.css");
			datiXml.addStyleSheet("../style/amministrazione/GestioneServizi/Edit.css");

			datiXml.addJavaScript("../js/mx/testJS.js");
			datiXml.addJavaScript("../js/mx/div.js");
			datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
			datiXml.addJavaScript("../js/amministrazione/AutorizzazioniBib/Edit.js");
			datiXml.addJavaScript("../js/amministrazione/AutorizzazioniBib/AutorizzazioniUtente.js");
			datiXml.addJavaScript("../js/amministrazione/AutorizzazioniBib/ModuliAmministrazione.js");
			datiXml.addJavaScript("../js/stdGraph/edit/GestFolder.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Edit(datiXml, banco);
			if (request.getParameter("idAutorizzazioniBib").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idAutorizzazioniBib", "0", true);
			}
			else
				editor.edit(request, edit, request.getParameter("idAutorizzazioniBib"), edit, modulo);
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
			else if (request.getParameter("azione").equals("addAutUte"))
			{
				ajax = true;
				AutBibAutUte.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delAutUte"))
			{
				ajax = true;
				AutBibAutUte.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addModAmm"))
			{
				ajax = true;
				AutBibModAmm.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delModAmm"))
			{
				ajax = true;
				AutBibModAmm.delete(request, datiXml, element);
			}
		}
		log.debug("Authority - extend End");
	}

	private void delete() throws ServletException
	{
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.AutorizzazioniBib table = null;

		try
		{
			if (request.getParameter("idAutorizzazioniBib") != null &&
					!request.getParameter("idAutorizzazioniBib").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.AutorizzazioniBib(Configuration.poolUol2010);
				table.setCampoValue("idAutorizzazioniBib",  request.getParameter("idAutorizzazioniBib"));
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

			datiXml.addJavaScript("../js/amministrazione/AutorizzazioniBib/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idAutorizzazioniBib", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idAutorizzazioniBib", request.getParameter("Ric_idAutorizzazioniBib"), false);
			datiXml.getConvert().addChildElement(show, "Ric_descrizione", request.getParameter("Ric_descrizione"), false);

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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.AutorizzazioniBib table = null;
		boolean errori = false;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.AutorizzazioniBib(Configuration.poolUol2010);
			table.setCampoValue("descrizione", request.getParameter("descrizione"));

			if (request.getParameter("idAutorizzazioniBib").equals("0"))
				table.insert();
			else
			{
				table.setCampoValue("idAutorizzazioniBib", request.getParameter("idAutorizzazioniBib"));
				table.update();
			}
			
			log.debug("AuthorityFiles - write End");
		}
		catch (MsSqlException e)
		{
			errori = true;
			log.error(e);
			throw new StdModuliException(e.getMessage());
		}
		finally
		{
			if (!errori)
			{
				show(table.get("idAutorizzazioniBib"));
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
				{
					log.debug("AddElement");
					datiXml.addElement(element);
				}
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
