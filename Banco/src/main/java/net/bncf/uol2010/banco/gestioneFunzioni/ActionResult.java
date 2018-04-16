/**
 * 
 */
package net.bncf.uol2010.banco.gestioneFunzioni;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;

import mx.randalf.converter.text.ConvertToUTF8;
import mx.randalf.hibernate.FactoryDAO;
import net.bncf.uol2010.utility.convertText.exception.ConvertException;

/**
 * Questa classe viene utilizzata per getrasferire tutto il materiale necessario
 * per le classe di implementazione della servlet (Connessione DB, oggetto
 * request, Parametri QueryString, ecc..) e per stampare il messaggio di
 * risposta
 * 
 * @author Randazzo Massimiliano
 * 
 */
public class ActionResult {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = Logger.getLogger(ActionResult.class);

	/**
	 * Questa variabile viene utilizzata per indicare la sessione attuale
	 */
	private String session = "";

	/**
	 * Questa variabile viene utilizzata per indicare il login dell'utente
	 */
	private String login = "";

	/**
	 * Questa variabile viene utilizzata per indicare la sigla della Biblioteca
	 */
	private String codBib = "";

	/**
	 * Questa variabile viene utilizzata per gestire la lista degli errori
	 * presenti
	 */
	private Vector<String> error = null;

	/**
	 * Questa variabile viene utilizzata per gestire la lista degli avvisi
	 * presenti
	 */
	private Vector<String> warning = null;

	/**
	 * Questa variabile viene utilizzata per gestire la lista delle conferme
	 * presenti
	 */
	private Vector<String> confirm = null;

	/**
	 * Questa variabile viene utilizzata per indicare se visualizzare oppure no
	 * il campo utilizzato per la ricerca del movimento
	 */
	private boolean viewNumMov = false;

	/**
	 * Questa variabile viene utilizzata per indicare se visualizzare oppure no
	 * l'area relativa alla visualizzazione dei dati della notizia e Utente
	 */
	private boolean viewNotizia = false;

	/**
	 * Questa variabile viene utilizzata per indicare il testo XML aggiuntivo da
	 * inviare nella risposta
	 */
	private MessageElement testoXml = null;

	/**
	 * Questa variabile viene utilizzata per indicare se visualizzare oppure no
	 * l'area relativa alle risposte del sistema
	 */
	private boolean viewRisultato = true;

	/**
	 * Questa variabile viene utilizzata per indicare il codice identificativo
	 * dell'Utente
	 */
	private String idUtente = "";

	/**
	 * Questa variabile viene utilizzata per indicare il nome dell'utente
	 */
	private String cognomeNome = "";

	/**
	 * Questa variabile viene utilizzata per indicare la data di Nascita
	 * dell'utente
	 */
	private Timestamp dataNascita = null;

	/**
	 * Questa variabile viene utilizzata per indicare l'autore dell'opera
	 */
	private String autore = "";

	/**
	 * Questa variabile viene utilizzata per indicare il titolo dell'opera
	 */
	private String titolo = "";

	/**
	 * Questa variabile viene utilizzata per indicare la scadenza dell'opera
	 */
	private Timestamp scadenza = null;

	/**
	 * Questa variabile viene utilizzata per indicare la descrizione
	 * dell'attivit�
	 */
	private Vector<String> descrAtt = null;

	/**
	 * Questa variabile viene utilizzata per indicare le note della richiesta
	 */
	private Vector<String> note = null;

	/**
	 * Questa variabile viene utilizzata per indicare se visualizzare oppure no
	 * il campo delle note del bibliotecario
	 */
	private boolean viewNote = false;

	/**
	 * Questa variabile viene utilizzata per indicare le note del bibliotecario
	 * sulla richiesta
	 */
	private String noteBib = "";

	/**
	 * Questa variabile viene utilizzata per la gestione del Pool di connessioni
	 * con il Database private ConnectionPool conn = null;
	 */

	/**
	 * Questa variabile viene utilizzata gestire tutte le chiamate provenienti
	 * dal client.
	 */
	private HttpServletRequest request = null;

	/**
	 * Questa variabile viene utilizzata per gestire tutte le chiamate verso il
	 * client.
	 */
	private HttpServletResponse response = null;

	/**
	 * Questa variabile viene utilizzata per indicare la posizione del file
	 * relativo alla Security. private String securityPath = "";
	 */

	/**
	 * Questa variabile viene utilizzata per indicare la durati di una sessione
	 * private int securityTimeOut = 0;
	 */

