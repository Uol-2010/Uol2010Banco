/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.stdModuli;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

//import antlr.collections.List;
import mx.randalf.hibernate.exception.HibernateUtilException;
import net.bncf.uol2010.database.schema.servizi.dao.AutBibAutUteDAO;
import net.bncf.uol2010.database.schema.servizi.dao.AutBibModAmmDAO;
import net.bncf.uol2010.database.schema.servizi.dao.AutBibServiziDAO;
import net.bncf.uol2010.database.schema.servizi.dao.AutorizzazioniBibDAO;
import net.bncf.uol2010.database.schema.servizi.entity.AutBibAutUte;
import net.bncf.uol2010.database.schema.servizi.entity.AutBibModAmm;
import net.bncf.uol2010.database.schema.servizi.entity.AutBibServizi;
import net.bncf.uol2010.database.schema.servizi.entity.AutorizzazioniBib;
import net.bncf.uol2010.database.schema.servizi.entity.AutorizzazioniUte;
import net.bncf.uol2010.database.schema.servizi.entity.ModuliAmministrazione;
import net.bncf.uol2010.database.schema.servizi.entity.Servizi;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente.Autorizzazioni;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente.Autorizzazioni.Diritto;

/**
 * @author massi
 *
 */
public class GenAutorizzazione {

	/**
	 * Questa variabile viene utilizzata per loggare l'apliczione
	 */
	private static Logger log = Logger.getLogger(GenAutorizzazione.class);

	/**
	 * Questo metodo viene utilizzato per generare le informazioni della
	 * Autorizzazioni
	 * 
	 * @param idAutorizzazione
	 * @return
	 */
	public static Autorizzazioni generate(String descAutorizzazione) {
		Autorizzazioni autorizzazione = null;
		AutorizzazioniBibDAO autorizzazioniBibDAO = null;
		AutorizzazioniBib autorizzazioniBib = null;

		try {
			autorizzazioniBibDAO = new AutorizzazioniBibDAO();
			autorizzazioniBib = autorizzazioniBibDAO.findByDesc(descAutorizzazione);
			if (autorizzazioniBib != null) {
				autorizzazione = new Autorizzazioni();
				autorizzazione.setNome(descAutorizzazione);

				readAutBibUte(autorizzazioniBib, autorizzazione);
				readAutBibServizi(autorizzazioniBib, autorizzazione);
				readAutBibModuli(autorizzazioniBib, autorizzazione);
			}
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			log.error(e.getMessage(), e);
		}
		return autorizzazione;
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative tra
	 * Autorizzazion Bibliotecario e Utente
	 * 
	 * @param idAutorizzazione
	 * @param autorizzazione
	 * @throws HibernateUtilException
	 */
	private static void readAutBibUte(AutorizzazioniBib autorizzazioniBib, Autorizzazioni autorizzazione)
			throws HibernateUtilException {
		AutBibAutUteDAO autBibAutUteDAO = null;
		List<AutBibAutUte> autBibAutUtes = null;

		try {
			autBibAutUteDAO = new AutBibAutUteDAO();
			autBibAutUtes = autBibAutUteDAO.find(autorizzazioniBib, null, null);

			if (autBibAutUtes != null && autBibAutUtes.size() > 0) {
				for (AutBibAutUte autBibAutUte : autBibAutUtes) {
					readAutUte(autorizzazione, autBibAutUte.getIdAutorizzazioniUte());
				}
			}
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni dell'utente
	 * 
	 * @param autorizzazione
	 * @param idAutorizzazioneUte
	 */
	private static void readAutUte(Autorizzazioni autorizzazione, AutorizzazioniUte idAutorizzazioneUte) {
		Diritto diritto = null;

		if (idAutorizzazioneUte != null) {
			diritto = new Diritto();
			diritto.setID(idAutorizzazioneUte.getId());
			diritto.setValue(idAutorizzazioneUte.getDescrizione());
			autorizzazione.getDiritto().add(diritto);
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative tra
	 * Autorizzazion Bibliotecario e Servizi
	 * 
	 * @param idAutorizzazione
	 * @param autorizzazione
	 * @throws HibernateUtilException
	 */
	private static void readAutBibServizi(AutorizzazioniBib idAutorizzazioniBib, Autorizzazioni autorizzazione)
			throws HibernateUtilException {
		AutBibServiziDAO autBibServiziDAO = null;
		List<AutBibServizi> autBibServizis = null;

		try {
			autBibServiziDAO = new AutBibServiziDAO();
			autBibServizis = autBibServiziDAO.find(idAutorizzazioniBib, null, null);

			if (autBibServizis != null && autBibServizis.size() > 0) {
				for (AutBibServizi autBibServizi : autBibServizis) {
					readAutServizi(autorizzazione, autBibServizi.getIdServizi());
				}
			}
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni del servizio
	 * 
	 * @param autorizzazione
	 * @param idServizi
	 */
	private static void readAutServizi(Autorizzazioni autorizzazione, Servizi idServizi) {
		Diritto diritto = null;

		if (idServizi != null) {
			diritto = new Diritto();
			diritto.setID(idServizi.getId());
			diritto.setValue(idServizi.getDescrizione());
			autorizzazione.getDiritto().add(diritto);
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative tra
	 * Autorizzazion Bibliotecario e Moduli
	 * 
	 * @param idAutorizzazione
	 * @param autorizzazione
	 * @throws HibernateUtilException
	 */
	private static void readAutBibModuli(AutorizzazioniBib idAutorizzazioniBib, Autorizzazioni autorizzazione)
			throws HibernateUtilException {
		AutBibModAmmDAO autBibModAmmDAO = null;
		List<AutBibModAmm> autBibModAmms = null;

		try {
			autBibModAmmDAO = new AutBibModAmmDAO();
			autBibModAmms = autBibModAmmDAO.find(idAutorizzazioniBib, null, null);

			if (autBibModAmms != null && autBibModAmms.size() > 0) {
				for (AutBibModAmm autBibModAmm : autBibModAmms) {
					readModuli(autorizzazione, autBibModAmm.getIdModuliAmministrazione());
				}
			}
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni del servizio
	 * 
	 * @param autorizzazione
	 * @param idModuliAmministrazione
	 */
	private static void readModuli(Autorizzazioni autorizzazione, ModuliAmministrazione idModuliAmministrazione) {
		Diritto diritto = null;

		if (idModuliAmministrazione != null) {
			diritto = new Diritto();
			diritto.setID(idModuliAmministrazione.getId());
			diritto.setValue(idModuliAmministrazione.getDescrizione());
			autorizzazione.getDiritto().add(diritto);
		}
	}
}
