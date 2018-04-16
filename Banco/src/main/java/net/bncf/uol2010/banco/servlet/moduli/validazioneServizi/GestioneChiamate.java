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
public class GestioneChiamate extends AuthorityFiles
{

	/**
	 * 
	 */
	public GestioneChiamate()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Gestione Chiamate");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.servizi.tabelleValidazioneServizi.GestioneChiamate(Configuration.poolUol2010);
		primaryKey = "idGestioneChiamate";
		modulo="GestChiamate";
		super.init();
	}

}
