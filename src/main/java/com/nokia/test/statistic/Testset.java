package com.nokia.test.statistic;

import static com.nokia.granite.analyzer.CommonUtils.filterXmlSpecialCharactors;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nokia.granite.analyzer.CommonUtils;

public class Testset {
	private int id;
	private String name;
	private String product;

	/**
	 * First level is for store featureGroup, second is for store testcase id.
	 */
	private Map<String, List<TestCase>> testcases = new LinkedHashMap<String, List<TestCase>>();

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct( String product ) {
		this.product = product;
	}

	public int getTestCaseCount() {
		int count = 0;
		for ( List<TestCase> l : testcases.values() ) {
			count += l.size();
		}
		return count;
	}

	public Map<String, List<TestCase>> getTestcases() {
		return testcases;
	}

	public void setTestcases( Map<String, List<TestCase>> testcases ) {
		this.testcases = testcases;
	}

	public void addTestCase( String featureGroup, TestCase tc ) {
		if ( featureGroup == null ) {
			featureGroup = "";
		}
		if ( tc == null )
			return;
		if ( testcases.get( featureGroup ) == null ) {
			testcases.put( featureGroup, new LinkedList<TestCase>() );
		}
		testcases.get( featureGroup ).add( tc );
	}

	public TestCase getTestCaseById( int caseId ) {
		TestCase testCase = null;
		for ( List<TestCase> tcs : testcases.values() ) {
			if ( testCase != null )
				break;
			for ( TestCase tc : tcs ) {
				if ( tc.getId() == caseId ) {
					testCase = tc;
					break;
				}
			}
		}
		return testCase;
	}

	public String toTestset() {
		StringBuilder sb = new StringBuilder( 50000 );
		//﻿sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append( "<testset>\n" );
		for ( Entry<String, List<TestCase>> entry : testcases.entrySet() ) {
			for(TestCase tc: entry.getValue()) {
				sb.append( "  <testcase  name=\"" ).append( filterXmlSpecialCharactors( tc.getCaseName() ) ).append( "\" feature=\"" ).append( filterXmlSpecialCharactors( tc.getFeature() ) ).append("\" subarea=\"").append( filterXmlSpecialCharactors( tc.getFeatureGroup() ) );
				sb.append( "\" ucid=\"\" totalruncount=\"1\">\n" );
				sb.append( "    <testscript directory=\"" ).append( filterXmlSpecialCharactors(tc.getDirectory()) ).append( "\" file=\"" ).append( filterXmlSpecialCharactors(tc.getFile()) ).append( "\" class=\"" ).append( filterXmlSpecialCharactors(tc.getTestClass()) );
				sb.append( "\" method=\"" ).append( filterXmlSpecialCharactors(tc.getMethod()) ).append( "\" />\n" );
				sb.append( "  </testcase>\n" );
			}
		}
		sb.append( "</testset>\n" );
		if( !CommonUtils.checkXmlValidation( sb.toString() ) ) {
			throw new RuntimeException( "Xml content not valiated:"+sb.toString().substring( 0, 500 ) );
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
