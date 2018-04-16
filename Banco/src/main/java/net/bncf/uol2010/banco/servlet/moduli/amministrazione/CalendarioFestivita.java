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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.calendarioFestivita.Show;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class CalendarioFestivita extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(CalendarioFestivita.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public CalendarioFestivita()
	{
		
	}
	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/CalendarioFestivita.xsl";
		datiXml.setTitle("Amministrazione - Calendario Festivit&agrave;");

		element = new MessageElement();
		element.setName("calendarioFestivita");
		modulo = "CalFest";
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

			datiXml.addJavaScript("../js/amministrazione/CalendarioFestivita/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			if (request.getParameter("giornoMese").equals("0-0"))
			{
				datiXml.getConvert().addChildElement(edit, "giornoFestivita", "0", true);
				datiXml.getConvert().addChildElement(edit, "meseFestivita", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("giornoMese"), edit, modulo);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.servizi.calendario.CalendarioFestivita table = null;
		String[] giornoMese = null;

		try
		{
			if (request.getParameter("giornoMese") != null &&
					!request.getParameter("giornoMese").trim().equals(""))
			{
				giornoMese = request.getParameter("giornoMese").split("-");
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.servizi.calendario.CalendarioFestivita(Configuration.poolUol2010);
				table.setCampoValue("giornoFestivita", giornoMese[0]);
				table.setCampoValue("meseFestivita", giornoMese[1]);
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
		String[] giornoMese = null;

		try
		{
			super.show(id);
			datiXml.addStyleSheet("../style/authorityFiles/Show.css");

			datiXml.addJavaScript("../js/amministrazione/CalendarioFestivita/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
			{
				giornoMese = id.split("-");
				datiXml.getConvert().addChildElement(show, "Ric_giornoFestivita", giornoMese[0], false);
				datiXml.getConvert().addChildElement(show, "Ric_meseFestivita", giornoMese[1], false);
			}
			else
			{
				datiXml.getConvert().addChildElement(show, "Ric_giornoFestivita", request.getParameter("Ric_giornoFestivita"), false);
				datiXml.getConvert().addChildElement(show, "Ric_meseFestivita", request.getParameter("Ric_meseFestivita"), false);
			}
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.servizi.calendario.CalendarioFestivita table = null;
		ResultSet rs = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.servizi.calendario.CalendarioFestivita(Configuration.poolUol2010);
			table.setCampoValue("giornoFestivita", request.getParameter("giornoFestivita"));
			table.setCampoValue("meseFestivita", request.getParameter("meseFestivita"));
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
				show(request.getParameter("giornoFestivita")+"-"+request.getParameter("meseFestivita"));
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
				{
					log.debug("AddElement");
					datiXml.addElement(element);
				}
			}
		}
	}
}
