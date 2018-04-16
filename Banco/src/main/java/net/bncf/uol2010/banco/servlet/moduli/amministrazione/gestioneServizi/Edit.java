/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.AutorizzazioniBibliotecario;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.AutorizzazioniUtente;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.Calendario;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.gestioneServizi.edit.IterServ;
import net.bncf.xsd.BancoXsd;

import org.apache.axis.message.MessageElement;

/**
 * @author massi
 *
 */
public class Edit extends Show
{

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
	public void edit(String idServizio, MessageElement edit) throws ServletException, IOException
	{

		try
		{
			this.show(idServizio, edit);
			if (edit.hasChildNodes())
			{
				// Stiamo eseguendo una modifica di un servizio esistente
				datiXml.getConvert().addChildElement(edit, "listaFruibilita", "listaFruibilita", true);
//				Fruibilita.readFruibilita(idServizio, edit, datiXml);
				datiXml.getConvert().addChildElement(edit, "controlli", "controlli", true);
//				Controlli.readControlli(idServizio, edit, datiXml);
				AutorizzazioniUtente.readAutiorizzazioniUtente(idServizio, edit, datiXml);
				AutorizzazioniBibliotecario.readAutiorizzazioniBibliotecario(idServizio, edit, datiXml);
				Calendario.readCalendarioSettimanale(idServizio, edit, datiXml);
				Calendario.readCalendarioEccezioni(idServizio, edit, datiXml);
				Calendario.readCalendarioSospensioni(idServizio, edit, datiXml);
				IterServ.readIterServizio(idServizio, edit, datiXml);
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
		catch (SOAPException e)
		{
			throw new ServletException(e.getMessage());
		}
	}
}
