/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.moduli;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import mx.database.MsSql;
import mx.database.MsSqlException;
import mx.log4j.Logger;
import net.bncf.uol2010.banco.email.SendMail;
import net.bncf.uol2010.banco.security.exception.SecurityException;
import net.bncf.uol2010.banco.servlet.Banco;
import net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.implement.ActionImp;
import net.bncf.uol2010.banco.utility.GestData;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.uol2010.database.table.richieste.Richieste;
import net.bncf.uol2010.database.viewer.servizi.ViewListaAttivita;

/**
 * @author massi
 * 
 */
public class ChangeAtti extends ActionImp {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(CheckMovi.class,
			"net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.moduli");

	/**
	 * Questa variabile viene utilizzata indicare il risultato del metodo esegui
	 */
	protected boolean risEsegui = false;

	/**
	 * 
	 */
	public ChangeAtti() {
		super();
	}

	/**
	 * Questo metodo viene utilizzato per la gestione della azione
	 * 
	 * @throws SecurityException
	 * 
	 * @see net.bncf.banco.implement.ActionImp#esegui()
	 */
	public void esegui() throws SecurityException, SecurityException {
		log.debug("esegui");
		if (actionResult.getRequest().getParameter("progrIter") != null) {
			log.debug("Progr Iter: "
					+ actionResult.getRequest().getParameter("progrIter"));
			super.esegui();

			// scrivo aggiorno il movimento
			writeMov();

			// Visualizzo le informazioni relative all'utente e all'opera
			// richiesta
			this.viewNotizia();

		}
		actionResult.setViewNumMov(true);
	}

	/**
	 * Questo metodo viene utilizzato per scrivere le modifice sul record dei
	 * movimenti
	 * 
	 */
	private void writeMov() {
		log.debug("writeMov");

		// Hashtable datimov = new Hashtable();
		ViewListaAttivita attivita = null;

		log.debug("Note: " + actionResult.getRequest().getParameter("note"));
		if (actionResult.getRequest().getParameter("note") == null)
			infoRic.setCampoValue("noteBib", "");
		else
			infoRic.setCampoValue("noteBib", actionResult.getRequest()
					.getParameter("note"));

		// Aggiornamento informazioni relativo alle attivita'
		attivita = new ViewListaAttivita(Configuration.poolUol2010);

		attivita.readatti(actionResult.getCodBib(), infoRic.get("idServizi"),
				infoRic.getIntCampoValue("progIter"), infoRic.getSoloFinale(),
				actionResult.getRequest().getParameter("progrIter"), infoRic);

		infoRic.setCampoValue("progIter", Integer.parseInt(actionResult
				.getRequest().getParameter("progrIter")));

		caldata();

		log.debug("Check Deposito: " + infoRic.getCheckDeposito());
		if (infoRic.getCheckDeposito() != null) {
			if (infoRic.getCheckDeposito().equals("Y")) {
				gestAtti();

				writemod();
			}
		}

		if (infoRic.getCheckDeposito().equals("N")) {
			log.debug("Numero Massimo depositi superato");
			actionResult.addError("Numero Massimo depositi superato");
		} else {
			log.debug("Data Fine Eff: " + infoRic.get("dataFinEff"));
			actionResult.setScadenza((Timestamp) infoRic.getCampo("dataFinEff")
					.getValue());
			risEsegui = true;
		}
	}

	/**
	 * Questo metodo a il compito di registrare le modifiche dei movimenti
	 */
	private void writemod() {
		Richieste richieste = null;

		try {
			// per prima cosa disattivo l'autocommit cosi' nel caso in cui
			// qualcosa non andasse come dovrebbe posso tornare indietro.

			richieste = new Richieste(Configuration.poolUol2010);
			richieste
					.setCampoValue("dataMod", infoRic.getCampoValue("dataMod"));
			richieste.setCampoValue("dataIniEff", infoRic
					.getCampoValue("dataIniEff"));
			richieste.setCampoValue("dataFinEff", infoRic
					.getCampoValue("dataFinEff"));
			richieste.setCampoValue("idStatoMovimenti", infoRic
					.getCampoValue("idStatoMovimenti"));
			richieste.setCampoValue("progIter", infoRic
					.getCampoValue("progIter"));
			richieste
					.setCampoValue("noteBib", infoRic.getCampoValue("noteBib"));
			richieste.setCampoValue("numRinnovi", infoRic
					.getCampoValue("numRinnovi"));
			richieste.setWhereValue("idRichieste", infoRic
					.getCampoValue("idRichieste"));
			richieste.setCampoValue("dataIniPrev", infoRic
					.getCampoValue("dataIniPrev"));
			richieste.setCampoValue("dataFinPrev", infoRic
					.getCampoValue("dataFinPrev"));
			richieste.update();
		} catch (MsSqlException e) {
			log.error(e);
		}
	}

