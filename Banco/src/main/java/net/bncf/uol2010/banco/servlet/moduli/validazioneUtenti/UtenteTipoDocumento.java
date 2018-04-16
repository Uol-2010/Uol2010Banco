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
public class UtenteTipoDocumento extends AuthorityFiles
{

	/**
	 * 
	 */
	public UtenteTipoDocumento()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Tipo Documento Utente");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneUtenti.database.table.utenti.tabelleValidazioneUtente.UtenteTipoDocumento(Configuration.poolUol2010);
		primaryKey = "idUtenteTipoDocumento";
		modulo="UteTipoDoc";
		super.init();
	}

}
