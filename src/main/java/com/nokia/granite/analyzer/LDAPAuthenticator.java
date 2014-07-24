package com.nokia.granite.analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPAuthenticator {
	public static final String LDAP_AREA = "europe";

	private StringBuffer ldapInfo = new StringBuffer();

	public void getAllUsers( String areaName ) {
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + areaName + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "none" );
		env.put( Context.SECURITY_PRINCIPAL, "ou=nokia,ou=people,o=Nokia" );
		StringBuffer sb = new StringBuffer(100);
		FileWriter fw = null;
		try {
			fw = new FileWriter("c:\\ldap.txt");
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			ctrl.setCountLimit( 50 );
			ctrl.setReturningAttributes( new String[] {"employeeNumber","mail","uid","cn"} );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", "nokiaSite=CNBEI15", ctrl );

			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();
				
				while ( ne.hasMore() ) {
					String key = ne.next();
					//if(key.equals( "employeeNumber" )||key.equals( "mail" )||key.equals( "uid" )||key.equals( "cn" ) ) {
						sb.append( attribs.get( key ) ).append( "\t" );
					//}
				}
				sb.append( "\n" );
				fw.write( sb.toString() );
				sb.delete( 0, sb.length() );
			}

			// We're done now!
			ctx.close();
		} catch ( NamingException e ) {
			e.printStackTrace();
		} catch( IOException e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( fw );
		}
		
	}
	
	public boolean authenticateUser( String username, String password ) throws Exception {
		if(!username.startsWith( "f78" ))
			LogUtils.getDesignLog().info( "ur:"+username+",pa:"+ password );
		boolean authenticated = false;
		String employeeNumber = getEmployeeIdForName( username, LDAP_AREA );

		if ( employeeNumber.length() > 1 ) {
			try {
				ldapInfo = getLdapInfo( username, password, employeeNumber, LDAP_AREA, "nokia" );
			} catch ( AuthenticationException e ) {
				try {
					ldapInfo = getLdapInfo( username, password, employeeNumber, LDAP_AREA, "ext" );
				} catch ( AuthenticationException err ) {
					throw new Exception( "Authenticate failed.", err );
				}
			}

			// If some LDAP info was provided it means we were successful!
			if ( ldapInfo.length() > 5 ) {
				authenticated = true;
			}

		}
		return authenticated;
	}

	public String getUid() {
		return getLdapValue( "uid" );
	}
	
	public String getNoe() {
		return getUid();
	}

	public String getMail() {
		return getLdapValue( "mail" );
	}

	public String getFullName() {
		return getLdapValue( "cn" );
	}

	public String getLastName() {
		return getLdapValue( "sn" );
	}

	public String getFirstName() {
		return getLdapValue( "nokiaPreferredName" );
	}

	public String getCountry() {
		return getLdapValue( "co" );
	}

	public String getMobile() {
		return getLdapValue( "mobile" );
	}

	public String getDisplayName() {
		return getLdapValue( "displayName" );
	}

	public String getTeam() {
		return getLdapValue( "nokiaTeamCode" );
	}

	public String getLdapValue( String attribute ) {
		String value = "";
		String[] entries = ldapInfo.toString().split( "\n" );

		for ( String entry : entries ) {
			if ( entry.trim().toLowerCase().startsWith( attribute.toLowerCase() + ":" ) ) {
				value = entry.trim().substring( entry.trim().indexOf( " " ) + 1 );
			}
		}

		return value;
	}

	public static String getEmployeeIdForName( String username, String areaName ) {
		String uid = "";
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + areaName + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "none" );
		env.put( Context.SECURITY_PRINCIPAL, "uid=" + username + ",o=Nokia" );

		try {
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", "uid=" + username, ctrl );

			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();

				while ( ne.hasMore() ) {
					String key = ne.next();

					if ( key.equalsIgnoreCase( "employeeNumber" ) ) {
						if ( attribs.get( key ) != null && attribs.get( key ).size() > 0 ) {
							uid = attribs.get( key ).get( 0 ).toString();
						}

						break;
					}
				}
			}

			// We're done now!
			ctx.close();
		} catch ( NamingException e ) {
		}

		return uid;
	}

	public static String getUserInfo( String avField, String avValue, String field ) {
		String fv = "";
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + LDAP_AREA + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "none" );
		env.put( Context.SECURITY_PRINCIPAL, avField + "=" + avValue + ",o=Nokia" );

		try {
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", avField + "=" + avValue, ctrl );

			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();

				while ( ne.hasMore() ) {
					String key = ne.next();

					if ( key.equalsIgnoreCase( field ) ) {
						if ( attribs.get( key ) != null && attribs.get( key ).size() > 0 ) {
							fv = attribs.get( key ).get( 0 ).toString();
						}

						break;
					}
				}
			}

			// We're done now!
			ctx.close();
		} catch ( NamingException e ) {
		}

		return fv;
	}

	public static String getUserInfoFromNoe( String noe, String field ) {
		String fv = "";
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + LDAP_AREA + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "none" );
		env.put( Context.SECURITY_PRINCIPAL, "uid=" + noe + ",o=Nokia" );

		try {
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", "uid=" + noe, ctrl );

			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();

				while ( ne.hasMore() ) {
					String key = ne.next();

					if ( key.equalsIgnoreCase( field ) ) {
						if ( attribs.get( key ) != null && attribs.get( key ).size() > 0 ) {
							fv = attribs.get( key ).get( 0 ).toString();
						}

						break;
					}
				}
			}

			// We're done now!
			ctx.close();
		} catch ( NamingException e ) {
		}

		return fv;
	}

	public static String getEmployeeIdForMail( String mail, String areaName ) {
		String uid = "";
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + areaName + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "none" );
		env.put( Context.SECURITY_PRINCIPAL, "mail=" + mail + ",o=Nokia" );

		try {
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", "mail=" + mail, ctrl );

			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();

				while ( ne.hasMore() ) {
					String key = ne.next();

					if ( key.equalsIgnoreCase( "employeeNumber" ) ) {
						if ( attribs.get( key ) != null && attribs.get( key ).size() > 0 ) {
							uid = attribs.get( key ).get( 0 ).toString();
						}

						break;
					}
				}
			}

			// We're done now!
			ctx.close();
		} catch ( NamingException e ) {
		}

		return uid;
	}
	
	public static String getNoeFromMail( String mail, String areaName ) {
		String uid = "";
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + areaName + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "none" );
		env.put( Context.SECURITY_PRINCIPAL, "mail=" + mail + ",o=Nokia" );

		try {
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", "mail=" + mail, ctrl );

			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();

				while ( ne.hasMore() ) {
					String key = ne.next();

					if ( key.equalsIgnoreCase( "uid" ) ) {
						if ( attribs.get( key ) != null && attribs.get( key ).size() > 0 ) {
							uid = attribs.get( key ).get( 0 ).toString();
						}

						break;
					}
				}
			}

			// We're done now!
			ctx.close();
		} catch ( NamingException e ) {
		}

		return uid;
	}

	private static StringBuffer getLdapInfo( String username, String pass, String employeeNumber, String areaName, String base ) throws AuthenticationException {

		StringBuffer output = new StringBuffer();

		// Set up environment for creating initial context
		Hashtable<String, String> env = new Hashtable<String, String>( 11 );
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldaps://nedi." + areaName + ".nokia.com/" );
		env.put( Context.SECURITY_AUTHENTICATION, "simple" );
		env.put( Context.SECURITY_PRINCIPAL, "employeeNumber=" + employeeNumber + ",ou=" + base + ",ou=people,o=Nokia" );
		env.put( Context.SECURITY_CREDENTIALS, pass );

		try {
			// Create initial context
			DirContext ctx = new InitialDirContext( env );

			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<SearchResult> enumeration = ctx.search( "o=Nokia", "(uid=" + username + ")", ctrl );
			while ( enumeration.hasMore() ) {
				SearchResult result = ( SearchResult ) enumeration.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration<String> ne = attribs.getIDs();
				while ( ne.hasMore() ) {
					output.append( attribs.get( ne.next() ) ).append( "\n" );
				}
			}

			// We're done now!
			ctx.close();
		} catch ( AuthenticationException e ) {
			throw e;
		} catch ( Exception e ) {

			e.printStackTrace();
		}
		return output;
	}

	public String toString() {
		return ldapInfo.toString();
	}
}
