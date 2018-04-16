/**
 * 
 */
package net.bncf.uol2010.banco.servlet.moduli;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.soap.SOAPException;


import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import mx.randalf.moduli.servlet.core.exception.StdModuliException;

/**
 * Questa clase viene utilizzata per gestire la home page delle Richieste
 * 
 * @author Massimiliano Randazzo
 * 
 */
public class Home extends StdModuliBanco
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(Home.class);

	/**
	 * Costruttore
	 */
	public Home()
	{
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#edit()
	 */
	@Override
	protected void edit() throws ServletException, IOException
	{
		log.debug("Home - edit");
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#extend()
	 */
	@Override
	protected void extend() throws ServletException, IOException
	{
		log.debug("Home - extend");
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#init()
	 */
	@Override
	protected void init()
	{
		this.fileXsl = "Home.xsl";
		datiXml.setTitle("Home");
//		datiXml.addStyleSheet("../style/Home.css");
		// datiXml.addJavaScript("../js/home.js");

		element = new MessageElement();
		element.setName("home");
		super.init();
	}

	/**
	 * @see mx.servlet.moduli.standard.StdModuli#result()
	 */
	@Override
	protected void result() throws ServletException, IOException
	{
		log.debug("Home - result");
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
			showEle = new MessageElement();
			showEle.setName("show");
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
	protected void write() throws ServletException, IOException,
			StdModuliException
	{
		log.debug("Home - write");
	}

}