	/**
	 * Questa variabile viene utilizzata per indicare se effetuare la verifica
	 * della Password oppure No private boolean securityCheck = false;
	 */

	/**
	 * Questa variabile viene utilizzata per indicare se utilizzare il Debug
	 * oppure no private boolean debug = false;
	 */

	/**
	 * Questa variabile viene utilizzata per visualizzare l'area dei Tasti
	 * Funzione
	 */
	private boolean viewTastiFunzione = false;

	/**
	 * Questa variabile viene utilizzata per indicare i tisti Funzione delle
	 * attività
	 */
	private Vector<TastoFunzione> tastiFunzione = null;

	/**
	 * Questa variabile viene utilizzata per indicare i parametri ILL private
	 * Properties propertiesIll = null;
	 */

	/**
	 * Questa variabile viene utilizzata per eseguire l'aggiornamento del Server
	 * Chiamate private Chiamate chiamate = null;
	 */

	/**
	 * Costruttore
	 */
	public ActionResult() {
		super();
		error = new Vector<String>();
		descrAtt = new Vector<String>();
		note = new Vector<String>();
		warning = new Vector<String>();
		confirm = new Vector<String>();
		tastiFunzione = new Vector<TastoFunzione>();
	}

	/**
	 * Questo metodo viene utilizzata gestire tutte le chiamate provenienti dal
	 * client.
	 * 
	 * @param request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la posizione del file relativo
	 * alla Security.
	 * 
	 * @return public String getSecurityPath() { return securityPath; }
	 */

	/**
	 * Questo metodo viene utilizzato per indicare la posizione del file
	 * relativo alla Security.
	 * 
	 * @param securityPath
	 *            public void setSecurityPath(String securityPath) {
	 *            this.securityPath = securityPath; }
	 */

	/**
	 * Questo metodo viene utilizzato per leggere la durati di una sessione
	 * 
	 * @return public int getSecurityTimeOut() { return securityTimeOut; }
	 */

	/**
	 * Questo metodo viene utilizzato per indicare la durati di una sessione
	 * 
	 * @param securityTimeOut
	 *            public void setSecurityTimeOut(int securityTimeOut) {
	 *            this.securityTimeOut = securityTimeOut; }
	 */

	/**
	 * Questo metodo viene utilizzata leggere tutte le chiamate provenienti dal
	 * client.
	 * 
	 * @param request
	 */
	public HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * Questo metodo viene utilizzata per la geaddChildElementstione del Pool di
	 * connessioni con il Database
	 * 
	 * @param conn
	 *            public void setConn(ConnectionPool conn) { this.conn = conn; }
	 */

	/**
	 * Questo metodo viene utilizzato per leggere il Pool di connessioni con il
	 * Database
	 * 
	 * @return public ConnectionPool getConn() { return conn; }
	 */

