/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib;

import java.io.IOException;

import javax.servlet.ServletException;

import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.edit.AutBibAutUte;
import net.bncf.uol2010.banco.servlet.moduli.amministrazione.autorizzazioniBib.edit.AutBibModAmm;
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
	public void edit(MxMultipartRequest request, MessageElement show, String idAutorizzazioniBib, MessageElement edit, String modulo) throws ServletException, IOException
	{

		try
		{
			this.show(request, show, idAutorizzazioniBib, edit, modulo);
			if (edit.hasChildNodes())
			{
				// Stiamo eseguendo una modifica di un servizio esistente
				AutBibAutUte.read(idAutorizzazioniBib, edit, datiXml);
				AutBibModAmm.read(idAutorizzazioniBib, edit, datiXml);
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
}
