/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.soap.SOAPException;

import net.bncf.uol2010.banco.servlet.moduli.stdModuli.CascadeMenu;
import net.bncf.uol2010.banco.servlet.moduli.stdModuli.GenAutorizzazione;
import net.bncf.uol2010.utility.validate.standard.StdModuliAuthentication;
import net.bncf.uol2010.utility.xsd.BancoXsd;
import net.bncf.uol2010.utility.xsd.banco.Banco;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente.Autorizzazioni;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente.Autorizzazioni.Diritto;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

/**
 * @author massi
 *
 */
public abstract class StdModuliBanco extends StdModuliAuthentication
{

	private Logger log = Logger.getLogger(StdModuliBanco.class);

	protected BancoXsd banco = null;
	
	/**
	 * Costruttore
	 */
	public StdModuliBanco()
	{
	}

	protected void init()
	{

		try
		{

			log.debug("richieste");
			banco = new BancoXsd();
			log.debug("BancoXml: " + request.getParameter("BancoXml"));
			if (request.getParameter("BancoXml") != null)
			{
				log.debug("richieste.readXml");
				banco.readXml(request.getParameter("BancoXml"));
			}
			else
			{
				log.debug("init Richieste");
				banco.setBanco(new Banco());
				log.debug("initUtente");
				banco.getBanco().setUtente(initUtente());
			}
		}
		catch (JAXBException e)
		{
			log.error(e);
		}

	}

	/**
	 * Questo metodo viene utilizzato per inizializzare i dati Utente
	 * @return
	 */
	private Utente initUtente()
	{
		Utente utente = null;

		utente = new Utente();

		log.debug("Login: " + this.getIdUtente());
		utente.setLogin(this.getIdUtente());
		
		log.debug("Cognome Nome: " + this.getCognomeNome());
		utente.setCognomeNome(this.getCognomeNome());

		utente.setAutorizzazioni(initAutorizzazione());

		return utente;
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare la sezione di Autoriazione
	 * 
	 * @return
	 */
	private Autorizzazioni initAutorizzazione()
	{
		Autorizzazioni autorizzazione = null;
		
		if (utenteXsd != null &&
				utenteXsd.getLogin() != null &&
						utenteXsd.getLogin().getAutorizzazioni() != null)
		{
			autorizzazione = new Autorizzazioni();
			if(utenteXsd.getLogin().getAutorizzazioni().getNome() != null)
				autorizzazione.setNome(utenteXsd.getLogin().getAutorizzazioni().getNome());

			if (utenteXsd.getLogin().getAutorizzazioni().getDiritto() != null)
			{
				for (int x=0; x<utenteXsd.getLogin().getAutorizzazioni().getDiritto().size(); x++)
				{
					autorizzazione.getDiritto().add(new Diritto());
					autorizzazione.getDiritto().get(autorizzazione.getDiritto().size()-1).setID(utenteXsd.getLogin().getAutorizzazioni().getDiritto().get(x).getID());
					autorizzazione.getDiritto().get(autorizzazione.getDiritto().size()-1).setValue(utenteXsd.getLogin().getAutorizzazioni().getDiritto().get(x).getValue());
				}
			}
		}
		else if (banco != null &&
				banco.getBanco() != null &&
				banco.getBanco().getUtente() != null &&
				banco.getBanco().getUtente().getAutorizzazioni() != null)
		{
			autorizzazione = new Autorizzazioni();
			if (banco.getBanco().getUtente().getAutorizzazioni().getNome() != null)
				autorizzazione.setNome(banco.getBanco().getUtente().getAutorizzazioni().getNome());
			if (banco.getBanco().getUtente().getAutorizzazioni().getDiritto() !=null)
			{
				for (int x=0; x<banco.getBanco().getUtente().getAutorizzazioni().getDiritto().size(); x++)
				{
					autorizzazione.getDiritto().add(new Diritto());
					autorizzazione.getDiritto().get(autorizzazione.getDiritto().size()-1).setID(banco.getBanco().getUtente().getAutorizzazioni().getDiritto().get(x).getID());
					autorizzazione.getDiritto().get(autorizzazione.getDiritto().size()-1).setValue(banco.getBanco().getUtente().getAutorizzazioni().getDiritto().get(x).getValue());
				}
			}
		}
		else if (this.getAutorizzazione() != null)
			autorizzazione = GenAutorizzazione.generate(this.getAutorizzazione());
		return autorizzazione;
	}

	/**
	 * @see net.bncf.validate.moduli.standard.StdModuliAuthentication#endEsegui(java.lang.String)
	 */
	@Override
	protected void endEsegui(String pathXsl) throws ServletException
	{
		MessageElement anagrafica = null;
		MessageElement cascadeMenu = null;
		CascadeMenu cMenu = null;
		ArrayList<MessageElement> nodes = null;

		try
		{
			if (isAutenticathed())
			{
				anagrafica = new MessageElement();
				nodes = new ArrayList<MessageElement>();
				anagrafica.setName("anagrafica");
				datiXml.getConvert().addChildElement(anagrafica, 
						nodes, 
						"cognomeNome", 
						this.getCognomeNome(), 
						"ID",
						this.getIdUtente(), 
						true, 
						true);
				datiXml.addElement(anagrafica);

				cMenu = new CascadeMenu(datiXml.getConvert(), banco);
				cascadeMenu = cMenu.generate();

				if (cascadeMenu != null)
				{
					datiXml.addStyleSheet("../style/CascadeMenu.css");
					datiXml.addJavaScript("../js/CascadeMenu.js");
					datiXml.addElement(cascadeMenu);
				}
			}
			if (banco != null)
			{
				nodes = new ArrayList<MessageElement>();
				datiXml.getConvert().addChildElement(element, nodes, "BancoXml", banco.writeToString(), true);
			}
			super.endEsegui(pathXsl);
		}
		catch (PropertyException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (JAXBException e)
		{
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
	}

	/**
	 * @see net.bncf.validate.moduli.standard.StdModuliAuthentication#getCognomeNome()
	 */
	@Override
	protected String getCognomeNome()
	{
		String cognomeNome = null;
		
		cognomeNome = super.getCognomeNome();
		if (cognomeNome ==null)
			cognomeNome = banco.getBanco().getUtente().getCognomeNome();
		return cognomeNome;
	}

	/**
	 * @see net.bncf.validate.moduli.standard.StdModuliAuthentication#getIdUtente()
	 */
	@Override
	protected String getIdUtente()
	{
		String idUtente = null;
		
		idUtente = super.getIdUtente();
		if (idUtente == null)
			idUtente = banco.getBanco().getUtente().getLogin();
		return idUtente;
	}

	protected String getAutorizzazione()
	{
		String autorizzazione = null;
		
		autorizzazione = super.getAutorizzazione();
		if (autorizzazione == null)
			autorizzazione = banco.getBanco().getUtente().getAutorizzazioni().getNome();
		return autorizzazione;
	}
	
	protected void show(String id) throws ServletException, IOException
	{
		datiXml.addStyleSheet("../style/stdGraph/show/Ricerca.css");
		datiXml.addStyleSheet("../style/stdGraph/show/Risultati.css");
		datiXml.addJavaScript("../js/stdGraph/show/Risultati.js");
	}
}
