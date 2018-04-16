/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.anagraficaUtente;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.apache.log4j.Logger;

import net.bncf.uol2010.banco.servlet.moduli.exception.ModuliException;
import net.bncf.uol2010.database.schema.servizi.entity.Utente;
import net.bncf.uol2010.utility.xsd.StampanteXsd;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import mx.randalf.configuration.Configuration;

/**
 * Questa classe viene utilizzata per eseguire la stampa della scheda utente
 * 
 * @author Massimiliano Randazzo
 *
 */
public class Print
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = Logger.getLogger(Print.class);

	/**
	 * Costruttore
	 */
	public Print()
	{
		log.debug("Costruttore Print");
	}

	/**
	 * Questo metodo viene utilizzato per Stampare la scheda Utente
	 * @param idUtente
	 */
	public void print(Utente utente)
	{
//		ViewUtente viewUtente = null;
//		ResultSet rsViewUtente = null;
		File fTmpXml = null;
		File fTmpHtml = null;
		File fTmpPs = null;
		StampanteXsd stampanteXsd = null;

		try
		{
			log.debug("IdUtente: "+utente.getId());
			if (utente != null)
			{
					log.debug("Trovato almeno 1 record");
					try
					{
						fTmpXml = new File(Configuration.getValueDefault("anagraficaUtente.pathTemp", ".")+
								File.separator+utente.getId().replace(" ", "")+".xml");
						log.debug("file Tmp: "+fTmpXml.getAbsolutePath());
						if (!fTmpXml.getParentFile().exists())
							if (!fTmpXml.getParentFile().mkdirs())
								throw new  ModuliException("Problemi nella creazione della Cartella ["+fTmpXml.getParentFile().getAbsolutePath()+"]");

						fTmpHtml = new File(fTmpXml.getAbsolutePath()+".html");
						log.debug("file Tmp: "+fTmpHtml.getAbsolutePath());

						fTmpPs = new File(fTmpHtml.getAbsolutePath()+".ps");
						log.debug("file Tmp: "+fTmpPs.getAbsolutePath());

						log.debug("Generazione Stampante Xsd");
						stampanteXsd = genStampanteXsd(utente);

						genFileXml(fTmpXml, stampanteXsd);

						if (genFileHtml(fTmpHtml, stampanteXsd))
						{
							if (genFilePs(fTmpPs,fTmpHtml))
							{
								log.debug("Stampo richiesta alla coda : "+(String)Configuration.listaParametri.get("anagraficaUtente.codaStampante", "tessere"));
								if (!PrintPS.print(fTmpPs.getAbsolutePath(), 
										(String)Configuration.listaParametri.get("anagraficaUtente.codaStampante", "tessere")))
									log.error("Problemi durante la procedura di stampa sulla porta ["+(String)Configuration.listaParametri.get("anagraficaUtente.codaStampante", "tessere")+"]");
							}
							else
								log.error("Problemi nella creazione del file "+fTmpPs.getAbsolutePath());
						}
						else
							log.error("Problemi nella creazione del file "+fTmpHtml.getAbsolutePath());
					}
					catch (FileNotFoundException e)
					{
						log.error(e);
					}
					catch (SQLException e)
					{
						throw e;
					}
					catch (PropertyException e)
					{
						log.error(e);
					}
					catch (JAXBException e)
					{
						log.error(e);
					}
					catch (Exception e)
					{
						log.error(e);
					}
					finally
					{
						
						if (fTmpXml.exists())
						{
							log.debug("Cancello il File "+fTmpXml.getAbsolutePath());
								if (!fTmpXml.delete())
									throw new ModuliException("Problemi nella cancellazione del file ["+fTmpXml.getAbsolutePath()+"]");
						}
							
						if (fTmpHtml.exists())
						{
							log.debug("Cancello il File "+fTmpHtml.getAbsolutePath());
								if (!fTmpHtml.delete())
									throw new ModuliException("Problemi nella cancellazione del file ["+fTmpHtml.getAbsolutePath()+"]");
						}
						if (fTmpPs.exists())
						{
							log.debug("Cancello il File "+fTmpPs.getAbsolutePath());
								if (!fTmpPs.delete())
									throw new ModuliException("Problemi nella cancellazione del file ["+fTmpPs.getAbsolutePath()+"]");
						}
					}
				}
				else
					throw new ModuliException("Il codice Utnte ["+idUtente+"] non \u00E8 stato travato in base dati");
			}
			else
				throw new ModuliException("Non \u00E8 stato indicato il codice Utente da stampare");
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (ModuliException e)
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

	/**
	 * Questo metodo viene utilizzato per la generazione del file PS
	 * 
	 * @param fTmpPs
	 * @param fTmpHtml
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private boolean genFilePs(File fTmpPs, File fTmpHtml) throws FileNotFoundException, IOException
	{
		boolean ris = false;
		FileOutputStream fosPs = null;

		try
		{
			fosPs = new FileOutputStream(fTmpPs.getAbsolutePath());
			log.debug("Conversione Html in posscript");
			ris = Html2ps.convert((String)Configuration.listaParametri.get("demoniStampante.fileHtml2ps", "html2ps"), 
					fTmpHtml.getAbsolutePath(), fosPs);
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (fosPs != null)
				{
					fosPs.flush();
					fosPs.close();
				}
			}
			catch (IOException e)
			{
				throw e;
			}
		}
    return ris;
	}

	/**
	 * Questo metodo viene utilizzato per la creazione del file Html
	 * 
	 * @param fTmpHtml
	 * @param stampanteXsd
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private boolean genFileHtml(File fTmpHtml, StampanteXsd stampanteXsd) throws FileNotFoundException, IOException
	{
		FileOutputStream fos = null;
		boolean ris = false;
		
		try
		{
			log.debug("preparo outString");
			fos = new FileOutputStream(fTmpHtml.getAbsolutePath());
			log.debug("Converto Xml in HTML");
			ris = Xml2Html.convert(stampanteXsd, 
					(String)Configuration.listaParametri.get("anagraficaUtente.fileXsl", "./AnagraficaUtente.xsl"), 
					fos);
			log.debug("Ris: "+ris);
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (fos != null)
				{
					fos.flush();
					fos.close();
					System.gc();
				}
			}
			catch (IOException e)
			{
				throw e;
			}
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per la generazione del file Xml
	 * 
	 * @param fTmpXml
	 * @param stampanteXsd
	 * @throws PropertyException
	 * @throws JAXBException
	 * @throws Exception
	 */
	private void genFileXml(File fTmpXml, StampanteXsd stampanteXsd) throws PropertyException, JAXBException, Exception
	{

		try
		{
			log.debug("Scrivo il file Xml: "+fTmpXml.getAbsolutePath());
			stampanteXsd.write(fTmpXml.getAbsolutePath(), false);
		}
		catch (PropertyException e)
		{
			throw e;
		}
		catch (JAXBException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw e;
		}
		
	}

	/**
	 * Questo metodo viene utilizzato per generare l'oggetto Stampante Xsd
	 * 
	 * @param rsViewUtente
	 * @return
	 * @throws SQLException 
	 */
	private StampanteXsd genStampanteXsd(ResultSet rsViewUtente) throws SQLException
	{
		StampanteXsd stampanteXsd = null;

		try
		{
			stampanteXsd = new StampanteXsd();
			stampanteXsd.setStampante(genStampante(rsViewUtente));
		}
		catch (SQLException e)
		{
			throw e;
		}
		return stampanteXsd;
	}

	/**
	 * Questo metodo viene utilizzato per generare l'oggetto Stampante
	 * 
	 * @param rsViewUtente
	 * @return
	 * @throws SQLException 
	 */
	private Stampante genStampante(ResultSet rsViewUtente) throws SQLException
	{
		Stampante stampante = null;

		try
		{
			stampante = new Stampante();
			stampante.setAnagraficaUtente(genAnagraficaUtente(rsViewUtente));
		}
		catch (SQLException e)
		{
			throw e;
		}
		return stampante;
	}

	/**
	 * Questo metoo viene utilizzato per generare le informazioni relative all'anagrafica Utente
	 * 
	 * @param rsViewUtente
	 * @return
	 * @throws SQLException
	 */
	private AnagraficaUtente genAnagraficaUtente(ResultSet rsViewUtente) throws SQLException
	{
		AnagraficaUtente anagraficaUtente = null;
		GregorianCalendar gc = null;
		String barcode = null;

		try
		{
			anagraficaUtente = new AnagraficaUtente();
			anagraficaUtente.setIdUtente(rsViewUtente.getString("idUtente"));
			anagraficaUtente.setCognome(rsViewUtente.getString("cognome"));
			if (rsViewUtente.getString("nome").trim().length()>20){
				anagraficaUtente.setNome(rsViewUtente.getString("nome").trim().substring(0, 20)+"....");
			}else{
				anagraficaUtente.setNome(rsViewUtente.getString("nome"));
			}
			if (rsViewUtente.getString("email")!= null)
				anagraficaUtente.setEmail(rsViewUtente.getString("email"));

			anagraficaUtente.setColore((String)Configuration.listaParametri.get("anagraficaUtente.stampaUtente."+rsViewUtente.getString("idAutorizzazioniUtente")));
			anagraficaUtente.setRigaVerticale((String)Configuration.listaParametri.get("anagraficaUtente.stampaUtente.rigaVer"));
			anagraficaUtente.setRigaOrizzontale((String)Configuration.listaParametri.get("anagraficaUtente.stampaUtente.rigaOri"));

			gc = new GregorianCalendar();
			gc.setTimeInMillis(rsViewUtente.getTimestamp("dataIns").getTime());

			anagraficaUtente.setAutorizzazione(new Autorizzazione());
			anagraficaUtente.getAutorizzazione().setDataInizio(new XMLGregorianCalendarImpl(gc));

			anagraficaUtente.getAutorizzazione().setCodiceTipo(rsViewUtente.getString("descAutorizzazioneUte"));

			barcode = (String) Configuration.listaParametri.get("demoniStampante.urlBarcode", ".");
			if (((String) Configuration.listaParametri.get("demoniStampante.urlBarcode.asterisco", "true")).equalsIgnoreCase("true")){
				barcode += "*";
			}
			barcode += rsViewUtente.getString("idUtente").replace(" ", "%20")+"U";
			if (((String) Configuration.listaParametri.get("demoniStampante.urlBarcode.asterisco", "true")).equalsIgnoreCase("true")){
				barcode += "*";
			}
			anagraficaUtente.setBarcode(barcode);
			anagraficaUtente.setProfessione(rsViewUtente.getString("descUtenteProfessione"));

			anagraficaUtente.setResidenza(new Residenza());
			anagraficaUtente.getResidenza().setIndirizzo(rsViewUtente.getString("residenzaIndirizzo"));
			anagraficaUtente.getResidenza().setCitta(rsViewUtente.getString("residenzaCitta"));
			anagraficaUtente.getResidenza().setPaese(rsViewUtente.getString("descUtenteCittadinanza"));

			anagraficaUtente.setDomicilio(new Domicilio());
			anagraficaUtente.getDomicilio().setIndirizzo(rsViewUtente.getString("domicilioIndirizzo"));
			anagraficaUtente.getDomicilio().setCitta(rsViewUtente.getString("domicilioCitta"));
			anagraficaUtente.getDomicilio().setTelefono(rsViewUtente.getString("telefono"));

			anagraficaUtente.setNascita(new Nascita());
			anagraficaUtente.getNascita().setLuogo(rsViewUtente.getString("luogoNascita"));

			gc = new GregorianCalendar();
			gc.setTimeInMillis(rsViewUtente.getTimestamp("dataNascita").getTime());
			anagraficaUtente.getNascita().setData(new XMLGregorianCalendarImpl(gc));

			anagraficaUtente.setDocumento(new Documento());
			anagraficaUtente.getDocumento().setTipo(rsViewUtente.getString("descUtenteTipoDocumento"));
			anagraficaUtente.getDocumento().setNumeroDocumento(rsViewUtente.getString("numeroDocumento"));
			anagraficaUtente.getDocumento().setAutoritaRilascio(rsViewUtente.getString("autoritaRilascio"));
			anagraficaUtente.setDataGiorno(new XMLGregorianCalendarImpl(new GregorianCalendar()));
		}
		catch (SQLException e)
		{
			throw e;
		}
		return anagraficaUtente;
	}
}
