package net.bncf.uol2010.banco.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import mx.randalf.hibernate.exception.HibernateUtilException;
import net.bncf.uol2010.banco.security.exception.SecurityException;
import net.bncf.uol2010.database.schema.servizi.dao.AutBibServiziDAO;
import net.bncf.uol2010.database.schema.servizi.dao.AutorizzazioniBibDAO;
import net.bncf.uol2010.database.schema.servizi.dao.ServiziDAO;
import net.bncf.uol2010.database.schema.servizi.entity.AutBibServizi;
import net.bncf.uol2010.database.schema.servizi.entity.AutorizzazioniBib;
import net.bncf.uol2010.database.schema.servizi.entity.Servizi;

/**
 * Questa tabella viene utilizzata per la gestione della Sicurazza del banco
 * 
 * @author Massimiliano Randazzo
 *
 */
public class Security {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(Security.class);

	/**
	 * Questa variabile viene utilizzata per gestire le informazioni relatice al
	 * client
	 */
	private HttpServletRequest request = null;

	/**
	 * Questa variabile viene utilizzata per indicare l'autorizzazione del
	 * bibliotecario
	 */
	private String autorizzazioneBib = null;

	/**
	 * Questa variabile viene utilizzata per indicare il nome del bibliotecario
	 */
	private String nomeBibliotecario = null;

	/**
	 * Questa variabile viene utilizzata per indicare il login del bibliotecario
	 */
	private String loginBibliotecario = null;

	public Security(HttpServletRequest request, String cookieName) throws SecurityException {
		this.request = request;
		readCookie(cookieName);
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relativo hai
	 * Coooke
	 * 
	 * @param cookieName
	 * @throws SecurityException
	 */
	private void readCookie(String cookieName) throws SecurityException {
		String cookieValue = null;
		String[] st = null;

		log.debug("readCoockie - Start");
		if (request.getCookies() != null) {
			for (int x = 0; x < request.getCookies().length; x++) {
				if (request.getCookies()[x].getName().equals(cookieName)) {
					cookieValue = request.getCookies()[x].getValue();
					st = cookieValue.split("#");
					loginBibliotecario = st[0];
					if (st.length > 1)
						nomeBibliotecario = st[1];
					if (st.length > 2)
						autorizzazioneBib = st[2];
					else
						throw new SecurityException(3, loginBibliotecario);
				}
			}
		}

		if (cookieValue == null)
			throw new SecurityException(4);

		log.debug("readCoockie - End");
	}

	/**
	 * Questo metodo viene utilizzato per testare se l'utente bibliotecario è
	 * abilitato per la gestione del Servizio Selezionato
	 * 
	 * @param idServizi
	 * @return
	 */
	public boolean checkServizi(String idServizi) {
		boolean ris = false;
		ServiziDAO serviziDAO = null;
		AutorizzazioniBibDAO autorizzazioniBibDAO = null;
		AutBibServiziDAO autBibServiziDAO = null;
		Servizi servizi = null;
		AutorizzazioniBib autorizzazioniBib = null;
		List<AutBibServizi> autBibServizis = null;

		try {
			serviziDAO = new ServiziDAO();
			servizi = serviziDAO.findById(idServizi);
			if (servizi != null) {
				autorizzazioniBibDAO = new AutorizzazioniBibDAO();
				autorizzazioniBib = autorizzazioniBibDAO.findByDesc(autorizzazioneBib);
				if (autorizzazioniBib != null) {
					autBibServiziDAO = new AutBibServiziDAO();
					autBibServizis = autBibServiziDAO.find(autorizzazioniBib, servizi, null);
					if (autBibServizis != null && autBibServizis.size() > 0) {
						ris = true;
					}
				}
			}
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			log.error(e.getMessage(), e);
		}

		return ris;
	}

	public String getNomeBibliotecario() {
		return nomeBibliotecario;
	}

}
