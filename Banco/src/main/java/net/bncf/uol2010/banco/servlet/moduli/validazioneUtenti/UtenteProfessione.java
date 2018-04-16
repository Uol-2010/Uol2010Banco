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
public class UtenteProfessione extends AuthorityFiles
{

	/**
	 * 
	 */
	public UtenteProfessione()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Professione Utente");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneUtenti.database.table.utenti.tabelleValidazioneUtente.UtenteProfessione(Configuration.poolUol2010);
		primaryKey = "idUtenteProfessione";
		modulo="UteProfessione";
		super.init();
	}

}
