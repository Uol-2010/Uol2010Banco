/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import net.bncf.uol2010.banco.servlet.moduli.ricercaMaterialeAttivo.Show;
import net.bncf.xsd.RichiesteXsd;
import net.bncf.xsd.richieste.Richieste;
import net.bncf.xsd.richieste.Richieste.Utente;
import net.bncf.xsd.richieste.Richieste.Utente.Autorizzazione;
import net.bncf.xsd.richieste.Richieste.SituazioneRichiesta;
import net.bncf.xsd.richieste.Richieste.SituazioneRichiesta.Servizio;
import net.bncf.xsd.richieste.Autore;
import net.bncf.xsd.richieste.DatiBibliografici;
import net.bncf.xsd.richieste.DatiBibliografici.Segnatura;
import net.bncf.xsd.richieste.DatiBibliografici.Segnatura.Fruibilita;
import net.bncf.xsd.richieste.DatiBibliografici.Segnatura.Inventario;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import mx.randalf.moduli.servlet.core.exception.StdModuliException;

/**
 * @author massi
 *
 */
public class RicercaMaterialeAttivo extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(RicercaMaterialeAttivo.class);

	/**
	 * 
	 */
	public RicercaMaterialeAttivo()
	{
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare per informazioni relative alla visualizzazione
	 * 
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "RicercaMaterialeAttivo.xsl";
		datiXml.setTitle("Ricerca Materiale Attivo");
		datiXml.addStyleSheet("../style/RicercaMaterialeAttivo.css");

//		datiXml.addJavaScript("../js/mx/testJS.js");
//		datiXml.addJavaScript("../js/mx/div.js");
//		datiXml.addJavaScript("../js/anagraficaUtente/AccessoAjax.js");
		datiXml.addJavaScript("../js/RicercaMaterialeAttivo/RicercaMaterialeAttivo.js");

		element = new MessageElement();
		element.setName("ricercaMaterialeAttivo");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
		MessageElement edit = null;

		try
		{
			log.debug("AnagraficaUtente - edit");
			datiXml.addStyleSheet("../style/stdGraph/edit.css");
			datiXml.addStyleSheet("../style/RicercaMaterialeAttivo.css");

			datiXml.addJavaScript("../js/RicercaMaterialeAttivo/Edit.js");
			edit = new MessageElement();
			edit.setName("edit");

			if (request.getParameter("Ric_idRichieste") != null && !request.getParameter("Ric_idRichieste").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_idRichieste", request.getParameter("Ric_idRichieste"), false);
			else if (request.getParameter("idRichieste") != null && !request.getParameter("idRichieste").equals(""))
					datiXml.getConvert().addChildElement(edit, "Ric_idRichieste", request.getParameter("idRichieste"), false);

			if (request.getParameter("Ric_idUtente") != null && !request.getParameter("Ric_idUtente").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_idUtente", request.getParameter("Ric_idUtente"), false);
			else if (request.getParameter("idUtente") != null && !request.getParameter("idUtente").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_idUtente", request.getParameter("idUtente"), false);

			if (request.getParameter("Ric_cognomeKey") != null && !request.getParameter("Ric_cognomeKey").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_cognomeKey", request.getParameter("Ric_cognomeKey"), false);
			else if (request.getParameter("cognomeKey") != null && !request.getParameter("cognomeKey").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_cognomeKey", request.getParameter("cognomeKey"), false);

			if (request.getParameter("Ric_nomeKey") != null && !request.getParameter("Ric_nomeKey").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_nomeKey", request.getParameter("Ric_nomeKey"), false);
			else if (request.getParameter("nomeKey") != null && !request.getParameter("nomeKey").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_nomeKey", request.getParameter("nomeKey"), false);

			if (request.getParameter("Ric_dataStart") != null && !request.getParameter("Ric_dataStart").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_dataStart", request.getParameter("Ric_dataStart"), false);
			else if (request.getParameter("dataStart") != null && !request.getParameter("dataStart").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_dataStart", request.getParameter("dataStart"), false);

			if (request.getParameter("Ric_dataStop") != null && !request.getParameter("Ric_dataStop").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_dataStop", request.getParameter("Ric_dataStop"), false);
			else if (request.getParameter("dataStop") != null && !request.getParameter("dataStop").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_dataStop", request.getParameter("dataStop"), false);

			if (request.getParameter("Ric_idServizi") != null && !request.getParameter("Ric_idServizi").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_idServizi", request.getParameter("Ric_idServizi"), false);

			if (request.getParameter("Ric_idStatoMovimenti") != null && !request.getParameter("Ric_idStatoMovimenti").equals(""))
				datiXml.getConvert().addChildElement(edit, "Ric_idServizi", request.getParameter("Ric_idStatoMovimenti"), false);

			edit=Show.show(request, edit, datiXml, request.getParameter("idRichieste"));
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
	 * @throws StdModuliException 
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException, StdModuliException
	{
		if (request.getParameter("azione")!=null)
		{
			if (request.getParameter("azione").equals("storicizza"))
			{
				storicizza(null);
			}
			else if (request.getParameter("azione").equals("ristampa"))
			{
				ristampa();
			}
			else if (request.getParameter("azione").equals("cambioServizio"))
			{
				cambioServizio();
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per eseguire il cambio del Servizio
	 * @throws StdModuliException 
	 */
	private void cambioServizio() throws StdModuliException
	{
		ViewRichieste richieste = null;
		ResultSet rs = null;
		RichiesteXsd richiesteXsd = null;
		Controlli controlli = null;
		String noteUte = null;
		String notificaStatoRichiesta = null;
		String emailStatoRichiesta= null; 
		String indirizzoIp = null;
		List<RisultatoControlli> risCont = null;
		String message = null;


		try
		{
			if (request.getParameter("idRichieste") != null &&
					!request.getParameter("idRichieste").trim().equals("") &&
					!request.getParameter("idRichieste").equals("0"))
			{
				richieste = new ViewRichieste(Configuration.poolUol2010);
				richieste.setCampoValue("idRichieste", request.getParameter("idRichieste"));
				rs = richieste.startSelect();
				if (rs.next())
				{
					richiesteXsd = new RichiesteXsd();
					richiesteXsd.setRichieste(new Richieste());
					
					richiesteXsd.getRichieste().setUtente(new Utente());
					richiesteXsd.getRichieste().getUtente().setLogin(rs.getString("idUtente"));
					richiesteXsd.getRichieste().getUtente().setAutorizzazione(new Autorizzazione());
					richiesteXsd.getRichieste().getUtente().getAutorizzazione().setID(rs.getString("idAutorizzazioniUtente"));

					richiesteXsd.getRichieste().setSituazioneRichiesta(new SituazioneRichiesta());
					richiesteXsd.getRichieste().getSituazioneRichiesta().setServizio(new Servizio());
					richiesteXsd.getRichieste().getSituazioneRichiesta().getServizio().setID(request.getParameter("idServizi"));
					
					richiesteXsd.getRichieste().setDatiBibliografici(new DatiBibliografici());
					richiesteXsd.getRichieste().getDatiBibliografici().setBid(rs.getString("bid"));
					if (rs.getString("autore")!=null)
					{
						richiesteXsd.getRichieste().getDatiBibliografici().setAutore(new Autore());
						richiesteXsd.getRichieste().getDatiBibliografici().getAutore().setValue(rs.getString("autore"));
					}
					richiesteXsd.getRichieste().getDatiBibliografici().setTitolo(rs.getString("titolo"));
					richiesteXsd.getRichieste().getDatiBibliografici().setPubblicazione(rs.getString("pubblicazione"));

					richiesteXsd.getRichieste().getDatiBibliografici().setSegnatura(new Segnatura());
					richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().setSegnatura(rs.getString("segnatura"));
					richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().setFruibilita(new Fruibilita());
					richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().getFruibilita().setCodiceFruibilita(rs.getString("idFruibilita"));

					if (rs.getString("inventario") != null)
					{
						richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().setInventario(new Inventario());
						
						richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().getInventario().setValue(rs.getString("inventario"));
						if (rs.getString("precInv1") != null)
							richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().getInventario().setPrecisazione(rs.getString("precInv1"));
						if (rs.getString("precInv2") != null)
							richiesteXsd.getRichieste().getDatiBibliografici().getSegnatura().getInventario().setSecondaPrecisazione(rs.getString("precInv2"));
					}
					richiesteXsd.getRichieste().getDatiBibliografici().setAnnoPub(new BigInteger(rs.getString("annoPeriodico")));
					richiesteXsd.getRichieste().getDatiBibliografici().setNatura(rs.getString("natura"));

					if (rs.getString("annata") != null)
						richiesteXsd.getRichieste().getSituazioneRichiesta().setAnno(Integer.parseInt(rs.getString("annata")));
					if (rs.getString("fascicoli") != null)
						richiesteXsd.getRichieste().getSituazioneRichiesta().setMeseGiorno(rs.getString("fascicoli"));
					if (rs.getString("volume") != null)
						richiesteXsd.getRichieste().getSituazioneRichiesta().setVolume(rs.getString("volume"));

					noteUte = rs.getString("noteUte");
					notificaStatoRichiesta = rs.getString("notificaStatoRichiesta");
					emailStatoRichiesta = rs.getString("emailStatoRichiesta");
					indirizzoIp = rs.getString("indirizzoIp");
				}
			}
			else
				throw new StdModuliException("Non risulta essere indicato il movimento da aggiornare");
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new StdModuliException(e.getMessage());
		}
		catch (StdModuliException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (richieste != null)
					richieste.stopSelect();
				controlli = new Controlli(Configuration.poolUol2010);
				controlli.esegui(richiesteXsd, false);
				
				risCont = richiesteXsd.getRichieste().getSituazioneRichiesta().getRisultatoControlli();
				if (risCont != null)
				{
					for (int x=0; x<risCont.size(); x++)
					{
						if (risCont.get(x).isBloccante() &&
								!(risCont.get(x).getID()==18))
						{
							if (message==null)
								message = "";
							else
								message += ", ";
							message += risCont.get(x).getValue();
						}
					}
				}
				if (message==null)
				{
					controlli.writeRichiesta(richiesteXsd, noteUte, notificaStatoRichiesta, 
					emailStatoRichiesta, indirizzoIp);
					storicizza(controlli.getIdRichieste());
				}
				else
					throw new StdModuliException("Riscontrati alcuni problemi durante la generazione del movimento ["+message+"]");
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new StdModuliException(e.getMessage());
			}
			catch (ControlliException e)
			{
				log.error(e);
				throw new StdModuliException(e.getMessage());
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per eseguire la ristampa del movimento
	 * @throws StdModuliException 
	 */
	private void ristampa() throws StdModuliException
	{
		Richieste richieste = null;
		
		try
		{
			if (request.getParameter("idRichieste") != null &&
					!request.getParameter("idRichieste").trim().equals("") &&
					!request.getParameter("idRichieste").equals("0"))
			{
				richieste = new Richieste(Configuration.poolUol2010);
				richieste.setCampoValue("idRichieste", request.getParameter("idRichieste"));
				richieste.setCampoValue("progIter", 1);
				if (richieste.update()==0)
					throw new StdModuliException("Non risulta essere stato eseguito l'aggiornamento del movimento ["+request.getParameter("idRichieste")+"]");
			}
			else
				throw new StdModuliException("Non risulta essere indicato il movimento da aggiornare");
		}
		catch (MsSqlException e)
		{
			log.error(e);
			throw new StdModuliException(e.getMessage());
		}
		catch (StdModuliException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				show(request.getParameter("idRichieste"));
				if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
				{
					log.debug("AddElement");
					datiXml.addElement(element);
				}
			}
			catch (ServletException e)
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
	}

	/**
	 * Questo metodo viene utilizzato per storicizzare un determinato Movimento
	 * @throws StdModuliException 
	 */
	private void storicizza(String idRichieste) throws StdModuliException
	{
		Storico storico = null;
		
		try
		{
			storico = new Storico(Configuration.poolUol2010, Configuration.poolStorico);
			storico.storicizzaMovimenti(Integer.parseInt(request.getParameter("idRichieste")));

			show("-1");
			if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null)
			{
				log.debug("AddElement");
				datiXml.addElement(element);
			}
		}
		catch (NumberFormatException e)
		{
			log.error(e);
			throw new StdModuliException(e.getMessage());
		}
		catch (ServletException e)
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
	protected void show(String idRichiesta) throws ServletException, IOException
	{
		MessageElement show = null;

		try
		{
			log.debug("RicercaMaterialeAttivo - show");
			super.show(idRichiesta);
//			datiXml.addStyleSheet("../style/calendar/jscal2.css");
//			datiXml.addStyleSheet("../style/calendar/border-radius.css");
//			datiXml.addStyleSheet("../style/calendar/gold/gold.css");

			datiXml.addJavaScript("../js/calendar/verifyDataFormat.js");
//			datiXml.addJavaScript("../js/calendar/jscal2.js");
//			datiXml.addJavaScript("../js/calendar/lang/it.js");
			show = new MessageElement();
			show.setName("show");

			if (idRichiesta == null || !idRichiesta.equals("-1"))
			{
  			if (request.getParameter("Ric_idRichieste") != null && !request.getParameter("Ric_idRichieste").equals(""))
  				datiXml.getConvert().addChildElement(show, "Ric_idRichieste", request.getParameter("Ric_idRichieste"), false);
  			else if (request.getParameter("idRichieste") != null && !request.getParameter("idRichieste").equals(""))
  					datiXml.getConvert().addChildElement(show, "Ric_idRichieste", request.getParameter("idRichieste"), false);
			}

			if (request.getParameter("Ric_idUtente") != null && !request.getParameter("Ric_idUtente").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idUtente", request.getParameter("Ric_idUtente"), false);
			else if (request.getParameter("idUtente") != null && !request.getParameter("idUtente").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_idUtente", request.getParameter("idUtente"), false);

			if (request.getParameter("Ric_cognomeKey") != null && !request.getParameter("Ric_cognomeKey").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_cognomeKey", request.getParameter("Ric_cognomeKey"), false);
			else if (request.getParameter("cognomeKey") != null && !request.getParameter("cognomeKey").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_cognomeKey", request.getParameter("cognomeKey"), false);

			if (request.getParameter("Ric_nomeKey") != null && !request.getParameter("Ric_nomeKey").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_nomeKey", request.getParameter("Ric_nomeKey"), false);
			else if (request.getParameter("nomeKey") != null && !request.getParameter("nomeKey").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_nomeKey", request.getParameter("nomeKey"), false);

			if (request.getParameter("Ric_dataStart") != null && !request.getParameter("Ric_dataStart").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_dataStart", request.getParameter("Ric_dataStart"), false);
			else if (request.getParameter("dataStart") != null && !request.getParameter("dataStart").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_dataStart", request.getParameter("dataStart"), false);

			if (request.getParameter("Ric_dataStop") != null && !request.getParameter("Ric_dataStop").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_dataStop", request.getParameter("Ric_dataStop"), false);
			else if (request.getParameter("dataStop") != null && !request.getParameter("dataStop").equals(""))
				datiXml.getConvert().addChildElement(show, "Ric_dataStop", request.getParameter("dataStop"), false);

			if (request.getParameter("Ric_inventario") != null)
				datiXml.getConvert().addChildElement(show, "Ric_inventario", request.getParameter("Ric_inventario"), false);
			if (request.getParameter("Ric_collocazione") != null)
				datiXml.getConvert().addChildElement(show, "Ric_collocazione", request.getParameter("Ric_collocazione"), false);
			if (request.getParameter("Ric_bid") != null)
				datiXml.getConvert().addChildElement(show, "Ric_bid", request.getParameter("Ric_bid"), false);
			
			genServizi(show, request.getParameter("Ric_idServizi"));
			genStatoMovimento(show, request.getParameter("Ric_idStatoMovimenti"));

			if ((request.getParameter("ricerca") != null &&
					request.getParameter("ricerca").equals("Y")) || (idRichiesta != null && !idRichiesta.trim().equals("")))
				show = Show.show(request, show, datiXml, idRichiesta);
			element.addChildElement(show);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
	}

	private void genServizi(MessageElement show, String idServizi) throws ServletException
	{
		Servizi servizi = null;
		ResultSet rsServizi = null;
		MessageElement meSerizi = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			servizi = new Servizi(poolTeca);
			servizi.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsServizi = servizi.startSelect();

			meSerizi = new MessageElement();
			meSerizi.setName("servizi");
			while(rsServizi.next())
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsServizi.getString("idServizi"));
				obbAttr.add("true");

				if (idServizi != null &&
						rsServizi.getString("idServizi").equals(idServizi))
				{
					keyAttr.add("selected");
					valueAttr.add("selected");
					obbAttr.add("true");
				}

				datiXml.getConvert().addChildElement(meSerizi, "servizio", rsServizi.getString("descrizione"), keyAttr, valueAttr, true, obbAttr);
			}
			show.addChildElement(meSerizi);
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
				if (rsServizi != null)
					rsServizi.close();
				if (servizi != null)
					servizi.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}

	private void genStatoMovimento(MessageElement show, String idStatoMovimento) throws ServletException
	{
		StatoMovimento statoMovimento = null;
		ResultSet rsStatoMovimento = null;
		MessageElement meStatoMovimento = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			statoMovimento = new StatoMovimento(poolTeca);
			statoMovimento.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsStatoMovimento = statoMovimento.startSelect();

			meStatoMovimento = new MessageElement();
			meStatoMovimento.setName("statoMovimenti");
			while(rsStatoMovimento.next())
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rsStatoMovimento.getString("idStatoMovimenti"));
				obbAttr.add("true");

				if (idStatoMovimento != null &&
						rsStatoMovimento.getString("idStatoMovimenti").equals(idStatoMovimento))
				{
					keyAttr.add("selected");
					valueAttr.add("selected");
					obbAttr.add("true");
				}

				datiXml.getConvert().addChildElement(meStatoMovimento, "statoMovimento", rsStatoMovimento.getString("descrizione"), keyAttr, valueAttr, true, obbAttr);
			}
			show.addChildElement(meStatoMovimento);
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
				if (rsStatoMovimento != null)
					rsStatoMovimento.close();
				if (statoMovimento != null)
					statoMovimento.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
				throw new ServletException(e.getMessage());
			}
		}
	}
}
