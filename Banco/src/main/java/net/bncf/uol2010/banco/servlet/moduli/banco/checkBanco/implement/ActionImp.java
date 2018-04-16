package net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.implement;


import java.sql.Timestamp;
import java.util.GregorianCalendar;

import net.bncf.uol2010.banco.gestioneFunzioni.ActionResult;
import net.bncf.uol2010.banco.gestioneFunzioni.InfoRichieste;
import net.bncf.uol2010.configuration.Configuration;
import net.bncf.xsd.BancoXsd;
import net.bncf.uol2010.banco.security.exception.SecurityException;

import mx.log4j.Logger;

/**
 * Questa classe viene implementata per la gestione delle diverse classi necessarie 
 * per la gestione delle azioni della Servlet
 * 
 * @author Randazzo Massimiliano
 *
 */
public class ActionImp 
{

	private static Logger log = new Logger(ActionImp.class, "net.bncf.uol2010.banco.servlet.moduli.banco");

  /**
   * Questo metodo viene utilizzato per gestire tutto il risultato relativo 
   * all'implementazione della Servlet
   */
  protected ActionResult actionResult = null;
	
  /**
   * Questa variabile viene utilizzata per indicare il tipo di Autenticazione deve essere verificare 
   * dal modulo della sicurezza
   */
  protected String checkAut = "Banco";

  /**
   * Questa variabile viene utilizzata per gestire la letture delle informazioni dal database
   */
  protected InfoRichieste infoRic = null;

  protected BancoXsd banco = null;

  /**
   * Questa variabile viene utilizzata per verificare tutto quanto che riguarda la Sicurezza,
   * autenticazione ecc...
  protected Security security = null;
   */
  
  /**
   * Costrutore
   */
  public ActionImp()
  {
    super();
  }

  /**
   * Questo metodo viene utilizzato per eseguire l'attivit� designata alla classe
   * @throws SecurityException 
   *
   */
  public void esegui() throws SecurityException
  {
//    String sc_user = "";
//    String sc_pwd = "";
    GregorianCalendar gcStart = null;
    GregorianCalendar gcStop = null;

/*
    log.debug("Security Check: "+actionResult.isSecurityCheck());
    if (actionResult.isSecurityCheck())
    {
      security = new Security();

      security.setTimeOut(actionResult.getSecurityTimeOut());
      security.setIP(actionResult.getRequest().getRemoteAddr());
      security.setConn(actionResult.getConn());
      security.setDebug(actionResult.isDebug());
      security.setConnessionWeb(actionResult.getRequest(), actionResult.getResponse());

      log.debug("scUser: "+actionResult.getRequest().getParameter("sc_user"));
      if (actionResult.getRequest().getParameter("sc_user") != null)
        sc_user = actionResult.getRequest().getParameter("sc_user");

      log.debug("scPwd: "+actionResult.getRequest().getParameter("sc_pwd"));
      if (actionResult.getRequest().getParameter("sc_pwd") != null)
        sc_pwd = actionResult.getRequest().getParameter("sc_pwd");

      if (sc_user.length()>0 && sc_pwd.length()>0)
        security.insAccess(sc_user,sc_pwd);

      gcStart= new GregorianCalendar(); 
      security.readInfo();
      gcStop = new GregorianCalendar();
      if ((gcStop.getTimeInMillis()-gcStart.getTimeInMillis())>1000)
      	log.fatal("Tempo Lettura Informazioni Security di "+(gcStop.getTimeInMillis()-gcStart.getTimeInMillis())+" millisec \n"+security.getLogin());

      gcStart= new GregorianCalendar(); 
      security.checkCon();
      gcStop = new GregorianCalendar();
      if ((gcStop.getTimeInMillis()-gcStart.getTimeInMillis())>1000)
      	log.fatal("Tempo Check Connessione Security di "+(gcStop.getTimeInMillis()-gcStart.getTimeInMillis())+" millisec \n"+security.getLogin());

      gcStart= new GregorianCalendar(); 
      security.checkAut(checkAut);
      gcStop = new GregorianCalendar();
      if ((gcStop.getTimeInMillis()-gcStart.getTimeInMillis())>1000)
      	log.fatal("Tempo Check Autorizzazione Security ["+checkAut+"] di "+(gcStop.getTimeInMillis()-gcStart.getTimeInMillis())+" millisec \n"+security.getLogin());
    }
    */
    if (checkAut.equals("Banco"))
    {
      infoRic =  new InfoRichieste(Configuration.poolUol2010);

      if (actionResult.getRequest().getParameter("value") != null)
      	infoRic.setCampoValue("idRichieste",actionResult.getRequest().getParameter("value"));
      else
      	infoRic.setCampoValue("idRichieste",actionResult.getRequest().getParameter("idMovimento"));
//      infoRic.setActionResult(actionResult);

      gcStart= new GregorianCalendar(); 
      infoRic.read();
      gcStop = new GregorianCalendar();
      if ((gcStop.getTimeInMillis()-gcStart.getTimeInMillis())>1000)
      	log.fatal("Tempo Lettura Informazioni Movimento di "+(gcStop.getTimeInMillis()-gcStart.getTimeInMillis())+" millisec \n"+
      			(actionResult.getRequest().getParameter("value") != null?actionResult.getRequest().getParameter("value"):actionResult.getRequest().getParameter("idMovimento")));
    }
  }

