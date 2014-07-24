package com.nokia.test.statistic;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class TestParser {

	@SuppressWarnings( "unchecked" )
	public static Testset parseTestset( String product, String content ) throws Exception {
		Testset ts = new Testset();
		ts.setProduct( product );
		Document doc = DocumentHelper.parseText( content );
		Element root = doc.getRootElement();
		List<Element> cases = root.selectNodes( "testcase" );
		for(Element tc : cases) {
			TestCase ca = new TestCase();
			String id = tc.attributeValue( "name" );
			if(id.indexOf( "(" )<0||id.indexOf( ")" )<=0) {
		    	LogUtils.getStatLog().error( "Found not validate case in testset:"+id );
		    	continue;
		    }
			id = id.substring( id.indexOf( "(" )+1, id.indexOf( ")" ) ).trim().toUpperCase();
			ca.setCaseName( tc.attributeValue( "name" ) );
			ca.setCaseId( id );
			ca.setFeature( tc.attributeValue("feature") );
			ca.setFeatureGroup( tc.attributeValue("subarea") );
			ca.setProduct( product );
			int qcid = CommonUtils.parseInt( id.substring( id.indexOf( "-" )+1 ).trim(), 0  );
			if( qcid == 0 ) {
				LogUtils.getStatLog().error( "Found not validate case in testset:"+id );
				continue;
			}
			ca.setQcid( qcid );
			Element tsEle = tc.element( "testscript" );
			if( tsEle != null ) {
				ca.setDirectory( tsEle.attributeValue( "directory" ) );
				ca.setFile( tsEle.attributeValue( "file" ) );
				ca.setTestClass( tsEle.attributeValue( "class" ) );
				ca.setMethod( tsEle.attributeValue( "method" ) );
			}
			ts.addTestCase( ca.getFeatureGroup(), ca );
		}
		return ts;
	}
	
	public static String parseTestsetName( String content ) throws Exception {
		return((Element)DocumentHelper.parseText( content ).getRootElement().selectSingleNode( "testsuite" )).attributeValue( "name" );
	}
	
	@SuppressWarnings( "unchecked" )
	public static TestExecution parseTestExecution( String productV, String content )  throws Exception {
		TestExecution te = new TestExecution();
		Document doc = DocumentHelper.parseText( content );
		Element root = (Element)doc.getRootElement().selectSingleNode( "testsuite" );
		Element props = (Element)root.selectSingleNode( "properties" );
		String product = "";
		try {
			props.attributeValue( "product" );
		}catch(Exception ex) {
			System.err.println( "Current Dom didn't include prodcut attribute," + ex.getMessage() );
		}
		if( productV !=null && !productV.trim().equals( "" ) ) {
			product = productV;
		}
		te.setProduct( product );
		String testsetName = root.attributeValue( "name" );
		List<Testset> sets = StatisticManager.getTestsets( -1, -1, testsetName, product );
		if( sets!=null && sets.size()==1 ) {
			te.setTestsetId( sets.get( 0 ).getId() );
		}
		
		List<Element> results = root.selectNodes( "testcase" );
		for(Element result : results) {
			TestResult tr = new TestResult();
			//String path = result.attributeValue( "classname" );
			//String[] arr = path.split( "\\." );
			Element fail = (Element)result.selectSingleNode( "failure" );
			Element noresult = (Element)result.selectSingleNode( "na" );
			Element report = (Element) result.selectSingleNode( "report" );
			String reltivePath = "";
			if( report != null ) {
				reltivePath = report.attributeValue( "relativePath" );
				tr.setReference( reltivePath );
			}
			
		    if(fail!=null) {
		    	tr.setResult( TestResult.FAIL );
		    	tr.setMessage( fail.attributeValue( "message" ) );
		    	tr.setDetail( fail.attributeValue( "detail" ) );
		    } else if( noresult != null ) {
		    	tr.setResult( TestResult.NORESULT );
		    	tr.setMessage( noresult.attributeValue( "message" ) );
		    	tr.setDetail( noresult.attributeValue( "detail" ) );
		    } else {
		    	tr.setResult( TestResult.PASS );
		    }
		    String id = result.attributeValue( "name" );
		    if(id.indexOf( "(" )<0||id.indexOf( ")" )<=0) {
		    	LogUtils.getStatLog().error( "Found not validate case result:"+id );
		    	continue;
		    }
			id = id.substring( id.indexOf( "(" )+1, id.indexOf( ")" ) ).trim().toUpperCase();
			TestCase tc = StatisticManager.getTestCaseByCaseId( id, product );
			if( tc != null )
				tr.setCaseId( tc.getId() );
			tr.setDuration( parseDuration(result.attributeValue( "time" )) );
			te.addResult( tr );
			te.setDuration( te.getDuration()+tr.getDuration() );
		}
		return te;
	}
	
	
	public static List<TestCase> parseTestCasesFromExecution( String productV, String content )  throws Exception {
		List<TestCase> cases = new ArrayList<TestCase>(); 
		Document doc = DocumentHelper.parseText( content );
		Element root = (Element)doc.getRootElement().selectSingleNode( "testsuite" );
		Element props = (Element)root.selectSingleNode( "properties" );
		String product = props.attributeValue( "product" );
		if(productV!=null&&!productV.trim().equals( "" )) {
			product = productV;
		}
		
		List<Element> results = root.selectNodes( "testcase" );
		for(Element result : results) {
		    String id = result.attributeValue( "name" );
		    if(id.indexOf( "(" )<0||id.indexOf( ")" )<=0) {
		    	LogUtils.getStatLog().error( "Found not validate case result:"+id );
		    	continue;
		    }
			id = id.substring( id.indexOf( "(" )+1, id.indexOf( ")" ) ).trim().toUpperCase();
			TestCase tc = StatisticManager.getTestCaseByCaseId( id, product );
			if( tc == null) {
				LogUtils.getStatLog().error( "The id["+id+"] can't be fetched" );
			} else
				cases.add( tc );
		}
		return cases;
	}

	private static int parseDuration( String time ) {
		int total = 0;
		if( time ==null || time.trim().equals( "" ))
			return total;
		if( time.contains( "h" ) ) {
			String[] arr = time.split( "h" );
			if(arr.length >= 2 )
				time = arr[1].trim();
			else
				time = "";
			total += 3600*Integer.parseInt( arr[0].trim() );
		}
		
		if ( time.contains( "m" )) {
			String[] arr = time.split( "m" );
			if(arr.length >= 2 )
				time = arr[1].trim();
			else
				time = "";
			total += 60 * Integer.parseInt( arr[0].trim() );
		}
		if( time.contains( "s" )) {
			time = time.replaceAll( "s", "" );
			total += Float.parseFloat( time.trim() );
		}
		
		return total;
	}
}
