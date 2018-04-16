/**
 * 
 */
package net.bncf.uol2010.banco.email;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.Logger;

import mx.randalf.configuration.Configuration;
import mx.randalf.configuration.exception.ConfigurationException;
import net.bncf.uol2010.database.schema.servizi.entity.Richieste;
import net.bncf.uol2010.utility.email.MailClient;

/**
 * @author massi
 *
 */
public class SendMail extends MailClient {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = Logger.getLogger(SendMail.class);

	/**
	 * Costruttore
	 * 
	 * @param smtpServer
	 *            Nome del server Smtp
	 */
	public SendMail(String smtpServer) {
		super(smtpServer);
	}

	/**
	 * Costruttore
	 * 
	 * @param smtpServer
	 *            Nome del server Smtp
	 * @param smtpPort
	 *            Porta del server Smtp
	 */
	public SendMail(String smtpServer, String smtpPort) {
		super(smtpServer, smtpPort);
	}

	/**
	 * Questo metodo viene utilizzato per inviare le email con esito Positivo
	 * 
	 * @param infoRich
	 *            Informazioni della richiesta
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws ConfigurationException
	 */
	public static void sendMailChiamatePositiva(Richieste richieste)
			throws AddressException, MessagingException, ConfigurationException {
		sendMail((String) Configuration.getValueDefault("banco.sendEmail.chiamateUtentePositiva.from", ""),
				richieste.getEmailStatoRichiesta(),
				(String) Configuration.getValueDefault("banco.sendEmail.chiamateUtentePositiva.subject", ""),
				normalizzaMessaggio(
						(String) Configuration.getValueDefault("banco.sendEmail.chiamateUtentePositiva.body", ""),
						richieste));
	}

	/**
	 * Questo metodo viene utilizzato per inviare le email con esito Negativo
	 * 
	 * @param infoRich
	 *            Informazioni della richiesta
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws ConfigurationException
	 */
	public static void sendMailChiamateNegativo(Richieste richieste)
			throws AddressException, MessagingException, ConfigurationException {
		sendMail((String) Configuration.getValueDefault("banco.sendEmail.chiamateUtenteNegativa.from", ""),
				richieste.getEmailStatoRichiesta(),
				(String) Configuration.getValueDefault("banco.sendEmail.chiamateUtenteNegativa.subject", ""),
				normalizzaMessaggio(
						(String) Configuration.getValueDefault("banco.sendEmail.chiamateUtenteNegativa.body", ""),
						richieste));
	}

	/**
	 * Questo metodo viene utilizzato per Normalizzare il Messaggio
	 * 
	 * @param messaggio
	 *            Messaggio da normalizzare
	 * @param infoRich
	 *            Informazioni della richiesta
	 * @return Messagio normalizzato
	 */
	private static String normalizzaMessaggio(String messaggio, Richieste richieste) {
		int pos = -1;
		String testo = "";
		String key = "";

		while ((pos = messaggio.indexOf("$")) > -1) {
			testo += messaggio.substring(0, pos);
			messaggio = messaggio.substring(pos + 1);
			pos = messaggio.indexOf("$");
			if (pos > -1) {
				key = messaggio.substring(0, pos);
				messaggio = messaggio.substring(pos + 1);
				if (key.toLowerCase().equals("titolo")) {
					if (richieste.getTitolo() != null) {
						testo += richieste.getTitolo();
					}
				} else if (key.toLowerCase().equals("notebib")) {
					if (richieste.getNoteBib() != null) {
						testo += richieste.getNoteBib();
					}
				}
			}
		}
		if (!messaggio.trim().equals("")) {
			testo += messaggio;
		}
		return testo;
		// messaggio = messaggio.replace("$Titolo$", infoRic.get("titolo"));
		// return messaggio;
	}

	/**
	 * Questo metodo viene utilizzato per inviare una email
	 * 
	 * @param from
	 *            Mittente della email
	 * @param to
	 *            Destinatario della email
	 * @param subject
	 *            Soggetto del messaggio
	 * @param messageBody
	 *            Messaggio
	 * @throws MessagingException,
	 *             AddressException
	 * @throws ConfigurationException
	 */
	private static void sendMail(String from, String to, String subject, String messageBody)
			throws MessagingException, AddressException, ConfigurationException {
		SendMail sendMail = null;

		try {
			if (((String) Configuration.getValueDefault("banco.sendEmail.smtpPort", "")).equals(""))
				sendMail = new SendMail((String) Configuration.getValueDefault("banco.sendEmail.smtpServer", ""));
			else
				sendMail = new SendMail((String) Configuration.getValueDefault("banco.sendEmail.smtpServer", ""),
						(String) Configuration.getValueDefault("banco.sendEmail.smtpPort", ""));

			if (!((String) Configuration.getValueDefault("banco.sendEmail.smtpUser", "")).equals("")
					&& !((String) Configuration.getValueDefault("banco.sendEmail.smtpPassword", "")).equals(""))
				sendMail.setSmtpAuthentication((String) Configuration.getValueDefault("banco.sendEmail.smtpUser", ""),
						(String) Configuration.getValueDefault("banco.sendEmail.smtpPassword", ""));

			sendMail.setFrom(from);
			sendMail.addTo(to);
			sendMail.sendMail(subject, messageBody);
		} catch (AddressException e) {
			log.error(e);
			throw e;
		} catch (MessagingException e) {
			log.error(e);
			throw e;
		} catch (ConfigurationException e) {
			log.error(e);
			throw e;
		}
	}
}
