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
public class StatoMovimento extends AuthorityFiles
{

	/**
	 * 
	 */
	public StatoMovimento()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Stato Movimento");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.servizi.tabelleValidazioneServizi.StatoMovimento(Configuration.poolUol2010);
		primaryKey = "idStatoMovimenti";
		modulo="StatoMovimento";
		super.init();
	}

}
