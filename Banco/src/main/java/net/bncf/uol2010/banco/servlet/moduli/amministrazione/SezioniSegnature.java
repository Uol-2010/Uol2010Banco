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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.sezioniSegnature.Show;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class SezioniSegnature extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(SezioniSegnature.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public SezioniSegnature()
	{
		
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/SezioniSegnature.xsl";
		datiXml.setTitle("Amministrazione - Sezioni Segnature");

		element = new MessageElement();
		element.setName("sezioniSegnature");
		modulo = "SezSeg";
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
//			datiXml.addStyleSheet("../style/calendar/jscal2.css");
//			datiXml.addStyleSheet("../style/calendar/border-radius.css");
//			datiXml.addStyleSheet("../style/calendar/gold/gold.css");

			datiXml.addJavaScript("../js/calendar/verifyDataFormat.js");
//			datiXml.addJavaScript("../js/calendar/jscal2.js");
//			datiXml.addJavaScript("../js/calendar/lang/it.js");
			datiXml.addJavaScript("../js/amministrazione/SezioniSegnature/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			GruppoSegnature.listaDisponibilita(edit, datiXml);
			if (request.getParameter("idSezioniSegnature").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idSezioniSegnature", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("idSezioniSegnature"), edit, modulo);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.SezioniSegnature table = null;

		try
		{
			if (request.getParameter("idSezioniSegnature") != null &&
					!request.getParameter("idSezioniSegnature").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.SezioniSegnature(Configuration.poolUol2010);
				table.setCampoValue("idSezioniSegnature",  request.getParameter("idSezioniSegnature"));
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

			datiXml.addJavaScript("../js/amministrazione/SezioniSegnature/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idSezioniSegnature", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idSezioniSegnature", request.getParameter("Ric_idSezioniSegnature"), false);
			datiXml.getConvert().addChildElement(show, "Ric_segnatura", request.getParameter("Ric_segnatura"), false);

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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.SezioniSegnature table = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.SezioniSegnature(Configuration.poolUol2010);
			table.setCampoValue("idDisponibilita", request.getParameter("idDisponibilita"));
			table.setCampoValue("sezioniSegnature", request.getParameter("sezioniSegnature"));
			table.setCampoValue("dataIni", request.getParameter("dataIni"));
			table.setCampoValue("dataFin", request.getParameter("dataFin"));
			table.setCampoValue("note", request.getParameter("note"));
			if (request.getParameter("idSezioniSegnature").equals("0"))
				table.insert();
			else
			{
				table.setCampoValue("idSezioniSegnature", request.getParameter("idSezioniSegnature"));
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
			show(table.get("idSezioniSegnature"));
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}
		}
	}
}
