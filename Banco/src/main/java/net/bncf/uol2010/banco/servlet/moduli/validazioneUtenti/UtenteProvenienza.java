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
public class UtenteProvenienza extends AuthorityFiles
{

	/**
	 * 
	 */
	public UtenteProvenienza()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Provenienza Utente");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneUtenti.database.table.utenti.tabelleValidazioneUtente.UtenteProvenienza(Configuration.poolUol2010);
		primaryKey = "idUtenteProvenienza";
		modulo="UteProvenienza";
		super.init();
	}

}
