/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.validazioneSegnature;

import net.bncf.uol2010.banco.servlet.moduli.stdModuli.AuthorityFiles;
import net.bncf.uol2010.configuration.Configuration;

/**
 * @author MRandazzo
 *
 */
public class Disponibilita extends AuthorityFiles
{

	/**
	 * 
	 */
	public Disponibilita()
	{
	}

	@Override
	protected void init()
	{
		datiXml.setTitle("Disponibilit&agrave; Segnature");
		table = new net.bncf.uol2010.database.table.Disponibilita(Configuration.poolUol2010);
		primaryKey = "idDisponibilita";
		modulo="DispSegna";
		super.init();
	}

}