  /**
   * Questo metodo viene utilizzato per popolare le informazioni relative al riquadro della
   * Notizia
   *
   */
  protected void viewNotizia()
  {
    log.debug("viewNotizia");
    actionResult.setViewNotizia(true);
    if (infoRic.get("idUtente") != null)
    {
    	log.debug("cod. Utente: "+infoRic.get("idUtente"));
      actionResult.setIdUtente(infoRic.get("idUtente"));
    }
    if (infoRic.getCognomeNome() != null)
    {
    	log.debug("Cognome Nome: "+infoRic.getCognomeNome());
      actionResult.setCognomeNome(infoRic.getCognomeNome());
    }
    if (infoRic.get("Autore") != null)
    {
    	log.debug("Autore: "+infoRic.get("Autore"));
      actionResult.setAutore(infoRic.get("Autore"));
    }
    if (infoRic.get("Titolo") != null)
    {
    	log.debug("Titolo: "+infoRic.get("Titolo"));
      actionResult.setTitolo(infoRic.get("Titolo"));
    }
    if (infoRic.getCheckDeposito() != null &&
        infoRic.getCheckDeposito().equals("Y"))
    {
    	log.debug("Descr Atti: "+infoRic.getDescrAtti());
      actionResult.addDescrAtt(infoRic.getDescrAtti());
    }
    if (infoRic.get("noteUte") != null)
    {
    	log.debug("Note Ute: "+infoRic.get("noteUte"));
      actionResult.addNote(infoRic.get("noteUte"));
    }
    if (infoRic.get("noteBib") != null)
    {
    	log.debug("Note Bib: "+infoRic.get("noteBib"));
      actionResult.setNoteBib(infoRic.get("noteBib"));
      actionResult.setViewNote(true);
    }
    if (infoRic.get("dataFinEff") != null && !(infoRic.get("dataFinEff").startsWith("31/12/9999")))
    {
    	log.debug("Data fine Eff: "+infoRic.get("dataFinEff"));
      actionResult.setScadenza((Timestamp)infoRic.getCampo("dataFinEff").getValue());
    }
    else if (infoRic.get("dataFinPrev") != null)
    {
      log.debug("Data fine Prev: "+infoRic.get("dataFinPrev"));
      actionResult.setScadenza((Timestamp)infoRic.getCampo("dataFinPrev").getValue());
    }
  }

  /**
   * Questo metodo viene utilizzato per valorizzare la classe utilizzata per getiste 
   * tutto il risultato relativo alla implementazione della Servlet
   * @param actionResult
   */
  public void setActionResult(ActionResult actionResult)
  {
    this.actionResult = actionResult;
  }

	/**
	 * @param banco the banco to set
	 */
	public void setBanco(BancoXsd banco)
	{
		this.banco = banco;
	}
  
}
