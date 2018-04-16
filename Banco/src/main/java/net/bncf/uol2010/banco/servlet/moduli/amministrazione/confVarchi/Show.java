/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.confVarchi;

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
import net.bncf.uol2010.database.table.ingressi.ConfVarchi;
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
		ConfVarchi table = null;
		ResultSet rsTable = null;
		MessageElement risultato = null;

		try
		{
			if (id != null && !id.trim().equals(""))
				table = new ConfVarchi(Configuration.poolUol2010);
			else
				table = new ConfVarchi(Configuration.poolUol2010, request.getParameterMap());
				
			if (id != null && 
					!id.trim().equals(""))
				table.setCampoValue("idConfVarchi", id);
			else
			{
  			if (request.getParameter("Ric_idConfVarchi") != null && 
  					!request.getParameter("Ric_idConfVarchi").equals(""))
  				table.setCampoValue("idConfVarchi", request.getParameter("Ric_idConfVarchi"));
  			else if (request.getParameter("Ric_descrizione") != null &&
  						!request.getParameter("Ric_descrizione").equals(""))
				{
					table.setCampoValue("descr", "%"+request.getParameter("Ric_descrizione")+"%");
					table.getCampo("descr").setTipoRicerca("like");
				}
			}
			table.getCampo("descr").setOrderBy(Column.ORDERBY_CRES, 1);

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
			datiXml.getConvert().addChildElement(risultato, "idConfVarchi", rsServizi.getString("idConfVarchi"), true);
			datiXml.getConvert().addChildElement(risultato, "descrizione", rsServizi.getString("descr"), true);
			datiXml.getConvert().addChildElement(risultato, "idVarco", rsServizi.getString("idVarco"), false);
			datiXml.getConvert().addChildElement(risultato, "idLettore", rsServizi.getString("idLettore"), false);
			datiXml.getConvert().addChildElement(risultato, "flgUtPre", rsServizi.getString("flgUtPre"), false);
			datiXml.getConvert().addChildElement(risultato, "flgTeSca", rsServizi.getString("flgTeSca"), false);
			datiXml.getConvert().addChildElement(risultato, "flgTeSos", rsServizi.getString("flgTeSos"), false);
			datiXml.getConvert().addChildElement(risultato, "flgUtNonPre", rsServizi.getString("flgUtNonPre"), false);
			datiXml.getConvert().addChildElement(risultato, "flgUtPres", rsServizi.getString("flgUtPres"), false);
			datiXml.getConvert().addChildElement(risultato, "flgLibPre", rsServizi.getString("flgLibPre"), false);
			datiXml.getConvert().addChildElement(risultato, "listaServizi", rsServizi.getString("listaServizi"), false);
			datiXml.getConvert().addChildElement(risultato, "msgUtPre", rsServizi.getString("msgUtPre"), false);
			datiXml.getConvert().addChildElement(risultato, "msgTeSca", rsServizi.getString("msgTeSca"), false);
			datiXml.getConvert().addChildElement(risultato, "msgTeSos", rsServizi.getString("msgTeSos"), false);
			datiXml.getConvert().addChildElement(risultato, "msgUtNonPre", rsServizi.getString("msgUtNonPre"), false);
			datiXml.getConvert().addChildElement(risultato, "msgUtPres", rsServizi.getString("msgUtPres"), false);
			datiXml.getConvert().addChildElement(risultato, "msgLibPre", rsServizi.getString("msgLibPre"), false);
			datiXml.getConvert().addChildElement(risultato, "tipoVarco", rsServizi.getString("tipoVarco"), false);
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
