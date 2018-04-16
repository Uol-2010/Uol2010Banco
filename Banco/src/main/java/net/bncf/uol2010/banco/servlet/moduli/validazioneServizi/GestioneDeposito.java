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
public class GestioneDeposito extends AuthorityFiles
{

	/**
	 * 
	 */
	public GestioneDeposito()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Gestione Deposito");
		table = new net.bncf.uol2010.banco.servlet.moduli.validazioneServizi.database.table.servizi.tabelleValidazioneServizi.GestioneDeposito(Configuration.poolUol2010);
		primaryKey = "idGestioneDeposito";
		modulo="GestDeposito";
		super.init();
	}

}
