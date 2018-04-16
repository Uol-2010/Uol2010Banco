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
public class StatoIter extends AuthorityFiles
{

	/**
	 * 
	 */
	public StatoIter()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Stato Iter");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.servizi.tabelleValidazioneServizi.StatoIter(Configuration.poolUol2010);
		primaryKey = "idStatoIter";
		modulo="StatoIter";
		super.init();
	}

}
