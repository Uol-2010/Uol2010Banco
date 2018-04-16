/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.Servizi;
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
	public MessageElement show(String id) throws ServletException, IOException
	{
		MessageElement risultati = null;

		risultati = new MessageElement();
		risultati.setName("risultati");
		return show(id, risultati);
	}

	/**
	 * 
	 * @param id
	 * @param disRis
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected MessageElement show(String id, MessageElement risultati) throws ServletException, IOException
	{
		Servizi servizi = null;
		ResultSet rsServizi = null;
		MessageElement risultato = null;

		try
		{
			servizi = new Servizi(Configuration.poolUol2010);
			if (id != null && 
					!id.trim().equals(""))
				servizi.setCampoValue("idServizi", id);
			servizi.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);

			rsServizi = servizi.startSelect();
			while(rsServizi.next())
			{
				if (risultati.getName().equals("risultati"))
				{
					risultato = new MessageElement();
					risultato.setName("risultato");
					show(rsServizi, risultato);
					risultati.addChildElement(risultato);
				}
				else
					show(rsServizi, risultati);
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
			datiXml.getConvert().addChildElement(risultato, "idServizi", rsServizi.getString("idServizi"), true);
			datiXml.getConvert().addChildElement(risultato, "descrizione", rsServizi.getString("descrizione"), true);
			datiXml.getConvert().addChildElement(risultato, "maxMovimenti", rsServizi.getString("maxMovimenti"), false);
			datiXml.getConvert().addChildElement(risultato, "durataMovimenti", rsServizi.getString("durataMovimenti"), false);
			datiXml.getConvert().addChildElement(risultato, "durataDeposito", rsServizi.getString("durataDeposito"), false);
			datiXml.getConvert().addChildElement(risultato, "maxDepositi", rsServizi.getString("maxDepositi"), false);
			datiXml.getConvert().addChildElement(risultato, "ridisponibilitaMateriale", rsServizi.getString("ridisponibilitaMateriale"), false);
			datiXml.getConvert().addChildElement(risultato, "email", rsServizi.getString("email"), false);
			datiXml.getConvert().addChildElement(risultato, "dataIns", rsServizi.getString("dataIns"), false);
			datiXml.getConvert().addChildElement(risultato, "dataMod", rsServizi.getString("dataMod"), false);
			datiXml.getConvert().addChildElement(risultato, "durataRinnovo1", rsServizi.getString("durataRinnovo1"), false);
			datiXml.getConvert().addChildElement(risultato, "durataRinnovo2", rsServizi.getString("durataRinnovo2"), false);
			datiXml.getConvert().addChildElement(risultato, "durataRinnovo3", rsServizi.getString("durataRinnovo3"), false);
			datiXml.getConvert().addChildElement(risultato, "ggdepositi", rsServizi.getString("ggdepositi"), false);
			datiXml.getConvert().addChildElement(risultato, "emailchiamate", rsServizi.getString("emailchiamate"), false);
			datiXml.getConvert().addChildElement(risultato, "sollecito", rsServizi.getString("sollecito"), false);
			datiXml.getConvert().addChildElement(risultato, "ggsollecito", rsServizi.getString("ggsollecito"), false);
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
