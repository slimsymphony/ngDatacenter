package com.nokia.test.qc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;
import com.nokia.test.casedesign.TextCase;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestCase;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestResult;

public class QcHelper {

	private static enum ItemType {
		TEST, INSTANCE, RUN, TESTSET
	}

	final public static String PASS = "Passed";
	final public static String NORESULT = "No Result";
	final public static String FAIL = "Failed";

	final public static String PRODUCT = "PRODUCT";
	final public static String ID = "ID";
	final public static String NAME = "NAME";
	final public static String IDENTIFIER = "IDENTIFIER";
	final public static String QCID = "QCID";
	final public static String TESTSETID = "TESTSETID";
	final public static String TESTCASEID = "TESTCASEID";
	final public static String INSTANCEID = "INSTANCEID";
	final public static String BUGID = "BUGID";
	final public static String NG11_MINI = "MINI";
	final public static String NG11_SS = "NG1.1SS";
	final public static String NG1X = "NG1.X";
	final public static String NG13 = "NG1.3";
	final public static String NG135 = "NG1.35";
	final public static String NG14 = "NG1.4";
	final public static String NG101 = "NG1.01";
	final public static String NGCAMP = "CAMP";
	final public static String NG20 = "NG2.0";

	public static String MAPPING_RFA = "rfa";
	public static String MAPPING_FRT = "frt";
	public static String DEFAULT_DOMAIN = "MP_S40";
	public static String DEFAULT_PROJECT = "S40_NG_1_0";
	public static String DICT_REGRESSION = "QC_INFO";
	public static String DICT_RFA = "QC_INFO_RFA";

	public static Map<String, Map<String, Map<String, String>>> MAPPING_DATA;
	static {
		refresh();
	};

	private Logger log;
	private String domain;
	private String project;
	private String username;
	private String password;
	private String product;
	private String baseUrl = "https://qc11.nokia.com/qcbin/";
	private String credential;
	private String swVersion;
	private Map<String, Map<String, String>> currentDict = null;
	private DefaultHttpClient client = new DefaultHttpClient( new PoolingClientConnectionManager() );
	private final static Map<ItemType, Map<String, String>> props = new HashMap<ItemType, Map<String, String>>();
	static {
		Map<String, String> tMap = new HashMap<String, String>();
		tMap.put( PRODUCT, "user-template-03" );
		tMap.put( ID, "id" );
		tMap.put( NAME, "name" );
		props.put( ItemType.TESTSET, tMap );
		tMap = null;
		tMap = new HashMap<String, String>();
		tMap.put( ID, "id" );
		tMap.put( NAME, "name" );
		tMap.put( QCID, "user-template-30" );
		props.put( ItemType.TEST, tMap );
		tMap = null;
		tMap = new HashMap<String, String>();
		tMap.put( ID, "id" );
		tMap.put( NAME, "name" );
		tMap.put( TESTSETID, "cycle-id" );
		tMap.put( TESTCASEID, "test-id" );
		props.put( ItemType.INSTANCE, tMap );
		tMap = null;
		tMap = new HashMap<String, String>();
		tMap.put( ID, "id" );
		tMap.put( NAME, "name" );
		tMap.put( INSTANCEID, "testcycl-id" );
		tMap.put( TESTCASEID, "test-id" );
		tMap.put( BUGID, "user-template-65" );
		props.put( ItemType.RUN, tMap );
		tMap = null;

	}

	public QcHelper( String username, String password, String domain, String project, String product ) {
		this( username, password, domain, project, product, null );
	}