	/**
	 * Questo metodo viene utilizzato per stampare il risultato della
	 * elaborazione
	 * 
	 * @return
	 */
	public String printResult() {
		String ris = "";
		MessageElement bancodigit = null;
		ConvertException convertException = null;
		ConvertToUTF8 convertUtf8 = null;

		try {
			log.debug("printResult Start");
			convertException = new ConvertException();
			convertUtf8 = new ConvertToUTF8(convertException, "bancodigit");
			bancodigit = new MessageElement();
			bancodigit.setName("bancodigit");
			convertUtf8.setAttribute(bancodigit, "xmlns:xlink",
					"http://www.w3.org/TR/xlink",true);
			convertUtf8.setAttribute(bancodigit, "version", "1.0", true);

			if (viewNumMov)
				bancodigit.addChildElement(writeNumMovimento(convertUtf8));

			if (viewRisultato)
				bancodigit.addChildElement(writeRisultato(convertUtf8));

			if (viewNotizia)
				bancodigit.addChildElement(writeNotizia(convertUtf8));

			if (viewNote && viewTastiFunzione)
				bancodigit.addChildElement(writeNote(convertUtf8));

			if (viewTastiFunzione)
				bancodigit.addChildElement(writeTastiFunzione(convertUtf8));

			if (!viewNumMov && !viewRisultato && !viewNotizia && !viewNote
					&& !viewTastiFunzione)
				bancodigit.addChildElement(writeNumMovimento(convertUtf8));

			if (testoXml != null)
				bancodigit.addChildElement(testoXml);
			log.debug("printResult End");
		} catch (DOMException e) {
			log.error(e);
		} catch (SOAPException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			log.debug("printResult Finally Start");
			ris = XMLUtils.ElementToString(bancodigit);
			log.debug("Ris: " + ris);
			log.debug("printResult Finally End");
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per stampare la risposta relativa alla
	 * sezione numMovimento
	 * 
	 * @return
	 */
	private MessageElement writeNumMovimento(ConvertToUTF8 convertUtf8) {
		MessageElement numMovi = null;

		convertUtf8.setSezione("bancodigit/numMovimento");
		numMovi = new MessageElement();
		numMovi.setName("numMovimento");
		convertUtf8.setAttribute(numMovi, "view", "Y",true);
		return numMovi;
	}

	/**
	 * Questo metodo viene utilizzato per stampare la risposta relativa all'area
	 * risultato
	 * 
	 * @return
	 */
	private MessageElement writeRisultato(ConvertToUTF8 convertUtf8) {
		MessageElement risultato = null;
		ArrayList<MessageElement> val = null;

		try {
			convertUtf8.setSezione("bancodigit/risultato");
			risultato = new MessageElement();
			risultato.setName("risultato");
			convertUtf8.setAttribute(risultato, "view", "Y",true);

			val = new ArrayList<MessageElement>();
			for (int x = 0; x < warning.size(); x++) {
				convertUtf8.addChildElement(risultato, val, "warning",
						(String) warning.get(x));
			}

			for (int x = 0; x < error.size(); x++) {
				convertUtf8.addChildElement(risultato, val, "error",
						(String) error.get(x));
			}

			for (int x = 0; x < confirm.size(); x++) {
				convertUtf8.addChildElement(risultato, val, "confirm",
						(String) confirm.get(x));
			}

			for (int x = 0; x < note.size(); x++) {
				convertUtf8.addChildElement(risultato, val, "note",
						(String) note.get(x));
			}
		} catch (SOAPException e) {
			log.error(e);
		}

		return risultato;
	}

	/**
	 * Questo metodo viene utilizzato per stampare la risposta relativa all'area
	 * Notizia
	 * 
	 * @return
	 */
	private MessageElement writeNotizia(ConvertToUTF8 convertUtf8) {
		MessageElement notizia = null;
		ArrayList<MessageElement> val = null;
		GregorianCalendar gc = null;
		String data = "";

		try {
			convertUtf8.setSezione("bancodigit/notizia");
			notizia = new MessageElement();
			notizia.setName("notizia");
			convertUtf8.setAttribute(notizia, "view", "Y",true);

			val = new ArrayList<MessageElement>();

			if (!autore.equals(""))
				convertUtf8.addChildElement(notizia, val, "autore", autore);

			if (!titolo.equals("")) {
				convertUtf8.setConvertText(false);
				convertUtf8.addChildElement(notizia, val, "titolo", titolo);
				convertUtf8.setConvertText(true);
			}

			if (!idUtente.equals(""))
				notizia.addChildElement(writeUtente(convertUtf8));
			convertUtf8.setSezione("bancodigit/notizia");

			for (int x = 0; x < descrAtt.size(); x++) {
				convertUtf8.addChildElement(notizia, val, "descrAtt",
						(String) descrAtt.get(x));
			}

			if (scadenza != null) {
				gc = new GregorianCalendar();
				gc.setTimeInMillis(scadenza.getTime());

				if (gc.get(Calendar.DAY_OF_MONTH) < 10)
					data = "0";
				data += gc.get(Calendar.DAY_OF_MONTH);
				data += "-";
				if ((gc.get(Calendar.MONTH) + 1) < 10)
					data += "0";
				data += gc.get(Calendar.MONTH) + 1;
				data += "-";
				data += gc.get(Calendar.YEAR);
				convertUtf8.addChildElement(notizia, val, "scadenza", data);
			}
		} catch (SOAPException e) {
			log.error(e);
		}

		return notizia;
	}

	/**
	 * Questo metodo viene utilizzato per stampare la risposta relativa all'area
	 * Utente
	 * 
	 * @return
	 */
	private MessageElement writeUtente(ConvertToUTF8 convertUtf8) {
		MessageElement utente = null;
		ArrayList<MessageElement> val = null;

		try {
			convertUtf8.setSezione("bancodigit/notizia/utente");
			utente = new MessageElement();
			utente.setName("utente");

			val = new ArrayList<MessageElement>();

			if (!cognomeNome.equals(""))
				convertUtf8.addChildElement(utente, val, "cognomeNome",
						cognomeNome);

			if (!idUtente.equals(""))
				convertUtf8.addChildElement(utente, val, "idUtente", idUtente);

			if (dataNascita != null) {
				convertUtf8.addChildElement(utente, val, "dataNascita",
						FactoryDAO.converDateIta(dataNascita));
			}
		} catch (SOAPException e) {
			log.error(e);
		}

		return utente;
	}

	/**
	 * Questo metodo viene utilizzato per stampare la risposta relativa all'area
	 * Note
	 * 
	 * @return
	 */
	private MessageElement writeNote(ConvertToUTF8 convertUtf8) {
		MessageElement note = null;
		ArrayList<MessageElement> val = null;

		try {
			convertUtf8.setSezione("bancodigit/note");
			note = new MessageElement();
			note.setName("note");
			convertUtf8.setAttribute(note, "view", "Y", true);

			val = new ArrayList<MessageElement>();

			if (!noteBib.equals(""))
				convertUtf8.addChildElement(note, val, "noteBib", noteBib);

		} catch (SOAPException e) {
			log.error(e);
		}

		return note;
	}

	/**
	 * Questo metodo viene utilizzato per stampare la risposta relativa all'area
	 * dei Tasti Funzione
	 * 
	 * @return
	 */
	private MessageElement writeTastiFunzione(ConvertToUTF8 convertUtf8) {
		MessageElement tastiFunzione = null;

		try {
			convertUtf8.setSezione("bancodigit/tastiFunzione");
			tastiFunzione = new MessageElement();
			tastiFunzione.setName("tastiFunzione");
			convertUtf8.setAttribute(tastiFunzione, "view", "Y", true);

			convertUtf8.setSezione("bancodigit/tastiFunzione/tastoFunzione");
			for (int x = 0; x < this.tastiFunzione.size(); x++) {
				tastiFunzione
						.addChildElement(((TastoFunzione) this.tastiFunzione
								.get(x)).write(convertUtf8));
			}

		} catch (SOAPException e) {
			log.error(e);
		}

		return tastiFunzione;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere l'errore nella lista
	 * 
	 * @param error
	 */
	public void addError(String error) {
		this.error.add(error);
	}

	/**
	 * Questo metodo viene utilizzato per leggere la lista degli errori presenti
	 * 
	 * @return
	 */
	public Vector<String> getError() {
		return this.error;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere un avvertimento nella lista
	 * 
	 * @param error
	 */
	public void addWarning(String warning) {
		this.warning.add(warning);
	}

	/**
	 * Questo metodo viene utilizzato per leggere la lista degli avvertimenti
	 * presenti
	 * 
	 * @return
	 */
	public Vector<String> getWarning() {
		return this.warning;
	}

	/**
	 * Questo metodo viene utilizzato per gestire tutte le chiamate verso il
	 * client.
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Questo metodo viene utilizzato per gestire tutte le chiamate verso il
	 * client.
	 * 
	 * @param response
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la sigla della Biblioteca
	 * 
	 * @return
	 */
	public String getCodBib() {
		return codBib;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la sigla della Biblioteca
	 * 
	 * @param codBib
	 */
	public void setCodBib(String codBib) {
		this.codBib = codBib;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se visualizzare oppure no il
	 * campo utilizzato per la ricerca del movimento
	 * 
	 * @param viewNumMov
	 */
	public void setViewNumMov(boolean viewNumMov) {
		this.viewNumMov = viewNumMov;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se visualizzare oppure no
	 * l'area relativa alla visualizzazione dei dati della notizia e Utente
	 * 
	 * @param viewNotizia
	 */
	public void setViewNotizia(boolean viewNotizia) {
		this.viewNotizia = viewNotizia;
	}

	/**
	 * Questo metodo viene utilizzato per indicare l'autore dell'opera
	 * 
	 * @param autore
	 */
	public void setAutore(String autore) {
		this.autore = autore;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il nome dell'utente
	 * 
	 * @param cognomeNome
	 */
	public void setCognomeNome(String cognomeNome) {
		this.cognomeNome = cognomeNome;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la descrizione dell'attivit�
	 * 
	 * @param descrAtt
	 */
	public void addDescrAtt(String descrAtt) {
		this.descrAtt.add(descrAtt);
	}

	/**
	 * Questo metodo viene utilizzato per indicare il codice identificativo
	 * dell'Utente
	 * 
	 * @param idUtente
	 */
	public void setIdUtente(String idUtente) {
		this.idUtente = idUtente;
	}

	/**
	 * Questo metodo viene utilizzato per indicare le note della richiesta
	 * 
	 * @param note
	 */
	public void addNote(String note) {
		this.note.add(note);
	}

	/**
	 * Questo metodo viene utilizzato per indicare le note del bibliotecario
	 * sulla richiesta
	 * 
	 * @param noteBib
	 */
	public void setNoteBib(String noteBib) {
		this.noteBib = noteBib;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il titolo dell'opera
	 * 
	 * @param titolo
	 */
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se visualizzare oppure no il
	 * campo delle note del bibliotecario
	 * 
	 * @param viewNote
	 */
	public void setViewNote(boolean viewNote) {
		this.viewNote = viewNote;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se visualizzare oppure no
	 * l'area relativa alle risposte del sistema
	 * 
	 * @param viewRisultato
	 */
	public void setViewRisultato(boolean viewRisultato) {
		this.viewRisultato = viewRisultato;
	}

	/**
	 * Questo metodo viene utilizzato per visualizzare l'area dei Tasti Funzione
	 * 
	 * @param viewTastiFunzione
	 */
	public void setViewTastiFunzione(boolean viewTastiFunzione) {
		this.viewTastiFunzione = viewTastiFunzione;
	}

	/**
	 * Questo metodo viene utilizzato per indicare le conferme della richiesta
	 * 
	 * @param note
	 */
	public void addConfirm(String confirm) {
		this.confirm.add(confirm);
	}

	/**
	 * Questo metodo viene utilizzato per indicare la data di Nascita
	 * dell'utente
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(Timestamp dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la scadenza dell'opera
	 * 
	 * @param scadenza
	 */
	public void setScadenza(Timestamp scadenza) {
		this.scadenza = scadenza;
	}

	/**
	 * Questo metodo viene utilizzato per indicare i tisti Funzione delle
	 * attivit�
	 * 
	 * @param tastiFunzione
	 */
	public void addTastiFunzione(TastoFunzione tastiFunzione) {
		this.tastiFunzione.add(tastiFunzione);
	}

	/**
	 * Questo metodo viene utilizzato per verificare se effetuare la verifica
	 * della Password oppure No
	 * 
	 * @return public boolean isSecurityCheck() { return securityCheck; }
	 */

	/**
	 * Questo metodo viene utilizzato per indicare se effetuare la verifica
	 * della Password oppure No
	 * 
	 * @param securityCheck
	 *            public void setSecurityCheck(boolean securityCheck) {
	 *            this.securityCheck = securityCheck; }
	 */

	/**
	 * Questo metodo viene utilizzato per verificare se utilizzare il Debug
	 * oppure no
	 * 
	 * @return public boolean isDebug() { return debug; }
	 */

	/**
	 * Questo metodo viene utilizzato per indicare se utilizzare il Debug oppure
	 * no
	 * 
	 * @param debug
	 *            public void setDebug(boolean debug) { this.debug = debug; }
	 */

	/**
	 * Questo metodo viene utilizzato per leggere i parametri ILL
	 * 
	 * @return public Properties getPropertiesIll() { return propertiesIll; }
	 */

	/**
	 * Questo metod viene utilizzato per indicare i parametri ILL
	 * 
	 * @param propertiesIll
	 *            public void setPropertiesIll(Properties propertiesIll) {
	 *            this.propertiesIll = propertiesIll; }
	 */

	/**
	 * Questo metodo viene utilizzato per leggere l'aggiornamento del Server
	 * Chiamate
	 * 
	 * @return public Chiamate getChiamate() { return chiamate; }
	 */

	/**
	 * Questo metodo viene utilizzato per eseguire l'aggiornamento del Server
	 * Chiamate
	 * 
	 * @param chiamate
	 *            public void setChiamate(Chiamate chiamate) { this.chiamate =
	 *            chiamate; }
	 */

	/**
	 * Questo metodo viene utilizzato per leggere il login dell'utente
	 * 
	 * @return
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il login dell'utente
	 * 
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la sessione attuale
	 * 
	 * @return
	 */
	public String getSession() {
		return session;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la sessione attuale
	 * 
	 * @param session
	 */
	public void setSession(String session) {
		this.session = session;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il testo XML
	 * 
	 * @param testoXml
	 *            The testoXml to set.
	 */
	public void setTestoXml(MessageElement testoXml) {
		this.testoXml = testoXml;
	}
}
