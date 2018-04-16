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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.Edit;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.Show;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.AutorizzazioniBibliotecario;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.AutorizzazioniUtente;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.Calendario;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.Controlli;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.Fruibilita;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.IterServ;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.IterServLegami;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.Servizi;

import org.apache.axis.message.MessageElement;

/**
 * @author massi
 *
 */
public class GestioneServizi extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(GestioneServizi.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Questa variabile viene utilizzata per verificare se si sta' utilizzando una chiamata Ajax
	 */
	private boolean ajax = false;

	/**
	 * Costruttore
	 */
	public GestioneServizi()
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "amministrazione/GestioneServizi.xsl";
		datiXml.setTitle("Amministrazione - Gestione Servizi");
		datiXml.addStyleSheet("../style/amministrazione/GestioneServizi.css");

		element = new MessageElement();
		element.setName("gestioneServizi");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;
		Edit editor = null;

		try
		{
			log.debug("AnagraficaUtente - edit");
			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/stdGraph/edit/GestFolder.css");
//			datiXml.addStyleSheet("../style/calendar/jscal2.css");
//			datiXml.addStyleSheet("../style/calendar/border-radius.css");
//			datiXml.addStyleSheet("../style/calendar/gold/gold.css");
			datiXml.addStyleSheet("../style/amministrazione/GestioneServizi/Edit.css");

			datiXml.addJavaScript("../js/mx/testJS.js");
			datiXml.addJavaScript("../js/mx/div.js");
			datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
			datiXml.addJavaScript("../js/anagraficaUtente/AnagraficaUtente.js");
			datiXml.addJavaScript("../js/calendar/verifyDataFormat.js");
//			datiXml.addJavaScript("../js/calendar/jscal2.js");
//			datiXml.addJavaScript("../js/calendar/lang/it.js");

			datiXml.addJavaScript("../js/stdGraph/edit/GestFolder.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/Edit.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/Fruibilita.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/Controllo.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/AutUteServizio.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/AutBibServizio.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/CalendarioSettimanale.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/CalendarioEccezione.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/CalendarioSospensione.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/IterServizio.js");
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/IterServizioLegami.js");

			edit = new MessageElement();
			edit.setName("edit");
			editor = new Edit(datiXml, banco);
			editor.edit(request.getParameter("idServizio"), edit);
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

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException
	{
		
		log.debug("Gestione Servizi - extend");
		if (request.getParameter("azione")!=null)
		{
			if (request.getParameter("azione").equals("addFruibilita"))
			{
				ajax = true;
				Fruibilita.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delFruibilita"))
			{
				ajax = true;
				Fruibilita.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("popTabFruibilita"))
			{
				ajax = true;
				Fruibilita.readFruibilita(request.getParameter("idServizi"), datiXml);
			}
			else if (request.getParameter("azione").equals("addControllo"))
			{
				ajax = true;
				Controlli.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delControllo"))
			{
				ajax = true;
				Controlli.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("popTabControllo"))
			{
				ajax = true;
				Controlli.readControlli(request.getParameter("idServizi"), datiXml);
			}
			else if (request.getParameter("azione").equals("spostaControllo"))
			{
				ajax = true;
				Controlli.sposta(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addAutUteServizi"))
			{
				ajax = true;
				AutorizzazioniUtente.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delAutUteServizi"))
			{
				ajax = true;
				AutorizzazioniUtente.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addAutBibServizi"))
			{
				ajax = true;
				AutorizzazioniBibliotecario.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delAutBibServizi"))
			{
				ajax = true;
				AutorizzazioniBibliotecario.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("aggCalSet"))
			{
				ajax = true;
				Calendario.aggSet(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addCalEcc"))
			{
				ajax = true;
				Calendario.writeEcc(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delCalEcc"))
			{
				ajax = true;
				Calendario.deleteEcc(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addIterServ"))
			{
				ajax = true;
				IterServ.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delIterServ"))
			{
				ajax = true;
				IterServ.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addIterServLegami"))
			{
				ajax = true;
				IterServLegami.write(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delIterServLegami"))
			{
				ajax = true;
				IterServLegami.delete(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("addCalSos"))
			{
				ajax = true;
				Calendario.writeSos(request, datiXml, element);
			}
			else if (request.getParameter("azione").equals("delCalSos"))
			{
				ajax = true;
				Calendario.deleteSos(request, datiXml, element);
			}
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#show(java.lang.String)
	 */
	@Override
	protected void show(String id) throws ServletException, IOException
	{
		MessageElement show = null;
		Show showing = null;
		
		try
		{
			super.show(id);
			datiXml.addJavaScript("../js/amministrazione/GestioneServizi/Show.js");

			show = new MessageElement();
			show.setName("show");

			showing = new Show(datiXml, banco);
			show.addChild(showing.show(id));
			element.addChildElement(show);
		}
		catch (SOAPException e)
		{
			log.error(e);
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
		Servizi servizi = null;
		ResultSet rsServizi = null;

		try
		{
			servizi = new Servizi(Configuration.poolUol2010);
			servizi.setCampoValue("idServizi", request.getParameter("idServizi"));

			rsServizi = servizi.startSelect();
			servizi.setCampoValue("descrizione", request.getParameter("descrizione"));
			servizi.setCampoValue("maxMovimenti", request.getParameter("maxMovimenti"));
			servizi.setCampoValue("durataMovimenti", request.getParameter("durataMovimenti"));
			servizi.setCampoValue("durataDeposito", request.getParameter("durataDeposito"));
			servizi.setCampoValue("maxDepositi", request.getParameter("maxDepositi"));
			servizi.setCampoValue("ridisponibilitaMateriale", request.getParameter("ridisponibilitaMateriale"));
			servizi.setCampoValue("email", request.getParameter("email"));
			servizi.setCampoValue("durataRinnovo1", request.getParameter("durataRinnovo1").equals("")?"0":request.getParameter("durataRinnovo1"));
			servizi.setCampoValue("durataRinnovo2", request.getParameter("durataRinnovo2").equals("")?"0":request.getParameter("durataRinnovo2"));
			servizi.setCampoValue("durataRinnovo3", request.getParameter("durataRinnovo3").equals("")?"0":request.getParameter("durataRinnovo3"));
			servizi.setCampoValue("ggDepositi", request.getParameter("ggdepositi").equals("")?"0":request.getParameter("ggdepositi"));
			servizi.setCampoValue("emailchiamate", request.getParameter("emailchiamate").equals("")?"0":request.getParameter("emailchiamate"));
			servizi.setCampoValue("sollecito", request.getParameter("sollecito").equals("")?"0":request.getParameter("sollecito"));
			servizi.setCampoValue("ggsollecito", request.getParameter("ggsollecito").equals("")?"0":request.getParameter("ggsollecito"));

			if (rsServizi.next())
				servizi.update();
			else
				servizi.insert();
			show(servizi.get("idServizi"));
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
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
		finally
		{
			try
			{
				if (rsServizi != null)
					rsServizi.close();
				if (servizi != null)
					servizi.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
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
