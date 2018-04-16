/**
 * 
 */
package net.bncf.uol2010.banco.gestioneFunzioni;

import java.util.ArrayList;
import java.util.Vector;

import javax.xml.soap.SOAPException;


import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import mx.randalf.converter.text.ConvertToUTF8;

/**
 * Questa classe viene utilizzata per indicare i Tasti funzione per la gestione
 * delle attività
 * 
 * @author Randazzo Massimiliano
 * 
 */
public class TastoFunzione {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = Logger.getLogger(TastoFunzione.class);

	/**
	 * Questa variabile viene utilizzata per indicare che tipo di Tasto funzione
	 * assegnare
	 */
	private String key = "";

	/**
	 * Questa variabile viene utilizzata per indicare la descrizione associato
	 * al tasto
	 */
	private String descrizione = "";

	/**
	 * Questa variabile viene utilizzata per indicare il nuovo progressivo Iter
	 * da associare al movimento
	 */
	private int progrIterFin = 0;

	/**
	 * Questa variabile viene utilizzata per indicare il codice del movimento da
	 * modificare
	 */
	private int codMovServ = 0;

	/**
	 * Questa variabile viene utilizzata per indicare se l'attivit� � di tipo
	 * ILL
	 */
	private boolean richiestaILL = false;

	/**
	 * Questa variabile viene utilizzata per indicare la lista delle azioni
	 */
	private Vector<String> action = null;

	/**
	 * Costruttore
	 */
	public TastoFunzione() {
		super();
		action = new Vector<String>();
	}

	/**
	 * Costruttore nel quale � possibile valorizzare tutte le informazioni
	 * necesarie per la risposta
	 * 
	 * @param key
	 * @param descrizione
	 * @param progrIterFin
	 * @param codMovServ
	 * @param richiestaILL
	 */
	public TastoFunzione(String key, String descrizione, int progrIterFin,
			int codMovServ, boolean richiestaILL) {
		super();
		action = new Vector<String>();
		this.key = key;
		this.descrizione = descrizione;
		this.progrIterFin = progrIterFin;
		this.codMovServ = codMovServ;
		this.richiestaILL = richiestaILL;
	}

	/**
	 * Questo metodo viene utilizzato per stampare i dati del Tasto funzione
	 * 
	 * @return
	 */
	public MessageElement write(ConvertToUTF8 convertUtf8) {
		MessageElement tastoFunzione = null;
		ArrayList<MessageElement> val = null;

		try {
			tastoFunzione = new MessageElement();
			tastoFunzione.setName("tastoFunzione");

			val = new ArrayList<MessageElement>();

			if (!key.equals(""))
				convertUtf8.addChildElement(tastoFunzione, val, "key", key);

			if (!descrizione.equals(""))
				convertUtf8.addChildElement(tastoFunzione, val, "descrizione",
						descrizione);

			if (progrIterFin > 0)
				convertUtf8.addChildElement(tastoFunzione, val, "progrIterFin",
						progrIterFin);

			if (codMovServ > 0)
				convertUtf8.addChildElement(tastoFunzione, val, "codMovServ",
						codMovServ);

			convertUtf8.addChildElement(tastoFunzione, val, "richiestaILL",
					richiestaILL ? "Y" : "N");

			for (int x = 0; x < action.size(); x++) {
				convertUtf8.addChildElement(tastoFunzione, val, "action",
						(String) action.get(x));
			}
		} catch (SOAPException e) {
			log.error(e);
		}

		return tastoFunzione;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la lista delle azioni
	 * 
	 * @param action
	 */
	public void addAction(String action) {
		this.action.add(action);
	}

	/**
	 * Questo metodo viene utilizzato per indicare il codice del movimento da
	 * modificare
	 * 
	 * @param codMovServ
	 */
	public void setCodMovServ(int codMovServ) {
		this.codMovServ = codMovServ;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la descrizione associato al
	 * tasto
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Questo metodo viene utilizzato per indicare che tipo di Tasto funzione
	 * assegnare
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il nuovo progressivo Iter da
	 * associare al movimento
	 * 
	 * @param progrIterFin
	 */
	public void setProgrIterFin(int progrIterFin) {
		this.progrIterFin = progrIterFin;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se l'attivit� � di tipo ILL
	 * 
	 * @param richiestaILL
	 */
	public void setRichiestaILL(boolean richiestaILL) {
		this.richiestaILL = richiestaILL;
	}

}
