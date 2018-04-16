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
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.exception.StdModuliException;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.Fruibilita;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gruppoSegnature.Show;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.Disponibilita;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class GruppoSegnature extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(GruppoSegnature.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	public GruppoSegnature()
	{
		
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/GruppoSegnature.xsl";
		datiXml.setTitle("Amministrazione - Gruppo Segnature");

		element = new MessageElement();
		element.setName("gruppoSegnature");
		modulo = "GestSegna";
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

			datiXml.addJavaScript("../js/amministrazione/GruppoSegnature/Edit.js");
			
			edit = new MessageElement();
			edit.setName("edit");
			editor = new Show(datiXml, banco);
			Fruibilita.listaFruibilita(edit, null, datiXml);
			listaDisponibilita(edit, datiXml);
			if (request.getParameter("idGruppoSegnature").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idGruppoSegnature", "0", true);
			}
			else
				editor.show(request, edit, request.getParameter("idGruppoSegnature"), edit, modulo);
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

	public static void listaDisponibilita(MessageElement edit, DatiXml datiXml) throws ServletException
	{
		Disponibilita table = null;
		ResultSet rsTable = null;
		MessageElement elenco = null;
		
		try
		{
			table = new Disponibilita(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTable = table.startSelect();
			elenco = new MessageElement();
			elenco.setName("elenco");
			while(rsTable.next())
			{
					datiXml.getConvert().addChildElement(elenco, "disponibilita", rsTable.getString("descrizione"), "id", rsTable.getString("idDisponibilita"), true,true);
			}
			edit.addChildElement(elenco);
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
		}
		log.debug("Authority - extend End");
	}

	private void delete() throws ServletException
	{
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.GruppoSegnature table = null;

		try
		{
			if (request.getParameter("idGruppoSegnature") != null &&
					!request.getParameter("idGruppoSegnature").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.GruppoSegnature(Configuration.poolUol2010);
				table.setCampoValue("idGruppoSegnature",  request.getParameter("idGruppoSegnature"));
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

			datiXml.addJavaScript("../js/amministrazione/GruppoSegnature/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idGruppoSegnature", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idGruppoSegnature", request.getParameter("Ric_idGruppoSegnature"), false);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.GruppoSegnature table = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.segnature.GruppoSegnature(Configuration.poolUol2010);
			table.setCampoValue("idFruibilita", request.getParameter("idFruibilita"));
			table.setCampoValue("idDisponibilita", request.getParameter("idDisponibilita"));
			table.setCampoValue("segnaturaStart", request.getParameter("segnaturaStart"));
			table.setCampoValue("segnaturaStop", request.getParameter("segnaturaStop"));
			table.setCampoValue("natura", request.getParameter("natura"));
			table.setCampoValue("note", request.getParameter("note"));
			if (request.getParameter("idGruppoSegnature").equals("0"))
				table.insert();
			else
			{
				table.setCampoValue("idGruppoSegnature", request.getParameter("idGruppoSegnature"));
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
			show(table.get("idGruppoSegnature"));
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}
		}
	}
}
