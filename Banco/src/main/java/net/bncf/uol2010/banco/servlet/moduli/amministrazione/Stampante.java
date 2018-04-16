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
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.exception.StdModuliException;
import mx.normalize.ConvertText;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampante.Edit;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampante.Show;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampante.edit.StampantiAutorizzazioni;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.segnature.GruppoSegnature;
import net.bncf.uol2010.database.viewer.utenti.ViewAutUteServizi;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class Stampante extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(Stampante.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Questa variabile viene utilizzata per verificare se si sta' utilizzando una chiamata Ajax
	 */
	private boolean ajax = false;

	/**
	 * Questa variabile viene utilizzata per verificare se si sta' utilizzando una chiamata Ajax
	 */
	private boolean ajax2 = false;

	public Stampante()
	{
		
	}
	
	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/Stampante.xsl";
		datiXml.setTitle("Amministrazione - Stampante");

		element = new MessageElement();
		element.setName("stampante");
		modulo = "GestStp";
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
			datiXml.addStyleSheet("../style/amministrazione/Stampante/Edit.css");
			datiXml.addStyleSheet("../style/autocompilatore/show.css");

			datiXml.addJavaScript("../js/mx/testJS.js");
			datiXml.addJavaScript("../js/mx/div.js");
			datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
			datiXml.addJavaScript("../js/amministrazione/Stampante/Edit.js");
			datiXml.addJavaScript("../js/amministrazione/Stampante/AutorizzazioniUtente.js");
			datiXml.addJavaScript("../js/amministrazione/Stampante/StampantiAutorizzazioni.js");
			datiXml.addJavaScript("../js/stdGraph/edit/GestFolder.js");
			datiXml.addJavaScript("../js/autocompilatore/prototype.js");
			datiXml.addJavaScript("../js/autocompilatore/effects.js");
			datiXml.addJavaScript("../js/autocompilatore/controls.js");

			edit = new MessageElement();
			edit.setName("edit");
			editor = new Edit(datiXml, banco);

			editor.readStampanteColore(edit);
			editor.readStampanteModello(edit);
			if (request.getParameter("idStampante").equals("0"))
			{
				datiXml.getConvert().addChildElement(edit, "idStampante", "0", true);
			}
			else
				editor.edit(request, edit, request.getParameter("idStampante"), edit, modulo);
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
			else if (request.getParameter("azione").equals("readServizi"))
			{
				ajax = true;
				readServizi(request);
			}
			else if (request.getParameter("azione").equals("searchSegna"))
			{
				ajax2 = true;
				readGruppoSegnatura(request);
			}
			else if (request.getParameter("azione").equals("addStpAut"))
			{
				ajax = true;
				StampantiAutorizzazioni.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delStpAut"))
			{
				ajax = true;
				StampantiAutorizzazioni.delete(request, datiXml, element);
			}
		}
		log.debug("Authority - extend End");
	}

	private void readGruppoSegnatura(MxMultipartRequest request) throws ServletException
	{
		try
		{
			response.getWriter().write("<ul>");
			if (!readGruppoSegnaturaBetween(request))
				readGruppoSegnaturaLike(request);
			response.getWriter().write("</ul>");
		}
		catch (IOException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		
	}
	
	private boolean readGruppoSegnaturaLike(MxMultipartRequest request) throws ServletException
	{
		GruppoSegnature table = null;
		ResultSet rs = null;
		boolean trovati=false;
		
		try
		{
			table = new GruppoSegnature(Configuration.poolUol2010);
			table.setCampoValue("segnaturaStopKey", ConvertText.conveSegna(request.getParameter("gruppoSegnature"))+"%");
			table.getCampo("segnaturaStopKey").setTipoRicerca("like");
			table.getCampo("segnaturaStart").setOrderBy(Column.ORDERBY_CRES, 1);
			rs = table.startSelect();
			while(rs.next())
			{
				trovati = true;
				response.getWriter().write("<li>"+rs.getString("segnaturaStart")+"-"+rs.getString("segnaturaStop")+"</li>");
			}
		}
		catch (IOException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
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
				throw new ServletException(e.getMessage());
			}
		}
		return trovati;
	}
	
	private boolean readGruppoSegnaturaBetween(MxMultipartRequest request) throws ServletException
	{
		GruppoSegnature table = null;
		ResultSet rs = null;
		boolean trovati=false;
		
		try
		{
			table = new GruppoSegnature(Configuration.poolUol2010);
			table.setCampoValue("segnaturaStartKey", ConvertText.conveSegna(request.getParameter("gruppoSegnature"))+"%");
			table.getCampo("segnaturaStartKey").setTipoRicerca("like");
			table.getCampo("segnaturaStartKey").setOperatore("OR");
			table.setCampoValue("segnatura", ConvertText.conveSegna(request.getParameter("gruppoSegnature")));
			table.getCampo("segnaturaStart").setOrderBy(Column.ORDERBY_CRES, 1);
			rs = table.startSelect();
			while(rs.next())
			{
				trovati = true;
				response.getWriter().write("<li>"+rs.getString("segnaturaStart")+"-"+rs.getString("segnaturaStop")+"</li>");
			}
		}
		catch (IOException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
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
				throw new ServletException(e.getMessage());
			}
		}
		return trovati;
	}

	private void readServizi(MxMultipartRequest request) throws ServletException
	{
		ViewAutUteServizi table = null;
		ResultSet rsTable = null;
		
		try
		{
			table = new ViewAutUteServizi(Configuration.poolUol2010);
			table.setCampoValue("idAutorizzazioniUtente", request.getParameter("idAutorizzazioniUtente"));
			table.getCampo("descServizio").setOrderBy(Column.ORDERBY_CRES, 1);
			
			rsTable = table.startSelect();
			while(rsTable.next())
				datiXml.getConvert().addChildElement(element, "servizi", rsTable.getString("descServizio"), "id", rsTable.getString("idServizi"), true, true);
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

	private void delete() throws ServletException
	{
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.Stampante table = null;

		try
		{
			if (request.getParameter("idStampante") != null &&
					!request.getParameter("idStampante").trim().equals(""))
			{
				table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.Stampante(Configuration.poolUol2010);
				table.setCampoValue("idStampante",  request.getParameter("idStampante"));
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

			datiXml.addJavaScript("../js/amministrazione/Stampante/Show.js");

			show = new MessageElement();
			show.setName("show");

			if (id!= null && !id.trim().equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idStampante", id, false);
			else
				datiXml.getConvert().addChildElement(show, "Ric_idStampante", request.getParameter("Ric_idStampante"), false);
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
		net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.Stampante table = null;

		try
		{
			log.debug("AuthorityFiles - write Start");
			table = new net.bncf.uol2010.banco.servlet.moduli.amministrazione.database.table.amministrazione.stampanti.Stampante(Configuration.poolUol2010);
			table.setCampoValue("descrizione", request.getParameter("descrizione"));
			table.setCampoValue("idStampanteColore", request.getParameter("idStampanteColore"));
			table.setCampoValue("idStampanteModello", request.getParameter("idStampanteModello"));
			table.setCampoValue("codaStampante", request.getParameter("codaStampante"));
			

			if (request.getParameter("idStampante").equals("0"))
				table.insert();
			else
			{
				table.setCampoValue("idStampante", request.getParameter("idStampante"));
				table.update();
			}
			
			log.debug("AuthorityFiles - write End");
		}
		catch (MsSqlException e)
		{
			this.setSqlErr(e.getMessage());
			log.error(e);
		}
		finally
		{
			show(table.get("idStampante"));
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}
		}
	}

	@Override
	protected void endEsegui(String pathXsl) throws ServletException
	{
		
		if (getSqlErr() != null || (!ajax && !ajax2))
			super.endEsegui(pathXsl);
		else
		{
			if (ajax)
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
}
