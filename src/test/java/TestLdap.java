import com.nokia.granite.analyzer.LDAPAuthenticator;


public class TestLdap {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		LDAPAuthenticator lc = new LDAPAuthenticator();
		String a = lc.getNoeFromMail( "kevin.wangbing@nokia.com", "europe" );
		System.out.println(a);
		
	}

}
