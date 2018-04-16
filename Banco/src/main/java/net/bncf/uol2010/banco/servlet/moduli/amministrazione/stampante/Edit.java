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
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.stampante.edit.StampantiAutorizzazioni;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.amministrazione.stampanti.StampanteColore;
import net.bncf.uol2010.database.table.amministrazione.stampanti.StampanteModello;
import net.bncf.xsd.BancoXsd;

import org.apache.axis.message.MessageElement;

/**
 * @author massi
 *
 */
public class Edit extends Show
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(Edit.class,
			"net.bncf.uol2010.banco.servlet.moduli");

	/**
	 * Costruttore
	 */
	public Edit(DatiXml datiXml, BancoXsd bancoXsd)
	{
		super(datiXml, bancoXsd);
	}

	/**
	 * Questo metodo viene utilizzato per trovare le informazioni necesarie per
	 * l'edito del mag
	 * 
	 * @param idUtente
	 *          Identificativo Urente
	 */
	public void edit(MxMultipartRequest request, MessageElement show, String idAutorizzazioniBib, MessageElement edit, String modulo) throws ServletException, IOException
	{

		try
		{
			this.show(request, show, idAutorizzazioniBib, edit, modulo);
			if (edit.hasChildNodes())
			{
				// Stiamo eseguendo una modifica di un servizio esistente
				StampantiAutorizzazioni.read(idAutorizzazioniBib, edit, datiXml);
			}
		}
		catch (ServletException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	public void readStampanteColore(MessageElement edit) throws ServletException
	{
		StampanteColore table = null;
		ResultSet rs = null;
		MessageElement stampantiColore = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			stampantiColore = new MessageElement();
			stampantiColore.setName("stampantiColore");
			table = new StampanteColore(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rs = table.startSelect();
			while(rs.next())
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rs.getString("idStampanteColore"));
				obbAttr.add("true");

				keyAttr.add("fileImg");
				valueAttr.add(rs.getString("fileImg"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(stampantiColore, 
						"stampanteColore", 
						rs.getString("descrizione"), 
						keyAttr, valueAttr, true, obbAttr);
			}
			edit.addChildElement(stampantiColore);
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
	}

	public void readStampanteModello(MessageElement edit) throws ServletException
	{
		StampanteModello table = null;
		ResultSet rs = null;
		MessageElement stampantiModelli = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;
		
		try
		{
			stampantiModelli = new MessageElement();
			stampantiModelli.setName("stampantiModelli");
			table = new StampanteModello(Configuration.poolUol2010);
			table.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rs = table.startSelect();
			while(rs.next())
			{
				keyAttr = new Vector<String>();
				valueAttr = new Vector<String>();
				obbAttr = new Vector<String>();

				keyAttr.add("id");
				valueAttr.add(rs.getString("idStampanteModello"));
				obbAttr.add("true");

				keyAttr.add("fileModello");
				valueAttr.add(rs.getString("fileModello"));
				obbAttr.add("true");

				datiXml.getConvert().addChildElement(stampantiModelli, 
						"stampanteModello", 
						rs.getString("descrizione"), 
						keyAttr, valueAttr, true, obbAttr);
			}
			edit.addChildElement(stampantiModelli);
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
	}
}
