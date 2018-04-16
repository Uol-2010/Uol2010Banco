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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampanteColore.Show;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class StampanteColore extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(StampanteColore.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public StampanteColore()
	{
		
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/StampanteColore.xsl";
		datiXml.setTitle("Amministrazione - Stampante Colore");

		element = new MessageElement();
		element.setName("stampanteColore");
		modulo = "StpCol";
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

			datiXml.addJavaScript("../js/amministrazione/StampanteColore/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			if (request.getParameter("idStampanteColore").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idStampanteColore", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("idStampanteColore"), edit, modulo);
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
	protected void extend() throws StdModuliException, ServletException, IOException
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

	private void delete() throws StdModuliException, ServletException
	{
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteColore table = null;

		try
		{
			if (request.getParameter("idStampanteColore") != null &&
					!request.getParameter("idStampanteColore").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteColore(Configuration.poolUol2010);
				table.setCampoValue("idStampanteColore",  request.getParameter("idStampanteColore"));
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
			throw new StdModuliException(e.getMessage());
		}
		catch (IOException e)
		{
			log.error(e);
			throw new StdModuliException(e.getMessage());
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

			datiXml.addJavaScript("../js/amministrazione/StampanteColore/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idStampanteColore", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idStampanteColore", request.getParameter("Ric_idStampanteColore"), false);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteColore table = null;
		boolean errori = false;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.StampanteColore(Configuration.poolUol2010);
			table.setCampoValue("descrizione", request.getParameter("descrizione"));
			table.setCampoValue("fileImg", request.getParameter("fileImg"));
			if (request.getParameter("idStampanteColore").equals("0"))
				table.insert();
			else
			{
				table.setCampoValue("idStampanteColore", request.getParameter("idStampanteColore"));
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
  			show(table.get("idStampanteColore"));
  			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
  			{
  				log.debug("AddElement");
  				datiXml.addElement(element);
  			}
			}
		}
	}
}