	/**
	 * Procedura per la gestione delle attivita'
	 */
	private void gestAtti() {
		Hashtable<String, Object> newChiam = new Hashtable<String, Object>();
		try {
			// Controllo Procedura Chiamate
			log.debug("Chiamate = " + (String) infoRic.getChiamate());
			if (infoRic.getChiamate() != null
					&& (!infoRic.getChiamate().equals("N"))) {
				if (((String) Configuration.listaParametri.get(
						"banco.chiamate", "false")).equalsIgnoreCase("true")) {
					newChiam.put("chiamate", infoRic.getChiamate());
					newChiam.put("cognome_nome", infoRic.getCognomeNome());
					newChiam.put("cod_bib_ut", infoRic.get("idUtente")
							.substring(0, 3));
					newChiam.put("cod_utente", infoRic.get("idUtente")
							.substring(3));
					newChiam.put("tipo", infoRic.getColore().substring(0, 1));
					Banco.chiamate.add(infoRic.get("idRichieste"), newChiam);
				}

				if (((Boolean) infoRic.getCampo("notificaStatoRichiesta")
						.getValue()).booleanValue()
						&& !infoRic.get("emailStatoRichiesta").equals("")) {
					
					if (infoRic.getEmailchiamate().equals("1")){
//					if (((String) Configuration.listaParametri.get(
//							"banco.sendEmail", "false"))
//							.equalsIgnoreCase("true")) {
						if (infoRic.getChiamate().equals("S"))
							SendMail.sendMailChiamatePositiva(infoRic);
						else if (infoRic.getChiamate().equals("O"))
							SendMail.sendMailChiamateNegativo(infoRic);

					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Questo metodoviene utilizzato per calcolare la nuova data
	 * 
	 */
	private void caldata() {
		// questa funzione serve per calcolare la data di riconsegna del
		// materiale
		try {
			log.debug("caldata");
			GestData myDate = new GestData();

			infoRic.setCheckDeposito("Y");
			infoRic.setCheckMaxCon("Y");
			infoRic.setCampoValue("dataMod", myDate.getTimestamp());

			log.debug("Stato_iter = " + infoRic.getStatoIter());
			log.debug("Rinnovo = " + infoRic.getRinnovo());
			log.debug("deposito = " + infoRic.getDeposito());
			if (infoRic.getStatoIter().equals("I")) {
				log.debug("Inizio del servizio");
				// Questa nuova attivita' che indica l'inizio del servizio
				infoRic.setCampoValue("dataIniEff", myDate.getTimestamp());
				// datimov.put("data_in_prev", myDate.getTimestamp());
			}

			if (infoRic.getStatoIter().equals("F")) {
				log.debug("Fine del servizio");
				// Queusta attivita' porta alla conclusione del servizio di
				// conseguenza indico le dati di fine movimentazione uguale a
				// quelle
				// attuali
				infoRic.setCampoValue("dataFinEff", myDate.getTimestamp());
				infoRic.setCampoValue("dataFinPrev", myDate.getTimestamp());
			} else {
				log.debug("Rinnovo: " + infoRic.getRinnovo());
				if (infoRic.getRinnovo() != null
						&& (infoRic.getRinnovo().equals("S") || infoRic
								.getRinnovo().equals("1"))) {
					// L'attivita' che stiamo gestendo e' di rinnovo
					log.debug("Gestisco un rinnovo");
					GregorianCalendar fine_eff = new GregorianCalendar();
					fine_eff.setTimeInMillis(((Timestamp) infoRic
							.getCampoValue("dataFinEff")).getTime());

					log.debug("La vecchia data era :"
							+ MsSql.conveDateIta(fine_eff));
					myDate = new GestData(fine_eff.get(Calendar.YEAR), fine_eff
							.get(Calendar.MONTH), fine_eff
							.get(Calendar.DAY_OF_MONTH));

					int num_rin = infoRic.getIntCampoValue("numRinnovi") + 1;
					if (num_rin < 4) {
						myDate.add(infoRic.getDurataRinnovo(num_rin), infoRic
								.get("idServizi"));
					}
					infoRic.setCampoValue("numRinnovi", Integer
							.toString(num_rin));
					log
							.debug("La nuova data e' :"
									+ MsSql.conveDateIta(myDate));
					infoRic.setCampoValue("dataFinEff", myDate.getTimestamp());
					infoRic.setCampoValue("dataFinPrev", myDate.getTimestamp());
				} else if ((infoRic.getDeposito() != null && infoRic
						.getDeposito().equals("N"))
						&& (infoRic.getStatoIter() != null && infoRic
								.getStatoIter().equals("I"))) {
					// L'attivita' che stiamo gestendo non gestisce il deposito
					// ed
					// indica l'inizio del servizio e di conseguenza aggiungo i
					// giorni
					// normali per questo tipo di servizio
					log.debug("Nuovo movimento senza deposito");
					log.debug("Durata Movimento = " + infoRic.getDurMov());
					myDate.add(infoRic.getDurMov(), infoRic.get("idServizi"));
					infoRic.setCampoValue("dataFinEff", myDate.getTimestamp());
					infoRic.setCampoValue("dataFinPrev", myDate.getTimestamp());
				} else if (infoRic.getDeposito() != null
						&& infoRic.getDeposito().equals("I")) {
					// Viene messo il materiale in deposito di conseguenza
					// assegno come
					// data di inizio presunta quella attuale e come data di
					// fine
					// quella attuale + i giorni di deposito
					// datimov.put("data_in_eff", myDate.getTimestamp());
					log.debug("Inizio Test del Deposito");
					if (checkDeposito()) {
						log.debug("Controllo Deposito Positivo");
						myDate.add(infoRic.getMaxGgDep(), infoRic
								.get("idServizi"));
						infoRic.setCampoValue("dataFinEff", myDate
								.getTimestamp());
						infoRic.setCampoValue("dataFinPrev", myDate
								.getTimestamp());
					} else {
						log.debug("Controllo Deposito Negativo");
						infoRic.setCheckDeposito("N");
					}
				} else if (infoRic.getDeposito() != null
						&& infoRic.getDeposito().equals("F")) {
					// Viene ritirato il materiale dal deposito ed riconsegnato
					// all'utente di conseguenza assegno come data di inizio
					// presunta
					// quella attuale e come data di fine quella attuale + i
					// giorni
					// normali del servizio.

					// datimov.put("data_in_eff", myDate.getTimestamp());
					myDate.add(infoRic.getDurMov(), infoRic.get("idServizi"));
					infoRic.setCampoValue("dataFinEff", myDate.getTimestamp());
					infoRic.setCampoValue("dataFinPrev", myDate.getTimestamp());
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Questo metodo a il compitom di controlare se la richiesta di messa in
	 * deposito � possibile evaderla controllando che non sia superato il numero
	 * Max di libri in deposito.
	 */
	private boolean checkDeposito() {
		boolean risulta = true;
		Richieste richieste = null;
		ResultSet rsRichieste = null;

		try {
			richieste = new Richieste(Configuration.poolUol2010);
			richieste.setCampoTipoRicerca("idRichieste", "<>");
			richieste.setCampoValue("idRichieste", infoRic
					.getCampoValue("idRichieste"));
			richieste.setCampoValue("idServizi", infoRic
					.getCampoValue("idServizi"));
			richieste.setCampoValue("idUtente", infoRic
					.getCampoValue("idUtente"));
			richieste.setCampoValue("idStatoMovimenti", "D");

			rsRichieste = richieste.startSelect();
			if (richieste.getRecTot() >= infoRic.getNumMaxDepositi()) {
				risulta = false;
			}

		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (rsRichieste != null)
					rsRichieste.close();
				if (richieste != null)
					richieste.stopSelect();
			} catch (SQLException e) {
				log.error(e);
			}
		}
		return risulta;
	}

}
