/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.validazioneServizi;

import net.bncf.uol2010.banco.servlet.moduli.stdModuli.AuthorityFiles;
import net.bncf.uol2010.configuration.Configuration;

/**
 * @author MRandazzo
 *
 */
public class Fruibilita extends AuthorityFiles
{

	/**
	 * 
	 */
	public Fruibilita()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Fruibilit&agrave;");
		table = new net.bncf.uol2010.banco.servlet.moduli.net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.Fruibilita(Configuration.poolUol2010);
		primaryKey = "idFruibilita";
		modulo="Fruibilita";
		super.init();
	}

}
