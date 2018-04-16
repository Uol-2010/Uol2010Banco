/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.stdModuli;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;

import mx.randalf.configuration.Configuration;
import mx.randalf.configuration.exception.ConfigurationException;
import mx.randalf.converter.text.ConvertToUTF8;
import mx.randalf.hibernate.exception.HibernateUtilException;
import net.bncf.uol2010.database.schema.servizi.dao.MenuBancoDAO;
import net.bncf.uol2010.database.schema.servizi.entity.MenuBanco;
import net.bncf.uol2010.utility.xsd.BancoXsd;
import net.bncf.uol2010.utility.xsd.banco.Banco.Utente.Autorizzazioni.Diritto;

/**
 * Questa classe viene utilizzata per disegnare il menu' a cascata
 * 
 * @author Massimiliano Randazzo
 *
 */
public class CascadeMenu {

	/**
	 * Questa variabile viene utilizzata per gestire la conversione UTF-8
	 */
	private ConvertToUTF8 convert = null;

	/**
	 * Questa variabile viene utilizzata per gestire i parametri del banco
	 */
	private BancoXsd banco = null;

	/**
	 * Costruttore
	 * 
	 * @param convert
	 * @param convertURI
	 * @param banco
	 */
	public CascadeMenu(ConvertToUTF8 convert, BancoXsd banco) {
		this.convert = convert;
		this.banco = banco;
	}

	/**
	 * Questo metodo viene utilizzato per generare le informazioni per il
	 * Cascade Menu
	 * 
	 * @return
	 * @throws HibernateUtilException
	 * @throws SOAPException
	 * @throws ConfigurationException
	 */
	public MessageElement generate() throws HibernateUtilException, SOAPException, ConfigurationException {
		CascadeMenuStru cascadeMenuStru = null;
		MessageElement cascadeMenu = null;

		try {
			cascadeMenuStru = new CascadeMenuStru();

			cascadeMenu = cascadeMenuStru.disegna(convert,
					banco.getBanco().getUtente().getAutorizzazioni().getDiritto());
		} catch (HibernateUtilException e) {
			throw e;
		} catch (SOAPException e) {
			throw e;
		} catch (ConfigurationException e) {
			throw e;
		}

		return cascadeMenu;
	}

	/**
	 * Questo metodo viene utilizzato per testare i Servizi
	 * 
	 * @param idModuliAmministrazione
	 * @return public boolean checkServizi(String idModuliAmministrazione) {
	 *         boolean ris=false;
	 * 
	 *         if (idModuliAmministrazione != null &&
	 *         !idModuliAmministrazione.trim().equals("")) { for(int x=0;
	 *         x<banco.getBanco().getUtente().getAutorizzazioni().getDiritto().
	 *         size(); x++) { if
	 *         (banco.getBanco().getUtente().getAutorizzazioni().getDiritto().
	 *         get(x).getID().equals(idModuliAmministrazione)) { ris=true;
	 *         break; } } } else ris=true; return ris; }
	 */
}

class CascadeMenuStru {

	/**
	 * Questa variabile raccoglie le Voci dei Sottomenu di cui è composto il
	 * Nodo
	 */
	private List<CascadeMenuStru> menu = new ArrayList<CascadeMenuStru>();

	/**
	 * Identificativo del Menu
	 */
	private MenuBanco idMenuBanco = null;

	/**
	 * Lista delle autorizzazioni di cui è composto il Menu
	 */
	private List<String> idModuliAmministrazione = new ArrayList<String>();

	/**
	 * Identificativo del Menu Padre
	 */
	protected int idMenuBancoPadre = -1;

	/**
	 * Ordine di visualizzazione del Menu
	 */
	private int ordine = -1;

	/**
	 * Titolo del Menu
	 */
	private String titolo = null;

	/**
	 * Costruttore Semplice
	 * 
	 * @throws HibernateUtilException
	 */
	public CascadeMenuStru() throws HibernateUtilException {
		this.esamina();
	}

