/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampante;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.database.table.Column;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.viewer.amministrazione.stampanti.ViewStampanti;
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
		ViewStampanti table = null;
		ResultSet rsTable = null;
		MessageElement risultato = null;

		try
		{
			if (id != null && !id.trim().equals(""))
				table = new ViewStampanti(Configuration.poolUol2010);
			else
				table = new ViewStampanti(Configuration.poolUol2010, request.getParameterMap());
				
			if (id != null && 
					!id.trim().equals(""))
				table.setCampoValue("idStampante", id);
			else
			{
  			if (request.getParameter("Ric_idStampante") != null && 
  					!request.getParameter("Ric_idStampante").equals(""))
  				table.setCampoValue("idStampante", request.getParameter("Ric_idStampante"));
  			else if (request.getParameter("Ric_descrizione") != null &&
  						!request.getParameter("Ric_descrizione").equals(""))
				{
					table.setCampoValue("descrizioneStampante", "%"+request.getParameter("Ric_descrizione")+"%");
					table.getCampo("descrizioneStampante").setTipoRicerca("like");
				}
			}
			table.getCampo("descrizioneStampante").setOrderBy(Column.ORDERBY_CRES, 1);

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
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		try
		{
			datiXml.getConvert().addChildElement(risultato, "idStampante", rsServizi.getString("idStampante"), true);

			keyAttr = new Vector<String>();
			valueAttr = new Vector<String>();
			obbAttr= new Vector<String>();

			keyAttr.add("id");
			valueAttr.add(rsServizi.getString("idStampanteColore"));
			obbAttr.add("true");
			keyAttr.add("fileImg");
			valueAttr.add(rsServizi.getString("fileImg"));
			obbAttr.add("true");
			datiXml.getConvert().addChildElement(risultato, "colore", rsServizi.getString("descrizioneColore"), keyAttr, valueAttr, true, obbAttr);

			keyAttr = new Vector<String>();
			valueAttr = new Vector<String>();
			obbAttr= new Vector<String>();

			keyAttr.add("id");
			valueAttr.add(rsServizi.getString("idStampanteModello"));
			obbAttr.add("true");
			keyAttr.add("fileModello");
			valueAttr.add(rsServizi.getString("fileModello"));
			obbAttr.add("true");
			datiXml.getConvert().addChildElement(risultato, "modello", rsServizi.getString("descrizioneModello"), keyAttr, valueAttr, true, obbAttr);

			datiXml.getConvert().addChildElement(risultato, "descrizione", rsServizi.getString("descrizioneStampante"), true);
			datiXml.getConvert().addChildElement(risultato, "codaStampante", rsServizi.getString("codaStampante"), true);
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
