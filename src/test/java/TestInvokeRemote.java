import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;


public class TestInvokeRemote {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception{
		//CommonUtils.invokeRemote( "http://becim010:8007" );
		//sendPost("http://betstas01.china.nokia.com/scvMonitor/hydraDailyMonitor.jsp");
		//sendPost("http://betstas01.china.nokia.com/scvMonitor/hydraWeeklyMonitor.jsp");
		//sendPost("http://localhost/scvMonitor/hydraDailyMonitor.jsp");
		testNTCredentials();
	}
	
	public static void sendPost(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><notification xmlns=\"http://hydra.nokia.com/schema/rest\" href=\"http://behdr001.china.nokia.com/sites/e19a12e5-691a-4e50-9f95-2ef6d7d83f54\"><resource>https://behdr001.china.nokia.com/services/d8d74bff-272c-426e-86dd-c1027995445d</resource><event>release-updated</event><site>Beijing</site><at>2013-02-11T14:10:42+00:00</at><attribute><name>release-name</name><value>NG1.0swu-130701</value></attribute><attribute><name>service-name</name><value>S40_eVo_sw_updates</value></attribute><attribute><name>release-state</name><value>Ready</value></attribute><attribute><name>service-uri</name><value>http://behdr001.china.nokia.com/services/d8d74bff-272c-426e-86dd-c1027995445d</value></attribute><attribute><name>release-uri</name><value>http://behdr001.china.nokia.com/releases/b51c409a-5e5c-4286-9b21-e5ad43dca295</value></attribute></notification>";
		//String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><notification xmlns=\"http://hydra.nokia.com/schema/rest\" href=\"http://sahdrqa01.europe.nokia.com/sites/41a02a92-41fa-4d8d-8b9e-1d1561c97cc0\">  <resource>https://sahdrqa01.europe.nokia.com/services/822d0683-0d89-4ff8-8420-3ac093daeb9a</resource>  <event>release-created</event>  <site>Salo-QA</site>  <at>2012-11-22T07:41:24+00:00</at>  <attribute>    <name>release-name</name>    <value>diibadaaba</value>  </attribute>  <attribute>    <name>service-name</name>    <value>FSS_HYDRA_TEAM_ONLY</value>  </attribute>  <attribute>    <name>service-uri</name>    <value>http://sahdrqa01.europe.nokia.com/services/822d0683-0d89-4ff8-8420-3ac093daeb9a</value>  </attribute>  <attribute>    <name>release-state</name>    <value>Unpublished</value>  </attribute>  <attribute>    <name>release-uri</name>    <value>http://sahdrqa01.europe.nokia.com/releases/549dbf10-29f9-42ef-a033-c67291a50e12</value>  </attribute></notification>";
		post.setEntity( new StringEntity(content,ContentType.TEXT_XML) );
		try {
			client.execute( post );
		} catch ( ClientProtocolException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public static void testNTCredentials() throws IOException {
		DefaultHttpClient client = new DefaultHttpClient( new PoolingClientConnectionManager());
		HttpGet request = new HttpGet("http://qc11.nokia.com/qcbin/authentication-point/authenticate");//https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Authorization", "Basic "+new String(Base64.encodeBase64( "f78wang:Nokiapassword01".getBytes() ),"UTF-8" ) );
		HttpResponse res = client.execute( request );
		Header[] cookies = res.getHeaders("Set-Cookie");
		
		//request.addHeader(  )
		//client = new DefaultHttpClient();
		request = new HttpGet("http://qc11.nokia.com/qcbin/rest/is-authenticated");
		for(Header h : cookies) {
			if(h.getValue().trim().startsWith( "LWSSO_COOKIE_KEY" ))
				request.addHeader( "Cookie",h.getValue() );
		}
		res = client.execute( request );
		ByteArrayOutputStream bao = new ByteArrayOutputStream(); 
		res.getEntity().writeTo( bao );
		System.out.println("response:"+new String(bao.toByteArray()));
	}

}