	public QcHelper( String username, String password, String domain, String project, String product, String dictType ) {
		this.username = username;
		this.password = password;
		this.product = product;
		if ( dictType == null )
			this.currentDict = MAPPING_DATA.get( MAPPING_RFA );
		else
			this.currentDict = MAPPING_DATA.get( dictType );
		if ( this.currentDict == null )
			this.currentDict = MAPPING_DATA.get( MAPPING_RFA );
		if ( domain == null )
			this.domain = DEFAULT_DOMAIN;
		else
			this.domain = domain;
		if ( project == null )
			this.project = currentDict.get( product ).get( "PROJECT" );
		else
			this.project = project;
		this.log = LogUtils.getStatLog();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl( String baseUrl ) {
		this.baseUrl = baseUrl;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct( String product ) {
		this.product = product;
	}

	public String auth() throws Exception {
		return auth( this.username, this.password );
	}

	protected String auth( String username, String password ) throws Exception {
		if ( username != "" ) {
			this.username = username;
			this.password = password;
		}

		HttpGet request = new HttpGet( baseUrl + "authentication-point/authenticate" );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Authorization",
				"Basic " + new String( Base64.encodeBase64( ( username + ":" + password ).getBytes() ), "UTF-8" ) );
		HttpResponse res = client.execute( request );
		Header[] cookies = res.getHeaders( "Set-Cookie" );
		for ( Header h : cookies ) {
			if ( h.getValue().trim().startsWith( "LWSSO_COOKIE_KEY" ) ) {
				credential = h.getValue();
				break;
			}
		}
		if ( credential != null )
			return "OK";
		else
			return null;

	}

	public void logOut() throws Exception {
		HttpGet request = new HttpGet( baseUrl + "authentication-point/logout" );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
	}

	public String swap( String re ) {
		if ( TestResult.PASS.equalsIgnoreCase( re ) ) {
			return PASS;
		} else if ( TestResult.FAIL.equalsIgnoreCase( re ) ) {
			return FAIL;
		} else
			return NORESULT;
	}

	public String compareAndUpdate( String oldR, String newR ) {
		if ( oldR.equalsIgnoreCase( FAIL ) || newR.equalsIgnoreCase( FAIL ) )
			return FAIL;
		else if ( oldR.equalsIgnoreCase( NORESULT ) || newR.equalsIgnoreCase( NORESULT ) )
			return NORESULT;
		else
			return PASS;
	}

	private String getSwBranch( String product, String swVersion ) {
		if ( swVersion.toLowerCase().indexOf( "ca" ) >= 0 ) {
			return "Common Asset";
		} else if ( swVersion.toLowerCase().startsWith( "ng1.x" ) ) {
			if ( product.toLowerCase().startsWith( "lanai" ) )
				return "NG1.2";
			else if ( product.toLowerCase().startsWith( "pegasus" ) )
				return "NG1.1";
			else if ( product.toLowerCase().startsWith( "orion" ) )
				return "NG1.3";
			else if ( product.toLowerCase().startsWith( "aqua" ) )
				return "NG1.1";
		} else if ( swVersion.toLowerCase().startsWith( "ng1.35" ) ) {
			return "NG1.3";
		} else {
			if ( product.toLowerCase().startsWith( "lanai" ) )
				return "NG1.2";
			else if ( product.toLowerCase().startsWith( "pegasus" ) )
				return "NG1.1";
			else if ( product.toLowerCase().startsWith( "orion" ) )
				return "NG1.3";
			else if ( product.toLowerCase().startsWith( "aqua" ) )
				return "NG1.1";
			else if ( product.toLowerCase().startsWith( "spinel" ) )
				return "NG1.3";
			else
				return "Common Asset";
		}
		log.error( "Can't find correct swbranch value for product:" + product + ",sw:" + swVersion
				+ "; so use ca to replace." );
		return "Common Asset";
	}

	private String getMapData( String product, String item, String swVersion ) {
		if ( swVersion.toLowerCase().startsWith( "ng1.1rel-11.1" ) ) {
			return this.currentDict.get( product + "_" + NG11_SS ).get( item );
		} else if ( swVersion.toLowerCase().startsWith( "ng1.1rel-11.0" ) ) {
			return this.currentDict.get( product + "_" + NG11_MINI ).get( item );
		} else if ( swVersion.toLowerCase().startsWith( "ng1.x" ) ) {
			return this.currentDict.get( product + "_" + NG1X ).get( item );
		} else if ( swVersion.toLowerCase().startsWith( "ng1.35" ) ) {
			return this.currentDict.get( product + "_" + NG135 ).get( item );
		} else if ( swVersion.toLowerCase().startsWith( "ng1.3" ) ) {
			return this.currentDict.get( product + "_" + NG13 ).get( item );
		} else if ( swVersion.toLowerCase().startsWith( "ng1.4" ) ) {
			return this.currentDict.get( product + "_" + NG14 ).get( item );
		} else if ( swVersion.toLowerCase().startsWith( "ng1.01" ) ) {
			return this.currentDict.get( product + "_" + NG101 ).get( item );
		} else if ( product.equals( "Lanai SS" ) ) {
			return this.currentDict.get( product + "_" + NGCAMP ).get( item );
		} else if ( swVersion.trim().length() == 40
				&& ( product.equalsIgnoreCase( "Spinel DS QB" ) || product.equalsIgnoreCase( "Spinel SS QB" )
						|| product.equalsIgnoreCase( "Orion DS QB" ) || product.equalsIgnoreCase( "Orion SS QB" ) ) ) {
			return this.currentDict.get( product + "_" + NG135 ).get( item );
		} else if ( swVersion.toLowerCase().indexOf( "ca" ) >= 0 ) {
			return this.currentDict.get( product + "_" + NGCAMP ).get( item );
		} else {
			return this.currentDict.get( product + "_" + NGCAMP ).get( item );
		}
	}

	public int querTestsetId( String testsetName, String swVersion ) throws Exception {
		String strUrl = baseUrl + "rest/domains/" + domain + "/projects/" + project + "/test-sets?query={name['"
				+ testsetName + "'];parent-id[=" + getMapData( product, "PARENT_FOLDERID", swVersion ) + "]}&fields=id";
		URL url = new URL( strUrl );
		URI uri = new URI( url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null );
		log.info( "uri:" + uri );
		HttpGet request = new HttpGet( uri );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.debug( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='id']" );
		if ( ele != null )
			return CommonUtils.parseInt( ele.element( "Value" ).getText(), 0 );
		return 0;
	}

	public int querInstanceId( int testsetId, int testIdentifier ) throws Exception {
		String strUrl = baseUrl + "rest/domains/" + domain + "/projects/" + project
				+ "/test-instances?query={cycle-id[" + testsetId + "];test-id[" + testIdentifier + "]}&fields=id";
		URL url = new URL( strUrl );
		URI uri = new URI( url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null );
		log.info( "uri:" + uri );
		HttpGet request = new HttpGet( uri );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.info( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='id']" );
		if ( ele != null )
			return CommonUtils.parseInt( ele.element( "Value" ).getText(), 0 );
		return 0;
	}

	public void startProcessExecution( String product, String testsetName, TestExecution te ) throws Exception {
		if ( domain == null )
			domain = DEFAULT_DOMAIN;
		if ( project == null )
			project = DEFAULT_PROJECT;
		String ret = this.auth();
		Random r = new Random();
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "select * from STAT_TESTCASES where id=?";
		Map<Integer, String> cases = new HashMap<Integer, String>();
		Map<Integer, Integer> kv = new HashMap<Integer, Integer>();
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			if ( null != ret && "OK".equals( ret ) ) {
				swVersion = te.getSw();
				int testsetId = this.querTestsetId( testsetName, swVersion );
				if ( testsetId == 0 ) {
					testsetId = this.createTestset( testsetName, product, te.getSw() );
					log.info( "Successfully create a new testset on QC,testsetId:" + testsetId );
				} else {
					log.info( "Found an existed testset on QC,testsetId:" + testsetId );
				}
				/*
				 * if(testsetId == 0 ) { throw new RuntimeException(
				 * "No valid testset found, testSetName:"
				 * +testsetName+",createNew:"+createNewTestset ); }
				 */

				for ( TestResult tr : te.getResults() ) {
					try {
						TestCase tc = StatisticManager.getTestCaseById( ps, tr.getCaseId() );
						ps.clearParameters();
						if ( tc.getQcIdentifier() <= 0 ) {
							int qcIdentifier = this.queryTestIdentidier( tc.getQcid() );
							if ( qcIdentifier > 0 )
								tc.setQcIdentifier( qcIdentifier );
							else {
								log.error( "Can't find related qc testcase:" + tr + "-----" + tc );
								continue;
							}
						}

						if ( cases.get( tc.getQcid() ) != null ) {
							// check result.
							String rp = compareAndUpdate( cases.get( tc.getQcid() ), swap( tr.getResult() ) );
							if ( !rp.equals( cases.get( tc.getQcid() ) ) ) {
								int instanceId = kv.get( tc.getQcid() );
								this.updateInstance( product, testsetId, tc.getQcIdentifier(), instanceId,
										swap( tr.getResult() ), tr.getBugId(), tr.getBugInfo() );
								log.info( "Successful update test result to QC, instanceId:" + instanceId );
							}
						} else {
							cases.put( tc.getQcid(), swap( tr.getResult() ) );
							int instanceId = 0;
							instanceId = querInstanceId( testsetId, tc.getQcIdentifier() );
							if ( instanceId == 0 )
								instanceId = this.createTestInstance( product, testsetId, tc.getQcIdentifier(),
										swap( tr.getResult() ), swVersion );
							int runId = this.createTestRun( product, testsetId, tc.getQcIdentifier(),
									swap( tr.getResult() ), instanceId, tr.getDuration(), "Fast_Run_" + testsetId + "_"
											+ tc.getQcIdentifier() + "_" + r.nextInt( 2 ), swVersion );
							this.updateInstance( product, testsetId, tc.getQcIdentifier(), instanceId,
									swap( tr.getResult() ), tr.getBugId(), tr.getBugInfo() );
							kv.put( tc.getQcid(), instanceId );
							log.info( "Successful update test result to QC, instanceId:" + instanceId + ",runId:"
									+ runId );
						}
					} catch ( Exception e ) {
						log.error( "Handling test script got exception,tr:" + tr, e );
					}
				}
				this.logOut();
			} else {
				throw new RuntimeException( "Authentication Failed!" );
			}
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	public String updateInstance( String product, int testsetId, int testId, int instanceId, String result, int bugId,
			String bugInfo ) throws Exception {
		HttpPut request = new HttpPut( baseUrl + "rest/domains/" + domain + "/projects/" + project + "/test-instances/"
				+ instanceId );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		String content = testInstanceUpdateCreator( result, instanceId, bugId, bugInfo );
		try {
			DocumentHelper.parseText( content );
		} catch ( Exception e ) {
			log.warn( "Buginfo contain some illegal characters.bugInfo:" + bugInfo, e );
			content = testInstanceUpdateCreator( result, instanceId, bugId, "" );
		}
		request.setEntity( new StringEntity( content, ContentType.APPLICATION_XML ) );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.debug( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='status']" );
		return ele.element( "Value" ).getText();
	}

	public int createTestset( String tsName, String product, String swVersion ) throws Exception {
		HttpPost request = new HttpPost( baseUrl + "rest/domains/" + domain + "/projects/" + project + "/test-sets" );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		request.setEntity( new StringEntity( testSetCreator( tsName, product, swVersion,
				getSwBranch( product, swVersion ) ), ContentType.APPLICATION_XML ) );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.debug( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='id']" );
		int tsID = CommonUtils.parseInt( ele.element( "Value" ).getText(), -1 );
		return tsID;
	}

	public static String getQueryKey( ItemType type, String key, String value ) {
		Map<String, String> tmap = props.get( type );
		if ( tmap != null ) {
			return tmap.get( key );
		}
		return null;
	}

	public int createTestRun( String product, int testsetId, int testId, String result, int testinstanceId,
			int duration, String name, String swVersion ) throws Exception {
		HttpPost request = new HttpPost( baseUrl + "rest/domains/" + domain + "/projects/" + project + "/runs" );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		request.setEntity( new StringEntity( testRunCreator( product, testsetId, testId, result, testinstanceId,
				duration, name, getSwBranch( product, swVersion ) ), ContentType.APPLICATION_XML ) );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.info( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='id']" );
		int tsID = CommonUtils.parseInt( ele.element( "Value" ).getText(), -1 );
		return tsID;
	}

	public int createTestInstance( String product, int testsetId, int testId, String result, String swVersion )
			throws Exception {
		HttpPost request = new HttpPost( baseUrl + "rest/domains/" + domain + "/projects/" + project
				+ "/test-instances" );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		request.setEntity( new StringEntity( testInstanceCreator( product, testsetId, testId, result,
				getSwBranch( product, swVersion ) ), ContentType.APPLICATION_XML ) );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.info( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='id']" );
		int tsID = CommonUtils.parseInt( ele.element( "Value" ).getText(), -1 );
		return tsID;
	}

	private String testSetCreator( String name, String product, String swVersion, String productVersion ) {
		// Aqua DS
		StringBuffer sb = new StringBuffer( 200 );
		sb.append( "<Entity Type=\"test-set\"><Fields><Field Name=\"user-template-09\"><Value>" );
		sb.append( username );
		sb.append( "</Value></Field><Field Name=\"user-template-01\"><Value>" );
		sb.append( productVersion );
		sb.append( "</Value></Field><Field Name=\"parent-id\"><Value>" )
				.append( getMapData( product, "PARENT_FOLDERID", swVersion ) )
				.append( "</Value></Field><Field Name=\"user-template-03\"><Value>" );
		sb.append( getMapData( product, "PRODUCT_NAME", swVersion ) );
		String org = "Test Services";
		sb.append( "</Value></Field><Field Name=\"user-template-11\"><Value>" );
		sb.append( org );
		sb.append( "</Value></Field><Field Name=\"user-template-10\"><Value>" );
		String focus = "(SW) Feature Testing";
		sb.append( focus );
		sb.append( "</Value></Field><Field Name=\"subtype-id\"><Value>hp.qc.test-set.default</Value></Field><Field Name=\"status\"><Value>Open</Value></Field><Field Name=\"name\"><Value>" );
		sb.append( name );
		sb.append( "</Value></Field><Field Name=\"cycle-ver-stamp\"><Value>1</Value></Field></Fields></Entity>" );
		return sb.toString();
	}

	private String testInstanceCreator( String product, int testsetId, int testId, String result, String swBranch ) {
		StringBuffer sb = new StringBuffer( 500 );
		sb.append( "<Entity Type=\"test-instance\"><Fields><Field Name=\"user-template-07\"><Value>" );
		sb.append( swBranch );
		sb.append( "</Value></Field><Field Name=\"user-template-02\"><Value>" );
		sb.append( getMapData( product, "PRODUCT_NAME", swVersion ) ); // product
		sb.append( "</Value></Field><Field Name=\"cycle-id\"><Value>" );
		sb.append( testsetId );
		sb.append( "</Value></Field><Field Name=\"user-template-10\"><Value>" );
		String testFocus = "(SW) Feature Testing";
		sb.append( testFocus );
		sb.append( "</Value></Field><Field Name=\"subtype-id\"><Value>hp.qc.test-instance.MANUAL</Value></Field><Field Name=\"test-order\"><Value>1</Value></Field><Field Name=\"test-instance\"><Value>1</Value></Field><Field Name=\"test-id\"><Value>" );
		sb.append( testId );
		sb.append( "</Value></Field><Field Name=\"owner\"><Value>" );
		sb.append( username );
		sb.append( "</Value></Field>" );
		sb.append( "<Field Name=\"status\"><Value>" );
		sb.append( result );
		sb.append( "</Value></Field>" );
		sb.append( "<Field Name=\"test-config-id\"><Value>" );
		sb.append( testId + 1000 );
		sb.append( "</Value></Field></Fields></Entity>" );
		return sb.toString();
	}

	private String testInstanceUpdateCreator( String result, int instanceId, int bugId, String bugComments ) {
		StringBuffer sb = new StringBuffer( 500 );
		sb.append( "<Entity Type=\"test-instance\"><Fields>" );
		sb.append( "<Field Name=\"status\"><Value>" ).append( result ).append( "</Value></Field>" );
		if ( bugId > 0 ) {
			sb.append( "<Field Name=\"user-template-65\"><Value>" ).append( bugId ).append( "</Value></Field>" );
			sb.append( "<Field Name=\"user-template-57\"><Value>" ).append( bugComments ).append( "</Value></Field>" );
		}
		sb.append( "</Fields></Entity>" );
		return sb.toString();
	}

	private String testRunCreator( String product, int testsetId, int testId, String result, int instanceId,
			int duration, String name, String swBranch ) {
		StringBuffer sb = new StringBuffer( 500 );
		sb.append( "<Entity Type=\"run\"><Fields><Field Name=\"user-template-02\"><Value>" );
		sb.append( swBranch );
		sb.append( "</Value></Field><Field Name=\"user-template-03\"><Value>" );
		sb.append( getMapData( product, "PRODUCT_NAME", swVersion ) );
		sb.append( "</Value></Field><Field Name=\"cycle-id\"><Value>" );
		sb.append( testsetId );
		sb.append( "</Value></Field><Field Name=\"user-template-10\"><Value>" );
		String testFocus = "(SW) Feature Testing";
		sb.append( testFocus );
		sb.append( "</Value></Field><Field Name=\"subtype-id\"><Value>hp.qc.run.MANUAL</Value></Field><Field Name=\"test-instance\"><Value>1</Value></Field><Field Name=\"status\"><Value>" );
		sb.append( result );
		sb.append( "</Value></Field><Field Name=\"test-id\"><Value>" );
		sb.append( testId );
		sb.append( "</Value></Field><Field Name=\"owner\"><Value>" );
		sb.append( username );
		sb.append( "</Value></Field><Field Name=\"test-config-id\"><Value>" );
		sb.append( testId + 1000 );
		sb.append( "</Value></Field>" );
		sb.append( "<Field Name=\"name\"><Value>" );
		sb.append( name );
		sb.append( "</Value></Field>" );
		sb.append( "<Field Name=\"testcycl-id\"><Value>" );
		sb.append( instanceId );
		sb.append( "</Value></Field>" );
		sb.append( "<Field Name=\"draft\"><Value>N</Value></Field><Field Name=\"duration\"><Value>" );
		sb.append( duration );
		sb.append( "</Value></Field></Fields></Entity>" );
		return sb.toString();
	}

	public int queryTestIdentidier( int qcId ) throws Exception {
		String strUrl = baseUrl + "rest/domains/" + domain + "/projects/" + project + "/tests?query={user-template-30["
				+ qcId + "]}&fields=id";
		URL url = new URL( strUrl );
		URI uri = new URI( url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null );
		log.info( "uri:" + uri );
		HttpGet request = new HttpGet( uri );// https://qc11qa.nokia.com/qcbin/
		request.addHeader( "Content-Type", "application/xml" );
		request.addHeader( "Cookie", this.credential );
		request.addHeader( "Accept", "application/xml" );
		HttpResponse res = client.execute( request );
		log.info( res.getStatusLine().toString() );
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		res.getEntity().writeTo( bao );
		String respStr = new String( bao.toByteArray() );
		log.debug( "response:" + respStr );
		Document doc = DocumentHelper.parseText( respStr );
		Element ele = ( Element ) doc.getRootElement().selectSingleNode( "//Field[@Name='id']" );
		if ( ele != null )
			return CommonUtils.parseInt( ele.element( "Value" ).getText(), 0 );
		return 0;
	}

	public int createTestcase( TextCase tc ) {
		// TODO: implementation lost
		return 0;
	}

	public static void refresh() {
		InputStream in = null;
		ByteArrayOutputStream bos = null;
		try {
			in = ( Thread.currentThread().getContextClassLoader().getResourceAsStream( "mapping.json" ) );
			bos = new ByteArrayOutputStream();
			IOUtils.copy( in, bos );
			MAPPING_DATA = CommonUtils.fromJson( new String( bos.toByteArray() ),
					( new TypeToken<Map<String, Map<String, Map<String, String>>>>() {
					} ).getType() );
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( bos );
			CommonUtils.closeQuitely( in );
		}
	}

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		QcHelper h = new QcHelper( "f78wang", "", "MASTER", "S40_MASTER", "Orion DS", MAPPING_RFA );
		h.auth();
		int tsId = h.querTestsetId( "RFA_OrionDS_NG1.3swu-0.1320.11", "NG1.3swu-0.1323.11" );
		System.out.println( tsId );
		System.out.println( CommonUtils.toJson( QcHelper.MAPPING_DATA.get( MAPPING_RFA ) ) );
		System.out.println( CommonUtils.toJson( QcHelper.MAPPING_DATA.get( MAPPING_FRT ) ) );
	}
}
