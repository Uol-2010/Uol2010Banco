/**
 * 
 */
package net.bncf.uol2010.banco.chiamate;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import mx.randalf.socket.client.Client;

/**
 * @author Randazzo
 * 
 */
public class Chiamate extends Thread {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(Chiamate.class);

	/**
	 * Questa variabile viene utilizzata per indicare se il ciclo di verifica �
	 * attivo
	 */
	private boolean ciclo;

	/**
	 * Questa variabile viene utilizzata per indicare il nome del Server da
	 * contattare
	 */
	private String server;

	/**
	 * Questa variabile viene utilizzata per indicare la porta del Server da
	 * contattare
	 */
	private int port;

	private Hashtable<String, Hashtable<String, Object>> chiamate = null;

	/**
	 * Costruttore
	 */
	public Chiamate() {
		super();
		chiamate = new Hashtable<String, Hashtable<String,Object>>();
		ciclo = true;

	}

	/**
	 * Questo metodo viene invocato quando viene lanciato il Thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Client myCon = null;
		String Linea;
		String cKey;
		Hashtable<String, Object> myDati;

		try {
			myCon = new Client();

			// Inizio il Ciclo di controllo per inviare le informazioni al
			// client
			// del modulo di Chiamate.
			while (ciclo) {
				// Controllo se ci sono nuove informazioni da spedire
				if (chiamate.size() > 0) {
					log.debug("Ci sono Chiamate da evadere");
					try {
						// Apro la connesione con il Client
						log.debug("Apro la connessione al server [" + server
								+ ":" + Integer.toString(port) + "]");
						if (myCon.Connect(server, port)) {
							log.debug("Connessione Aperta rimango in attesa del messaggio di ben venuto");
							Linea = myCon.Recive();
							if (!Linea.equals("")) {
								log.debug("Messaggio ricevuto invio i dati al server");
								for (Enumeration<String> x = chiamate.keys(); x
										.hasMoreElements();) {
									cKey = (String) x.nextElement();
									myDati = (Hashtable<String, Object>) chiamate.get(cKey);
									if (((String) myDati.get("chiamate"))
											.equals("S")) {
										log.debug("Invio un inserimento");
										// Chiamata per il materiale non trovato
										if (myCon.Send("ADD(\""
												+ (String) myDati
														.get("cognome_nome")
												+ "\",\""
												+ (String) myDati.get("tipo")
												+ "\",\""
												+ (String) myDati
														.get("cod_bib_ut")
												+ "\",\""
												+ (String) myDati
														.get("cod_utente")
												+ "\")"))
											;
										{
											Linea = myCon.Recive();
											log.debug("Risposta inserimento : "
													+ Linea);
										}
									} else if (((String) myDati.get("chiamate"))
											.equals("O")) {
										log.debug("Invio un inserimento");
										// Chiamata per il materiale trovato
										if (myCon.Send("ADD(\""
												+ (String) myDati
														.get("cognome_nome")
												+ "\",\""
												+ (String) myDati.get("tipo")
												+ "\",\""
												+ (String) myDati
														.get("cod_bib_ut")
												+ "\",\""
												+ (String) myDati
														.get("cod_utente")
												+ "\")"))
											;
										{
											Linea = myCon.Recive();
											log.debug("Risposta inserimento : "
													+ Linea);
										}
									} else if (((String) myDati.get("chiamate"))
											.equals("C")) {
										// Chiusura chiamata
										log.debug("Invio una cancellazione");
										if (myCon.Send("DELETE(\""
												+ (String) myDati
														.get("cognome_nome")
												+ "\",\""
												+ (String) myDati.get("tipo")
												+ "\",\""
												+ (String) myDati
														.get("cod_bib_ut")
												+ "\", \""
												+ (String) myDati
														.get("cod_utente")
												+ "\")"))
											;
										{
											Linea = myCon.Recive();
											log.debug("Risposta Cancellazione : "
													+ Linea);
										}
									}
									// Bncf.Controlli.datiChiam.remove(myDati);
									chiamate.remove(cKey);
								}
								log.debug("invio Fine Trasm");
								myCon.Send("BYE");
							}
							log.debug("Chiudo connessione");
							myCon.Close();
						}
					} catch (Exception e) {
						log.error(e);
					}
				}
				// RImango fermo per 30 Secondi
				sleep(30000);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Questo metodo viene utilizzato per terminare il Thread
	 * 
	 */
	public void end() {
		ciclo = false;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere una chiamata da inviare al
	 * server chiamate
	 * 
	 * @param key
	 * @param value
	 */
	public void add(String key, Hashtable<String, Object> value) {
		chiamate.put(key, value);
	}

	/**
	 * Questo metodo viene utilizzato per indicare la porta del Server da
	 * contattare
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il nome del Server da
	 * contattare
	 * 
	 * @param server
	 */
	public void setServer(String server) {
		this.server = server;
	}
}
