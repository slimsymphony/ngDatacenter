package com.nokia.granite.analyzer;

import java.util.Collection;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class MailService {
	public static void sendMail( Collection<String> targets, Collection<String> ccs, String topic, String details ) throws EmailException {
		HtmlEmail mail = new HtmlEmail();
		mail.setCharset( "UTF-8" );
		for ( String to : targets ) {
			mail.addTo( to );
		}
		if ( ccs != null )
			for ( String cc : ccs ) {
				mail.addCc( cc );
			}
		mail.setFrom( "SCVDataCenter@nokia.com" );
		mail.setHostName( "smtp.nokia.com" );
		mail.setSubject( topic );
		mail.setHtmlMsg( details );
		mail.send();
	}
	
	public static void main(String[] args) throws Exception {
		//{"frank.8.wang@nokia.com"}
		sendMail(null,null,"Hello","");
	}
}
