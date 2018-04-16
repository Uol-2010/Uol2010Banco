package net.bncf.uol2010.banco.security.exception;

/**
  * Questa classe serve per la gesione degli errore del modulo di sicurezza
  * gli errori gestiti sono:
  * <OL>
  *   <LI> Utente non ancora Autenticato
  *   <LI> Tempo di Connesione scaduto
  *   <LI> Impossibile trovare i dati del profilo dell'utente 
  *   <LI> Impossibile trovare i dati relativi all'utente 
  *   <LI> Impossibile trovare un accesso valido per l'indirizzo 
  *   <LI> Password dell'utente � errata controllare
  *   <LI> Utente non autorizzato per la stazione 
  *   <LI> Utente non autorizzato per questo servizio
  * </OL>
  * @author Randazzo Massimiliano
  * @version 1.0
  */
public class SecurityException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 4892001302369137219L;

  /**
    * Codice di Errore
    */
  private int CodError;

  /**
    * Messaggio di Errore
    */
  private String MsgError;
  
  /**
    * Costruttore della classe SecurityException
    */
  public SecurityException()
  {
    super();
  }

  /**
    * Costruttore della classe SecurityException
    * @param Msg Messaggio di Errore
    */
  public SecurityException(String Msg)
  {
    super(Msg);
  }

  /**
    * Costruttore della classe SecurityException
    * @param Cod Codice di Errore
    * @param Msg Messaggio di Errore
    */
  public SecurityException(int Cod, String Msg)
  {
    super(Msg);
    CodError = Cod;
    decMsg(Cod, Msg);
  }

  /**
    * Costruttore della classe SecurityException
    * @param Cod Codice di Errore
    * @param Msg Messaggio di Errore
    */
  public SecurityException(int Cod, String Msg, String TipoAut)
  {
    super(Msg);
    CodError = Cod;
    decMsg(Cod, Msg, TipoAut);
  }

  /**
    * Costruttore della classe SecurityException
    * @param Cod Codice di Errore
    */
  public SecurityException(int Cod)
  {
    super();
    CodError = Cod;
    decMsg(Cod, "");
  }

  /**
    * Questo metodo serve per decodificare il codice di errore
    * @param Cod Codice di Errore
    * @param Msg Messaggio da allegare al codice
    */
  private void decMsg(int Cod, String Msg)
  {
    decMsg(Cod, Msg,"");
  }
  /**
   * Questo metodo serve per decodificare il codice di errore
   * @param Cod Codice di Errore
   * @param Msg Messaggio da allegare al codice
   */
 private void decMsg(int Cod, String Msg, String TipoAut)
  {
    if (Cod == 1)
    {
      MsgError = "Utente non ancora Autenticato";
    }
    else if (Cod == 2)
    {
      MsgError = "Tempo di Connesione scaduto";
    }
    else if (Cod == 3)
    {
      MsgError = "Impossibile trovare i dati del profilo dell'utente ["+Msg+"]";
    }
    else if (Cod == 4)
    {
      MsgError = "Impossibile trovare i dati relativi all'utente ";
    }
    else if (Cod == 5)
    {
      MsgError = "Impossibile trovare un accesso valido per l'indirizzo ["+Msg+"]";
    }
    else if (Cod == 6)
    {
      MsgError = "Password dell'utente ["+Msg+"] &egrave; errata controllare";
    }
    else if (Cod == 7)
    {
      MsgError = "Utente non autorizzato per la stazione ["+Msg+"]";
    }
    else if (Cod == 8)
    {
      MsgError = "Operatore non autorizzato per il servizio ["+TipoAut+"]";
    }
    else if (Cod == 9)
    {
      MsgError = "Utente scollegato";
    }
    else
    {
      MsgError = "Errore Generico";
    }
  }
  
  /**
    * Lettura messaggio di Errore
    * @return Messaggio di Errore
    */
  public String getMsgError()
  {
    return MsgError;
  }

  /**
    * Lettura codice di Errore
    * @return Codice di Errore
    */
  public int getCodError()
  {
    return CodError;
  }

}

