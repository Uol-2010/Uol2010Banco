/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.utenteBib;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.UtenteBib;
import net.bncf.xsd.BancoXsd;

import org.apache.axis.message.MessageElement;

/**
 * @author massi
 *
 */
public class Show
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Show.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Questa variabile viene utilizzata per la gestione delle informazioni legate
	 * al Xml finale
	 */
	protected DatiXml datiXml = null;

	/**
	 * Questa variabile viene utilizzata per la gestione delle informazioni
	 * relative alle informazioni degli utenti
	 */
	protected BancoXsd bancoXsd = null;

	/**
	 * Costruttore
	 */
	public Show(DatiXml datiXml, BancoXsd bancoXsd)
	{
		this.datiXml = datiXml;
		this.bancoXsd = bancoXsd;
	}

	/**
	 * Questo metodo viene utilizzato per catturare le informazioni 
	 * relativi hai Servizi 
	 * 
	 * @param id
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public MessageElement show(MxMultipartRequest request, MessageElement show, String id, String modulo) throws ServletException, IOException
	{
		MessageElement risultati = null;

		risultati = new MessageElement();
		risultati.setName("risultati");
		return show(request, show, id, risultati, modulo);
	}

	/**
	 * 
	 * @param id
	 * @param disRis
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public MessageElement show(MxMultipartRequest request, MessageElement show, String id, MessageElement risultati, String modulo) throws ServletException, IOException
	{
		UtenteBib table = null;
		ResultSet rsTable = null;
		MessageElement risultato = null;

		try
		{
			if (id != null && !id.trim().equals(""))
				table = new UtenteBib(Configuration.poolUol2010);
			else
				table = new UtenteBib(Configuration.poolUol2010, request.getParameterMap());
				
			if (id != null && 
					!id.trim().equals(""))
				table.setCampoValue("idUtenteBib", id);
			else
			{
				if (request.getParameter("Ric_login") != null &&
						!request.getParameter("Ric_login").equals(""))
				{
					table.setCampoValue("login", "%"+request.getParameter("Ric_login")+"%");
					table.getCampo("login").setTipoRicerca("like");
				}
				if (request.getParameter("Ric_cognome") != null &&
						!request.getParameter("Ric_cognome").equals(""))
				{
					table.setCampoValue("cognome", "%"+request.getParameter("Ric_cognome")+"%");
					table.getCampo("cognome").setTipoRicerca("like");
				}
				if (request.getParameter("Ric_nome") != null &&
						!request.getParameter("Ric_nome").equals(""))
				{
					table.setCampoValue("nome", "%"+request.getParameter("Ric_nome")+"%");
					table.getCampo("nome").setTipoRicerca("like");
				}
			}
			table.getCampo("cognome").setOrderBy(Column.ORDERBY_CRES, 1);
			table.getCampo("nome").setOrderBy(Column.ORDERBY_CRES, 2);

			table.setNumPagVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numPag", "10")));
			table.setNumRecVisual(Integer.parseInt((String)Configuration.listaParametri.get("anagraficaUtente.navigazione.numRec", "10")));
			rsTable = table.startSelect();
			if (table.getRecTot()>0)
			{
				show.addChildElement(table.viewNavigatore(datiXml.getConvert(), datiXml.getConvertUri(), modulo, "show"));

  			while(rsTable.next())
  			{
				  if (rsTable.getRow()<=table.getRecFin())
				  {
    				if (risultati.getName().equals("risultati"))
    				{
    					risultato = new MessageElement();
    					risultato.setName("risultato");
    					show(rsTable, risultato);
    					risultati.addChildElement(risultato);
    				}
    				else
    					show(rsTable, risultati);
			    }
				  else
				  	break;
  			}
			}
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

		return risultati;
	}

	/**
	 * Questo metodo viene utilizzato per ka generazione del ramo del risultato
	 * 
	 * @param rsServizi
	 * @return
	 * @throws SOAPException 
	 * @throws SQLException 
	 */
	private void show(ResultSet rsServizi, MessageElement risultato) throws SOAPException, SQLException
	{
		
		try
		{
			datiXml.getConvert().addChildElement(risultato, "idUtenteBib", rsServizi.getString("idUtenteBib"), true);
			datiXml.getConvert().addChildElement(risultato, "idAutorizzazioniBib", rsServizi.getString("idAutorizzazioniBib"), true);
			datiXml.getConvert().addChildElement(risultato, "password", rsServizi.getString("password"), true);
			datiXml.getConvert().addChildElement(risultato, "login", rsServizi.getString("login"), false);
			datiXml.getConvert().addChildElement(risultato, "cognome", rsServizi.getString("cognome"), false);
			datiXml.getConvert().addChildElement(risultato, "nome", rsServizi.getString("nome"), false);
			datiXml.getConvert().addChildElement(risultato, "indirizzoIP", rsServizi.getString("indirizzoIP"), false);
		}
		catch (SOAPException e)
		{
			throw e;
		}
		catch (SQLException e)
		{
			throw e;
		}
	}
}
