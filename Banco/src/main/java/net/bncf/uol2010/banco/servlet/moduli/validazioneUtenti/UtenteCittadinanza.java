/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.validazioneUtenti;

import net.bncf.uol2010.banco.servlet.moduli.stdModuli.AuthorityFiles;
import net.bncf.uol2010.configuration.Configuration;

/**
 * @author MRandazzo
 *
 */
public class UtenteCittadinanza extends AuthorityFiles
{

	/**
	 * 
	 */
	public UtenteCittadinanza()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Cittadinanza Utente");
		table = new net.bncf.uol2010.database.table.utenti.tabelleValidazioneUtente.UtenteCittadinanza(Configuration.poolUol2010);
		primaryKey = "idUtenteCittadinanza";
		modulo="UteCit";
		super.init();
	}

}
