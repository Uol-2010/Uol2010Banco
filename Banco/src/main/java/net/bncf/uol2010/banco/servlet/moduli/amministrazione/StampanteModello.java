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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampanteModello.Show;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class StampanteModello extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(StampanteModello.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public StampanteModello()
	{
		
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/StampanteModello.xsl";
		datiXml.setTitle("Amministrazione - Stampante Modello");

		element = new MessageElement();
		element.setName("stampanteModello");
		modulo = "StpMod";
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
			datiXml.addStyleSheet("../style/authorityFiles/Edit.css");

			datiXml.addJavaScript("../js/amministrazione/StampanteModello/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			if (request.getParameter("idStampanteModello").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idStampanteModello", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("idStampanteModello"), edit, modulo);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteModello table = null;

		try
		{
			if (request.getParameter("idStampanteModello") != null &&
					!request.getParameter("idStampanteModello").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteModello(Configuration.poolUol2010);
				table.setCampoValue("idStampanteModello",  request.getParameter("idStampanteModello"));
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

			datiXml.addJavaScript("../js/amministrazione/StampanteModello/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idStampanteModello", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idStampanteModello", request.getParameter("Ric_idStampanteModello"), false);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteModello table = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteModello(Configuration.poolUol2010);
			table.setCampoValue("descrizione", request.getParameter("descrizione"));
			table.setCampoValue("fileModello", request.getParameter("fileModello"));
			if (request.getParameter("idStampanteModello").equals("0"))
				table.insert();
			else
			{
				table.setCampoValue("idStampanteModello", request.getParameter("idStampanteModello"));
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
			show(table.get("idStampanteModello"));
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}
		}
	}
}
