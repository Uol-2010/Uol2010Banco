/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.soap.SOAPException;

import mx.database.MsSqlException;
import mx.log4j.Logger;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.servizi.IterServizioLegami;

import org.apache.axis.message.MessageElement;

/**
 * @author MRandazzo
 *
 */
public class IterServLegami
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(IterServ.class,
			"net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente");

	public static void delete(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		IterServizioLegami table = null;
		
		try
		{
			table = new IterServizioLegami(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("progIterPar", request.getParameter("progIterPar"));
			table.setCampoValue("progIterArr", request.getParameter("progIterArr"));
			if (table.delete()>0)
			{
				datiXml.getConvert().addChildElement(element, "progIterPar", request.getParameter("progIterPar"), true);
				datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
			}
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			datiXml.addElement(element);
		}
	}

	/**
	 * Questo metodo viene utilizzato per aggiornare la fruibilita del servizio
	 */
	public static void write(MxMultipartRequest request, DatiXml datiXml, MessageElement element)
	{
		IterServizioLegami table = null;
		ResultSet rs = null;
		
		try
		{
			table = new IterServizioLegami(Configuration.poolUol2010);
			table.setCampoValue("idServizi", request.getParameter("idServizi"));
			table.setCampoValue("progIterPar", request.getParameter("progIterPar"));
			table.setCampoValue("progIterArr", request.getParameter("progIterArr"));

			rs = table.startSelect();
			table.setCampoValue("idGestioneChiamate", request.getParameter("idGestioneChiamate"));
			table.setCampoValue("idGestioneDeposito", request.getParameter("idGestioneDeposito"));
			table.setCampoValue("tipoServizio", request.getParameter("tipoServizio"));
			if (rs.next())
				table.update();
			else
				table.insert();
				
			datiXml.getConvert().addChildElement(element, "progIterPar", table.get("progIterPar"), true);
			datiXml.getConvert().addChildElement(element, "progIterArr", request.getParameter("descProgIterArr"), "id", request.getParameter("progIterArr"), true, true);
			datiXml.getConvert().addChildElement(element, "gestioneChiamate", request.getParameter("descGestioneChiamate"), "id", request.getParameter("idGestioneChiamate"), true, true);
			datiXml.getConvert().addChildElement(element, "gestioneDeposito", request.getParameter("descGestioneDeposito"), "id", request.getParameter("idGestioneDeposito"), true, true);
			datiXml.getConvert().addChildElement(element, "tipoServizio", request.getParameter("tipoServizio"), false);
			datiXml.getConvert().addChildElement(element, "posizione", request.getParameter("posizione"), true);
			
		}
		catch (MsSqlException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		catch (SQLException e)
		{
			log.error(e);
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
			}
			finally
			{
				datiXml.addElement(element);
			}
		}
	}
}
