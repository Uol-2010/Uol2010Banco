/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import mx.randalf.moduli.standard.xml.DatiXml;
import net.bncf.uol2010.utility.xsd.BancoXsd;

/**
 * Questa classe viene utilizzata per gestire l'editing dell'archivio anagrafico
 * dell'utente
 * 
 * @author Massimiliano Randazzo
 * 
 */
public class Edit
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(Edit.class);

	/**
	 * Questa variabile viene utilizzata per la gestione delle informazioni legate
	 * al Xml finale
	 */
	private DatiXml datiXml = null;

	/**
	 * Questa variabile viene utilizzata per la gestione delle informazioni
	 * relative alle informazioni degli utenti
	 */
	private BancoXsd bancoXsd = null;

	/**
	 * Costruttore
	 */
	public Edit(DatiXml datiXml, BancoXsd bancoXsd)
	{
		this.datiXml = datiXml;
		this.bancoXsd = bancoXsd;
	}

	/**
	 * Questo metodo viene utilizzato per trovare le informazioni necesarie per
	 * l'edito del mag
	 * 
	 * @param idUtente
	 *          Identificativo Urente
	 */
	public void edit(String idUtente, MessageElement edit)
	{
		ViewUtente viewUtente = null;
		ResultSet rsViewUtente = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		boolean nuovo = true;

		try
		{
			if (idUtente != null && !idUtente.equals("") && !idUtente.equals("0"))
			{
				viewUtente = new ViewUtente(Configuration.poolUol2010);
				viewUtente.setCampoValue("idUtente", idUtente);
				rsViewUtente = viewUtente.startSelect();

				if (rsViewUtente.next())
				{
					nuovo = false;
					datiXml.getConvert().addChildElement(edit, nodes, "idUtente",
							idUtente, true);
					datiXml.getConvert().addChildElement(edit, nodes, "dataIns",
							MsSql.conveDateTimeIta(rsViewUtente.getTimestamp("dataIns")), true);
					datiXml.getConvert().addChildElement(edit, nodes, "dataMod",
							MsSql.conveDateTimeIta(rsViewUtente.getTimestamp("dataMod")), true);
					edit.addChildElement(getAutorizzazioniUtente(rsViewUtente
							.getString("idAutorizzazioniUtente"), rsViewUtente.getString("descAutorizzazioneUte")));
					edit.addChildElement(getProfessione(rsViewUtente
							.getString("idUtenteProfessione")));
					edit.addChildElement(getProvenienza(rsViewUtente
							.getString("idUtenteProvenienza")));
					edit.addChildElement(getCittadinanza(rsViewUtente
							.getString("idUtenteCittadinanza")));
					edit.addChildElement(getTipoDocumento(rsViewUtente
							.getString("idUtenteTipoDocumento")));
					datiXml.getConvert().addChildElement(edit, nodes, "password",
							rsViewUtente.getString("password"), true);
					datiXml.getConvert().addChildElement(edit, nodes, "cognome",
							rsViewUtente.getString("cognome"), true);
					datiXml.getConvert().addChildElement(edit, nodes, "nome",
							rsViewUtente.getString("nome"), true);
					datiXml.getConvert().addChildElement(edit, nodes, "sesso",
							rsViewUtente.getString("sesso"), false);
					datiXml.getConvert().addChildElement(edit, nodes,
							"residenzaIndirizzo",
							rsViewUtente.getString("residenzaIndirizzo"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "residenzaCitta",
							rsViewUtente.getString("residenzaCitta"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "residenzaCap",
							rsViewUtente.getString("residenzaCap"), false);
					datiXml.getConvert().addChildElement(edit, nodes,
							"domicilioIndirizzo",
							rsViewUtente.getString("domicilioIndirizzo"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "domicilioCitta",
							rsViewUtente.getString("domicilioCitta"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "domicilioCap",
							rsViewUtente.getString("domicilioCap"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "telefono",
							rsViewUtente.getString("telefono"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "cellulare",
							rsViewUtente.getString("cellulare"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "luogoNascita",
							rsViewUtente.getString("luogoNascita"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "dataNascita",
							MsSql.conveDateIta(rsViewUtente.getTimestamp("dataNascita")), false);
					datiXml.getConvert().addChildElement(edit, nodes, "numeroDocumento",
							rsViewUtente.getString("numeroDocumento"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "autoritaRilascio",
							rsViewUtente.getString("autoritaRilascio"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "codiceFiscale",
							rsViewUtente.getString("codiceFiscale"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "Note",
							rsViewUtente.getString("Note"), false);
					datiXml.getConvert().addChildElement(edit, nodes, "email",
							rsViewUtente.getString("email"), false);
//					edit.addChildElement(getSospensioni(rsViewUtente
//							.getString("idUtente"), rsViewUtente.getString("idAutorizzazioniUtente")));
					edit.addChildElement(initSospensioni());

					edit.addChildElement(initAutorizzazioni());
//					edit.addChildElement(getAutorizzazioni(rsViewUtente
//							.getString("idUtente"), rsViewUtente.getString("idAutorizzazioniUtente")));
				}
			}

			if (nuovo)
			{
				datiXml.getConvert().addChildElement(edit, nodes, "idUtente",
						"0", true);
				edit.addChildElement(getAutorizzazioniUtente(null,null));
				edit.addChildElement(getProfessione(null));
				edit.addChildElement(getProvenienza(null));
				edit.addChildElement(getCittadinanza(null));
				edit.addChildElement(getTipoDocumento(null));

			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsViewUtente != null)
					rsViewUtente.close();
				if (viewUtente != null)
					viewUtente.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
	}

	private MessageElement initAutorizzazioni()
	{
		MessageElement autorizzazioni = null;
		MessageElement servizi = null;
		GregorianCalendar data = new GregorianCalendar();

		try
		{
			autorizzazioni = new MessageElement();
			autorizzazioni.setName("autorizzazioni");

			servizi = new MessageElement();
			servizi.setName("servizi");

			datiXml.getConvert().addChildElement(servizi, "dataIni", MsSql.conveDateIta(data), true);
			data.add(Calendar.YEAR, 1);
			datiXml.getConvert().addChildElement(servizi, "dataFin", MsSql.conveDateIta(data), true);
			autorizzazioni.addChildElement(servizi);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		return autorizzazioni;
	}

	private MessageElement initSospensioni()
	{
		MessageElement autorizzazioni = null;
		MessageElement servizi = null;
		GregorianCalendar data = new GregorianCalendar();

		try
		{
			autorizzazioni = new MessageElement();
			autorizzazioni.setName("sospensioni");

			servizi = new MessageElement();
			servizi.setName("servizi");

			datiXml.getConvert().addChildElement(servizi, "dataIni", MsSql.conveDateIta(data), true);
			data.add(Calendar.YEAR, 1);
			datiXml.getConvert().addChildElement(servizi, "dataFin", MsSql.conveDateIta(data), true);
			autorizzazioni.addChildElement(servizi);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		return autorizzazioni;
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative
	 * all'autorizzazione Utente
	 * 
	 * @param idTipoDocumento
	 *          Identificativo dell'autorizzazione utente
	 * @return
	 */
	private MessageElement getTipoDocumento(String idTipoDocumento)
	{
		MessageElement tipiDocumenti = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		UtenteTipoDocumento tipoDocumento = null;
		ResultSet rsTipoDocumento = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			tipiDocumenti = new MessageElement();
			tipiDocumenti.setName("tipiDocumenti");

			tipoDocumento = new UtenteTipoDocumento(Configuration.poolUol2010);
			tipoDocumento.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsTipoDocumento = tipoDocumento.startSelect();
			while (rsTipoDocumento.next())
			{
				if (!rsTipoDocumento.getString("idUtenteTipoDocumento").trim().equals(
						""))
				{
					keyAttr = new Vector<String>();
					valueAttr = new Vector<String>();
					obbAttr = new Vector<String>();

					keyAttr.add("idUtenteTipoDocumento");
					valueAttr.add(rsTipoDocumento.getString("idUtenteTipoDocumento"));
					obbAttr.add("true");

					if (idTipoDocumento != null
							&& rsTipoDocumento.getString("idUtenteTipoDocumento").equals(
									idTipoDocumento))
					{
						keyAttr.add("selected");
						valueAttr.add("Yes");
						obbAttr.add("true");
					}
					datiXml.getConvert().addChildElement(tipiDocumenti, nodes,
							"tipoDocumento", rsTipoDocumento.getString("descrizione"),
							keyAttr, valueAttr, true, obbAttr);
				}
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsTipoDocumento != null)
					rsTipoDocumento.close();
				if (tipoDocumento != null)
					tipoDocumento.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return tipiDocumenti;
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative
	 * all'autorizzazione Utente
	 * 
	 * @param idUtenteCittadinanza
	 *          Identificativo dell'autorizzazione utente
	 * @return
	 */
	private MessageElement getCittadinanza(String idUtenteCittadinanza)
	{
		MessageElement cittadinanze = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		UtenteCittadinanza cittadinanza = null;
		ResultSet rsCittadinanza = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			cittadinanze = new MessageElement();
			cittadinanze.setName("cittadinanze");

			cittadinanza = new UtenteCittadinanza(Configuration.poolUol2010);
			cittadinanza.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsCittadinanza = cittadinanza.startSelect();
			while (rsCittadinanza.next())
			{
				if (!rsCittadinanza.getString("idUtenteCittadinanza").trim().equals(""))
				{
					keyAttr = new Vector<String>();
					valueAttr = new Vector<String>();
					obbAttr = new Vector<String>();

					keyAttr.add("idUtenteCittadinanza");
					valueAttr.add(rsCittadinanza.getString("idUtenteCittadinanza"));
					obbAttr.add("true");

					if (idUtenteCittadinanza != null
							&& rsCittadinanza.getString("idUtenteCittadinanza").equals(
									idUtenteCittadinanza))
					{
						keyAttr.add("selected");
						valueAttr.add("Yes");
						obbAttr.add("true");
					}
					datiXml.getConvert().addChildElement(cittadinanze, nodes,
							"cittadinanza", rsCittadinanza.getString("descrizione"), keyAttr,
							valueAttr, true, obbAttr);
				}
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsCittadinanza != null)
					rsCittadinanza.close();
				if (cittadinanza != null)
					cittadinanza.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return cittadinanze;
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative
	 * all'autorizzazione Utente
	 * 
	 * @param idAutorizzazioniUtente
	 *          Identificativo dell'autorizzazione utente
	 * @return
	 */
	private MessageElement getProvenienza(String idUtenteProvenienza)
	{
		MessageElement provenienze = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		UtenteProvenienza provenienza = null;
		ResultSet rsProvenienza = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			provenienze = new MessageElement();
			provenienze.setName("provenienze");

			provenienza = new UtenteProvenienza(Configuration.poolUol2010);
			provenienza.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsProvenienza = provenienza.startSelect();
			while (rsProvenienza.next())
			{
				if (!rsProvenienza.getString("idUtenteProvenienza").trim().equals(""))
				{
					keyAttr = new Vector<String>();
					valueAttr = new Vector<String>();
					obbAttr = new Vector<String>();

					keyAttr.add("idUtenteProvenienza");
					valueAttr.add(rsProvenienza.getString("idUtenteProvenienza"));
					obbAttr.add("true");

					if (idUtenteProvenienza != null
							&& rsProvenienza.getString("idUtenteProvenienza").equals(
									idUtenteProvenienza))
					{
						keyAttr.add("selected");
						valueAttr.add("Yes");
						obbAttr.add("true");
					}
					datiXml.getConvert().addChildElement(provenienze, nodes,
							"provenienza", rsProvenienza.getString("descrizione"), keyAttr,
							valueAttr, true, obbAttr);
				}
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsProvenienza != null)
					rsProvenienza.close();
				if (provenienza != null)
					provenienza.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return provenienze;
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative
	 * all'autorizzazione Utente
	 * 
	 * @param idAutorizzazioniUtente
	 *          Identificativo dell'autorizzazione utente
	 * @return
	 */
	private MessageElement getProfessione(String idUtenteProfessione)
	{
		MessageElement professioni = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		UtenteProfessione professione = null;
		ResultSet rsProfessione = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			professioni = new MessageElement();
			professioni.setName("professioni");

			professione = new UtenteProfessione(Configuration.poolUol2010);
			professione.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
			rsProfessione = professione.startSelect();
			while (rsProfessione.next())
			{
				if (!rsProfessione.getString("idUtenteProfessione").trim().equals(""))
				{
					keyAttr = new Vector<String>();
					valueAttr = new Vector<String>();
					obbAttr = new Vector<String>();

					keyAttr.add("idUtenteProfessione");
					valueAttr.add(rsProfessione.getString("idUtenteProfessione"));
					obbAttr.add("true");

					if (idUtenteProfessione != null
							&& rsProfessione.getString("idUtenteProfessione").equals(
									idUtenteProfessione))
					{
						keyAttr.add("selected");
						valueAttr.add("Yes");
						obbAttr.add("true");
					}
					datiXml.getConvert().addChildElement(professioni, nodes,
							"professione", rsProfessione.getString("descrizione"), keyAttr,
							valueAttr, true, obbAttr);
				}
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsProfessione != null)
					rsProfessione.close();
				if (professione != null)
					professione.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return professioni;
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni relative
	 * all'autorizzazione Utente
	 * 
	 * @param idAutorizzazioniUtente
	 *          Identificativo dell'autorizzazione utente
	 * @return
	 */
	private MessageElement getAutorizzazioniUtente(String idAutorizzazioniUtente, String descAutUte)
	{
		MessageElement autorizzazioniUte = null;
		ArrayList<MessageElement> nodes = new ArrayList<MessageElement>();
		AutorizzazioniUte autor = null;
		ResultSet rsAutor = null;
		Vector<String> keyAttr = null;
		Vector<String> valueAttr = null;
		Vector<String> obbAttr = null;

		try
		{
			autorizzazioniUte = new MessageElement();
			autorizzazioniUte.setName("autorizzazioniUtente");
			if (idAutorizzazioniUtente == null
					|| Autorizzazione.checkDiritti(idAutorizzazioniUtente, bancoXsd))
			{
				datiXml.getConvert().setAttribute(autorizzazioniUte, "editing",
						"true", true);
				autor = new AutorizzazioniUte(Configuration.poolUol2010);
				autor.getCampo("descrizione").setOrderBy(Column.ORDERBY_CRES, 1);
				rsAutor = autor.startSelect();
				while (rsAutor.next())
				{
					if (!rsAutor.getString("idAutorizzazioniUtente").trim().equals(""))
					{

						if (Autorizzazione.checkDiritti(rsAutor.getString("idAutorizzazioniUtente"), bancoXsd))
						{
							keyAttr = new Vector<String>();
							valueAttr = new Vector<String>();
							obbAttr = new Vector<String>();

							keyAttr.add("idAutorizzazioneUtente");
							valueAttr.add(rsAutor.getString("idAutorizzazioniUtente"));
							obbAttr.add("true");

							if (idAutorizzazioniUtente != null
									&& rsAutor.getString("idAutorizzazioniUtente").equals(
											idAutorizzazioniUtente))
							{
								keyAttr.add("selected");
								valueAttr.add("Yes");
								obbAttr.add("true");
							}
							datiXml.getConvert().addChildElement(autorizzazioniUte, nodes,
									"autorizzazioneUtente", rsAutor.getString("descrizione"),
									keyAttr, valueAttr, true, obbAttr);
						}
					}
				}
			}
			else
			{
				datiXml.getConvert().setAttribute(autorizzazioniUte, "editing",
						"false", true);
				datiXml.getConvert().setAttribute(autorizzazioniUte, "idAutorizzazioniUtente",
						idAutorizzazioniUtente, true);
				datiXml.getConvert().setAttribute(autorizzazioniUte, "descAutUte",
						descAutUte, true);
				
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsAutor != null)
					rsAutor.close();
				if (autor != null)
					autor.stopSelect();
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return autorizzazioniUte;
	}
}
