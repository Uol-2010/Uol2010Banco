/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli.banco;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import mx.database.ConnectionPool;
import mx.log4j.Logger;
import mx.security.Access;
import mx.servlet.moduli.servlet.MxMultipartRequest;
import mx.servlet.moduli.standard.exception.StdModuliException;
import net.bncf.uol2010.banco.gestioneFunzioni.ActionResult;
import net.bncf.uol2010.banco.servlet.moduli.StdModuliBanco;
import net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.implement.ActionImp;
import net.bncf.uol2010.banco.security.exception.SecurityException;

/**
 * @author massi
 *
 */
public class CheckBanco extends StdModuliBanco
{

	private Logger log = new Logger(CheckBanco.class, "net.bncf.uol2010.banco.servlet.moduli.banco");
	/**
	 * 
	 */
	public CheckBanco()
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#show(java.lang.String)
	 */
	@Override
	protected void show(String id) throws ServletException, IOException
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
	}

	/**
	 * @see net.bncf.validate.moduli.standard.StdModuliAuthentication#esegui(mx.servlet.moduli.servlet.MxMultipartRequest, javax.servlet.http.HttpServletResponse, mx.database.ConnectionPool, mx.database.ConnectionPool, java.lang.String, mx.security.Access)
	 */
	@Override
	public void esegui(MxMultipartRequest request, HttpServletResponse response,
			ConnectionPool poolTeca, ConnectionPool poolGestionale, String pathXsl,
			Access access) throws ServletException, IOException
	{
    Class myClass = null;
    ActionImp actionImp = null; 
    String className = "";
    ActionResult actionResult = null;
    String xml = "";

    try
		{
			actionResult = new ActionResult();
			actionResult.setRequest(request);
			actionResult.setResponse(response);
//    actionResult.setSecurityPath(securityPath);
//    actionResult.setSecurityTimeOut(securityTimeOut);
//    actionResult.setSecurityCheck(securityCheck);
//    actionResult.setCodBib(codBib);
//    actionResult.setPropertiesIll(propertiesIll);
//    actionResult.setChiamate(Banco.chiamate);
			className = "net.bncf.uol2010.banco.servlet.moduli.banco.checkBanco.moduli."+request.getParameter("azione").substring(0,1).toUpperCase()+request.getParameter("azione").substring(1);
			myClass = Class.forName(className);
			actionImp = (ActionImp) myClass.newInstance();
			actionImp.setActionResult(actionResult);
			actionImp.esegui();
		}
		catch (SecurityException e)
		{
			log.error(e);
		}
		catch (ClassNotFoundException e)
		{
			log.error(e);
		}
		catch (InstantiationException e)
		{
			log.error(e);
		}
		catch (IllegalAccessException e)
		{
			log.error(e);
		}
		finally
		{
      try
      {
      	log.debug("Finally Start");
/*
      	log.debug("Security: "+security);
        if (security)
        {
          response.setContentType("text/plain");
          response.addHeader("Cache-Control", "must-revalidate");
          response.addHeader("Cache-Control", "no-cache");
          response.addHeader("Cache-Control", "no-store");
          response.setDateHeader("Expires", 0);
          if (msgSecurity.equals(""))
          {
            response.getOutputStream().print("OK "+actionResult.getLogin()+"\n"+actionResult.getSession());
          }
          else
          {
            response.getOutputStream().print(msgSecurity+"\n"+actionResult.getSession());
          }
          log.debug("text/plain");
          log.debug(response.getContentType());
        }
        else
        {
        */
          log.debug(response.getContentType());
          response.setContentType("text/xml");
          log.debug(response.getContentType());
          response.addHeader("Cache-Control", "must-revalidate");
          response.addHeader("Cache-Control", "no-cache");
          response.addHeader("Cache-Control", "no-store");
          response.setDateHeader("Expires", 0);
    			response.setCharacterEncoding("utf-8");
    			xml = actionResult.printResult();
    			log.info("XML: "+xml);
          response.getOutputStream().print(xml);
          log.debug("text/xml");
          log.debug(response.getContentType());
//        }
      	log.debug("Finally End");
      }
      catch (IOException e)
      {
      	log.error(e);
      }
      finally
      {
      	try
				{
					response.flushBuffer();
				}
				catch (IOException e)
				{
	      	log.error(e);
				}
      }
		}
	}

}
