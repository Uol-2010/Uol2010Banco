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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.confVarchi.Show;
import net.bncf.uol2010.configuration.Configuration;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class ConfVarchi extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(ConfVarchi.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public ConfVarchi()
	{
		
	}
	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/ConfVarchi.xsl";
		datiXml.setTitle("Amministrazione - Configurazione Varchi");

		element = new MessageElement();
		element.setName("confVarchi");
		modulo = "GestVarchi";
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

			datiXml.addJavaScript("../js/amministrazione/ConfVarchi/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			if (request.getParameter("idConfVarchi").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idConfVarchi", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("idConfVarchi"), edit, modulo);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.ingressi.ConfVarchi table = null;

		try
		{
			if (request.getParameter("idConfVarchi") != null &&
					!request.getParameter("idConfVarchi").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.ingressi.ConfVarchi(Configuration.poolUol2010);
				table.setCampoValue("idConfVarchi",  request.getParameter("idConfVarchi"));
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

			datiXml.addJavaScript("../js/amministrazione/CalendarioFestivita/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idConfVarchi", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idConfVarchi", request.getParameter("Ric_idConfVarchi"), false);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.ingressi.ConfVarchi table = null;
		ResultSet rs = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.ingressi.ConfVarchi(Configuration.poolUol2010);
			table.setCampoValue("idConfVarchi", request.getParameter("idConfVarchi"));
			rs = table.startSelect();
			table.setCampoValue("descr", request.getParameter("descrizione"));
			table.setCampoValue("idVarco", request.getParameter("idVarco"));
			table.setCampoValue("idLettore", request.getParameter("idLettore"));
			table.setCampoValue("flgUtPre", request.getParameter("flgUtPre"));
			table.setCampoValue("flgTeSca", request.getParameter("flgTeSca"));
			table.setCampoValue("flgTeSos", request.getParameter("flgTeSos"));
			table.setCampoValue("flgUtNonPre", request.getParameter("flgUtNonPre"));
			table.setCampoValue("flgUtPres", request.getParameter("flgUtPres"));
			table.setCampoValue("flgLibPre", request.getParameter("flgLibPre"));
			table.setCampoValue("listaServizi", request.getParameter("listaServizi"));
			table.setCampoValue("msgUtPre", request.getParameter("msgUtPre"));
			table.setCampoValue("msgTeSca", request.getParameter("msgTeSca"));
			table.setCampoValue("msgTeSos", request.getParameter("msgTeSos"));
			table.setCampoValue("msgUtNonPre", request.getParameter("msgUtNonPre"));
			table.setCampoValue("msgUtPres", request.getParameter("msgUtPres"));
			table.setCampoValue("msgLibPre", request.getParameter("msgLibPre"));
			table.setCampoValue("tipoVarco", request.getParameter("tipoVarco"));
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
				show(request.getParameter("idConfVarchi"));
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
				{
					log.debug("AddElement");
					datiXml.addElement(element);
				}
			}
		}
	}
}
