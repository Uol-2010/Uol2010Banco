/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.singolaSegnature;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.MsSql;
import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import mx.normalize.ConvertText;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.viewer.amministrazione.segnature.ViewSingolaSegnatura;
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
		ViewSingolaSegnatura  table = null;
		ResultSet rsTable = null;
		MessageElement risultato = null;

		try
		{
			if (id != null && !id.trim().equals(""))
				table = new ViewSingolaSegnatura(Configuration.poolUol2010);
			else
				table = new ViewSingolaSegnatura(Configuration.poolUol2010, request.getParameterMap());
				
			if (id != null && 
					!id.trim().equals(""))
				table.setCampoValue("idSingolaSegnatura", id);
			else
			{
  			if (request.getParameter("Ric_idSingolaSegnatura") != null && 
  					!request.getParameter("Ric_idSingolaSegnatura").equals(""))
  				table.setCampoValue("idSingolaSegnatura", request.getParameter("Ric_idSingolaSegnatura"));
  			else if (request.getParameter("Ric_segnatura") != null &&
  						!request.getParameter("Ric_segnatura").equals(""))
				{
					table.setCampoValue("singolaSegnaturaKey", ConvertText.conveSegna(request.getParameter("Ric_segnatura"))+"%");
					table.getCampo("singolaSegnaturaKey").setTipoRicerca("like");
				}
  			else if (request.getParameter("singolaSegnaturaKey") != null &&
						!request.getParameter("singolaSegnaturaKey").equals(""))
					table.getCampo("singolaSegnaturaKey").setTipoRicerca("like");
			}
			table.getCampo("singolaSegnaturaKey").setOrderBy(Column.ORDERBY_CRES, 1);

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
			datiXml.getConvert().addChildElement(risultato, "idSingolaSegnatura", rsServizi.getString("idSingolaSegnatura"), true);
			datiXml.getConvert().addChildElement(risultato, "disponibilita", rsServizi.getString("descDisponibilita"), "id", rsServizi.getString("idDisponibilita"), true, true);
			datiXml.getConvert().addChildElement(risultato, "singolaSegnatura", rsServizi.getString("singolaSegnatura"), false);
			datiXml.getConvert().addChildElement(risultato, "dataIni", MsSql.conveDateIta(rsServizi.getTimestamp("dataIni")), false);
			datiXml.getConvert().addChildElement(risultato, "dataFin", MsSql.conveDateIta(rsServizi.getTimestamp("dataFin")), false);
			datiXml.getConvert().addChildElement(risultato, "note", rsServizi.getString("note"), false);
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
