/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;

import mx.randalf.moduli.servlet.core.exception.StdModuliException;
import mx.randalf.moduli.servlet.core.exception.StdNotificheException;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

/**
 * Questa clase viene utilizzata per gestire la home page delle Richieste
 * 
 * @author Massimiliano Randazzo
 * 
 */
public class Banco extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(Banco.class);

	/**
	 * Costruttore
	 */
	public Banco()
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
		log.debug("Banco - edit");
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException
	{
		log.debug("Banco - extend");
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "Banco.xsl";
		datiXml.setTitle("Banco");
		datiXml.addStyleSheet("../style/Banco.css");

		datiXml.addJavaScript("../js/mx/testJS.js");
		datiXml.addJavaScript("../js/mx/AccessoAjax.js");
		datiXml.addJavaScript("../js/mx/div.js");
		datiXml.addJavaScript("../js/mx/span.js");
		datiXml.addJavaScript("../js/mx/input.js");
		datiXml.addJavaScript("../js/mx/table.js");
		datiXml.addJavaScript("../js/banco/TastoFunzione.js");
		datiXml.addJavaScript("../js/banco/Banco.js");
		datiXml.addJavaScript("../js/banco/Security.js");


		element = new MessageElement();
		element.setName("banco");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException
	{
		log.debug("Banco - result");
		datiXml.addElement(element);
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#show(java.lang.String)
	 */
	@Override
	protected void show(String arg0) throws ServletException, IOException
	{
//		Show show = null;
		MessageElement showEle = null;
		
		try
		{
			log.debug("Banco - show");
			showEle = new MessageElement();
			showEle.setName("show");
			if (request.getParameter("value") != null &&
					!request.getParameter("value").equals(""))
				datiXml.getConvert().addChildElement(showEle, "value", request.getParameter("value"), true);
//			show =  new Show(richieste,request, poolTeca);
//			showEle = show.execute(datiXml);
//			if (showEle != null)
//			{
//				datiXml.addStyleSheet("../style/Home/show.css");
//				datiXml.addJavaScript("../js/Home/show.js");
				element.addChildElement(showEle);
//			}
		}
		catch (SOAPException e)
		{
			log.error(e);
		}
//		catch (StdModuliException e)
//		{
//			this.setMsgErr(e.getMessage());
//		}
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#write()
	 */
	@Override
	protected void write() throws ServletException, IOException, StdModuliException, StdNotificheException
	{
		log.debug("Banco - write");
	}

}