	/**
	 * Costruttore con popolamento delle informazioni del nodo
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public CascadeMenuStru(MenuBanco menuBanco) {
		idMenuBanco = menuBanco;
		if (menuBanco.getIdModuliAmministrazione() != null)
			idModuliAmministrazione.add(menuBanco.getIdModuliAmministrazione().getId());
		if (menuBanco.getIdMenuBancoPadre() != null)
			idMenuBancoPadre = menuBanco.getIdMenuBancoPadre().getId();
		ordine = menuBanco.getOrdine();
		titolo = menuBanco.getTitolo();
	}

	/**
	 * Questo metodo viene utilizzato per esaminare il Menu
	 * 
	 * @throws HibernateUtilException
	 */
	protected void esamina() throws HibernateUtilException {
		MenuBancoDAO menuBancoDAO = null;
		List<MenuBanco> menuBancos = null;
		List<Order> orders = null;
		CascadeMenuStru cascadeMenuStru = null;

		try {
			menuBancoDAO = new MenuBancoDAO();
			orders = new Vector<Order>();
			orders.add(Order.asc("ordine"));
			menuBancos = menuBancoDAO.findMenuBancoPadre(idMenuBanco, orders);
			if (menuBancos != null && menuBancos.size() > 0) {
				for (MenuBanco menuBanco : menuBancos) {
					menu.add(new CascadeMenuStru(menuBanco));
				}
			}
		} catch (HibernateException e) {
			throw new HibernateUtilException(e.getMessage(), e);
		} catch (HibernateUtilException e) {
			throw e;
		} finally {
			for (int x = 0; x < menu.size(); x++) {
				cascadeMenuStru = menu.get(x);
				cascadeMenuStru.esamina();
				idModuliAmministrazione.addAll(cascadeMenuStru.getIdModuliAmministrazione());
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere la lista dei Moduli di
	 * Amministrazione
	 * 
	 * @return
	 */
	protected List<String> getIdModuliAmministrazione() {
		return idModuliAmministrazione;
	}

	/**
	 * Verifica se l'utente � autorizzato a accedere al nodo
	 * 
	 * @param diritto
	 * @return
	 */
	protected boolean checkServizio(List<Diritto> diritto) {
		boolean ris = false;
		if (idModuliAmministrazione.size() > 0) {
			for (int x = 0; x < diritto.size(); x++) {
				if (idModuliAmministrazione.contains(diritto.get(x).getID())) {
					ris = true;
					break;
				}
			}
		}
		return ris;
	}

	public MessageElement disegna(ConvertToUTF8 convert, List<Diritto> diritto)
			throws SOAPException, ConfigurationException {
		MessageElement cascadeMenu = null;
		MessageElement menu = null;
		List<CascadeMenuStru> xMenu = new ArrayList<CascadeMenuStru>();

		try {
			cascadeMenu = new MessageElement();
			cascadeMenu.setName("cascadeMenu");

			menu = new MessageElement();
			menu.setName("menu");
			convert.setAttribute(menu, "id", "menuDis", false);
			convert.setAttribute(menu, "class", "menuDis", false);
			for (int x = 0; x < this.menu.size(); x++) {
				if (this.menu.get(x).checkServizio(diritto))
					xMenu.add(this.menu.get(x));
			}
			if (xMenu.size() > 0)
				disMenuPrinc(menu, convert, xMenu, diritto);
			cascadeMenu.addChildElement(menu);
		} catch (SOAPException e) {
			throw e;
		} catch (ConfigurationException e) {
			throw e;
		}

		return cascadeMenu;
	}

	/**
	 * Questo metodo viene utilizzato per disegnare il menu principale
	 * 
	 * @return
	 * @throws SOAPException
	 * @throws ConfigurationException
	 */
	protected void disMenuPrinc(MessageElement menuParent, ConvertToUTF8 convert, List<CascadeMenuStru> xMenu,
			List<Diritto> diritto) throws SOAPException, ConfigurationException {
		MessageElement menu = null;

		try {

			menu = new MessageElement();
			menu.setName("menu");
			convert.setAttribute(menu, "id", "menuBar", false);
			convert.setAttribute(menu, "class", "menuBar", false);

			for (int x = 0; x < xMenu.size(); x++) {
				menu.addChildElement(xMenu.get(x).disDiv(convert, "Bar", "Bar"));
			}
			menuParent.addChildElement(menu);

			for (int x = 0; x < xMenu.size(); x++)
				xMenu.get(x).disSubMenu(menuParent, convert, diritto);
		} catch (SOAPException e) {
			throw e;
		} catch (ConfigurationException e) {
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per disegnare la chiamata DIV del Menu
	 * 
	 * @param rs
	 *            RecordSet del database da cui leggere le informazioni
	 * @param id
	 *            Intestazione del codice ID da associare al DIV
	 * @param clas
	 *            Nome della classe da utilizzare per quanto riguarda la grafica
	 * @throws ConfigurationException
	 * 
	 */
	protected MessageElement disDiv(ConvertToUTF8 convert, String id, String clas) throws ConfigurationException {
		MessageElement menu = null;

		try {
			menu = new MessageElement();
			menu.setName("menu");
			convert.setAttribute(menu, "id", (id + ordine), false);
			convert.setAttribute(menu, "class", clas, false);

			if (titolo != null)
				convert.setAttribute(menu, "title", titolo, false);

			if (idModuliAmministrazione.size() == 1 && this.menu.size() == 0) {
				convert.setAttribute(menu, "cmd",
						Configuration.getValueDefault("banco.urlWeb", "Banco?azione=shiw&modulo=")
								+ idModuliAmministrazione.get(0),
						false);
			}
			if (this.menu.size() > 0) {
				convert.setAttribute(menu, "menu", "menu" + idMenuBanco, false);
			}
			convert.setValue(menu, titolo);
		} catch (ConfigurationException e) {
			throw e;
		}

		return menu;
	}

	/**
	 * Questo medoto viene utilizzato per disegnare il Sub Menu
	 * 
	 * @throws SOAPException
	 * @throws ConfigurationException
	 * 
	 * 
	 */
	protected void disSubMenu(MessageElement menuParent, ConvertToUTF8 convert, List<Diritto> diritto)
			throws SOAPException, ConfigurationException {
		List<CascadeMenuStru> xMenu = new ArrayList<CascadeMenuStru>();
		MessageElement menu = null;

		try {
			for (int x = 0; x < this.menu.size(); x++) {
				if (this.menu.get(x).checkServizio(diritto))
					xMenu.add(this.menu.get(x));
			}

			if (xMenu.size() > 0) {
				menu = new MessageElement();
				menu.setName("menu");
				convert.setAttribute(menu, "id", "menu" + idMenuBanco, false);
				convert.setAttribute(menu, "class", "menu", false);
				for (int x = 0; x < xMenu.size(); x++) {
					menu.addChildElement(xMenu.get(x).disDiv(convert, "menuItem" + idMenuBanco + "_", "menuItem"));
				}
				menuParent.addChildElement(menu);

				for (int x = 0; x < xMenu.size(); x++)
					xMenu.get(x).disSubMenu(menuParent, convert, diritto);
			}
		} catch (SOAPException e) {
			throw e;
		} catch (ConfigurationException e) {
			throw e;
		}

	}
}