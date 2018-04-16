/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.moduli;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import mx.database.MsSql;
import mx.log4j.Logger;
import net.bncf.uol2010.banco.gestioneFunzioni.TastoFunzione;
import net.bncf.uol2010.banco.security.Security;
import net.bncf.uol2010.banco.security.exception.SecurityException;
import net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.implement.ActionImp;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.viewer.servizi.ViewListaAttivita;

/**
 * @author massi
 * 
 */
public class CheckMovi extends ActionImp {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(CheckMovi.class,
			"net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.moduli");

	private Security security = null;

	/**
	 * 
	 */
	public CheckMovi() {
	}

	/**
	 * Questo metodo viene utilizzato per la gestione della azione
	 * 
	 * @see net.bncf.banco.implement.ActionImp#esegui()
	 */
	public void esegui() throws SecurityException {
		log.debug("Esegui");
		super.esegui();
		security = new Security(actionResult.getRequest(),
				"BNCF_AuthenticationBanco");
		esistMovi();
		log.debug("Esegui Fine");
	}

	/**
	 * Questo metodo viene utilizzato per verificare se esiste il movimento in
	 * Base Dati e se questo risulta attivo
	 * 
	 * @throws SecurityException
	 */
	private void esistMovi() throws SecurityException {
		log.debug("esistMovi");
		if (ifExistMov()) {
			log.debug("Il movimento esiste");
			// Controlla lo stato della richiesta.
			int num_atti = eleatti();

			/*
			 * TODO: Gestione della sicurezza da implementare //
			 * //logS(mess+"cod_tipo_serv  "+ (String)
			 * datimov.get("cod_tipo_serv")); Sec.checkAut((String)
			 * datimov.get("cod_tipo_serv"));
			 */

			log.debug("num Attivit&agrave;: " + num_atti);
			if (num_atti == 0) {
				log.debug("Impossibile trovare nuove attivit&agrave; per questo movimento");
				actionResult.setViewTastiFunzione(false);
				actionResult.setViewNumMov(true);
				actionResult
						.addError("Impossibile trovare nuove attivit&agrave; per questo movimento");
			} else
				actionResult.setViewTastiFunzione(true);
		} else
			log.debug("Il movimento non esiste");
	}

	/**
	 * Questo metodo viene utilizzato per leggere l'elenco delle attivit�
	 * disponibili
	 * 
	 * @param cod_mov_serv
	 * @return
	 */
	private int eleatti() {
		Hashtable<String, Hashtable<String, String>> ele_Atti = new Hashtable<String, Hashtable<String,String>>();
		String keys;
		int fkey = 0;
		ViewListaAttivita atti = null;

		atti = new ViewListaAttivita(Configuration.poolUol2010);
		ele_Atti = atti.readatti(actionResult.getCodBib(),
				infoRic.get("idServizi"), infoRic.getIntCampoValue("progIter"),
				infoRic.getSoloFinale());

		if (ele_Atti.size() > 0) {
			// Nel caso in cui le attivita' trovate sono piu' di una visualizzo
			// l'elenco delle attivta'
			for (Enumeration<String> e = ele_Atti.keys(); e.hasMoreElements();) {
				keys = (String) (e.nextElement());
				fkey++;
				Hashtable<String, String> attivita = (Hashtable<String, String>) ele_Atti.get(keys);

				TastoFunzione tastoFunzione = null;

				tastoFunzione = new TastoFunzione("F" + fkey,
						(String) (attivita.get("descr_atti")),
						Integer.parseInt(keys), 0, false);
				tastoFunzione.addAction("changeAtti");
				actionResult.addTastiFunzione(tastoFunzione);
			}
		}
		return ele_Atti.size();
	}

	/**
	 * Questo metodo viene utilizzato per verificare se Esiste il movimento
	 * 
	 */
	private boolean ifExistMov() {
		boolean ris = false;
		GregorianCalendar dataIni = new GregorianCalendar();
		GregorianCalendar dataFin = new GregorianCalendar();
		GregorianCalendar dataAtt = new GregorianCalendar();

		try {
			log.debug("ifExistsMov");

			if (!infoRic.isExist()) {
				log.debug("Il movimento " + infoRic.get("idRichieste")
						+ " non &egrave; presente nella base");
				actionResult.addError("Il movimento "
						+ infoRic.get("idRichieste")
						+ " non &egrave; presente nella base");
			} else {
				if (!security.checkServizi(infoRic.get("idServizi")))
					throw new SecurityException(
							8,
							"L'operatore non &egrave; autorizzato a gestire questo movimento",
							infoRic.get("idServizi"));

				if (infoRic.getStatoIter().equals("F")) {
					log.debug("Opera gi&agrave; Restituita mov. "
							+ infoRic.get("idRichieste"));
					actionResult.addError("Opera gi&agrave; Restituita mov. "
							+ infoRic.get("idRichieste"));
					actionResult.setViewNumMov(true);
				} else if (infoRic.getRichiesteAttive() >= infoRic
						.getNumMaxMovimenti()) {
					log.debug("Impossibile consegnare pi&ugrave; di "
							+ infoRic.getNumMaxMovimenti()
							+ " contemporaneamente");
					actionResult
							.addError("Impossibile consegnare pi&ugrave; di "
									+ infoRic.getNumMaxMovimenti()
									+ " contemporaneamente");
					actionResult.setViewNumMov(true);
				} else {
					log.debug("Per ora � tutto OK");
					if (infoRic.getCampoValue("dataIniEff") == null)
						dataIni.setTimeInMillis(((Timestamp) infoRic
								.getCampoValue("dataIniPrev")).getTime());
					else
						dataIni.setTimeInMillis(((Timestamp) infoRic
								.getCampoValue("dataIniEff")).getTime());
					dataFin.setTimeInMillis(((Timestamp) infoRic
							.getCampoValue("dataFinPrev")).getTime());
					if (dataAtt.before(dataIni)) {
						log.debug("Opera non &egrave; movimentabile prima del "
								+ MsSql.conveDateIta(dataIni));
						actionResult
								.addError("Opera non &egrave; movimentabile prima del "
										+ MsSql.conveDateIta(dataIni));
						viewNotizia();
					} else if (dataAtt.after(dataFin)) {
						log.debug("Movimento scaduto il "
								+ MsSql.conveDateIta(dataFin));
						actionResult.addError("Movimento scaduto il "
								+ MsSql.conveDateIta(dataFin));
						ris = true;
					} else {
						log.debug("Tutto OK");
						ris = true;
					}
				}
			}

			if (!ris)
				actionResult.setViewNumMov(true);
			else
				viewNotizia();
		} catch (SecurityException e) {
			actionResult.setViewNumMov(true);
			actionResult.addError(e.getMsgError());
		}

		return ris;
	}

}
