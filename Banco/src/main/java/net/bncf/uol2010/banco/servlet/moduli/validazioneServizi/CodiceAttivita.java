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
public class CodiceAttivita extends AuthorityFiles
{

	/**
	 * 
	 */
	public CodiceAttivita()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Codice Attivit&agrave;");
		table = new net.bncf.uol2010.database.table.servizi.tabelleValidazioneServizi.CodiceAttivita(Configuration.poolUol2010);
		primaryKey = "idAttivita";
		modulo="CodAttivita";
		super.init();
	}

}
