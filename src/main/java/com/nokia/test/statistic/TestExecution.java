package com.nokia.test.statistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class TestExecution {
	private int id;
	private String name;
	private Timestamp execTime;
	private String url;
	private int testsetId;
	private String product;
	private int duration;
	private String sw;
	private String from;
	private String type;
	private List<TestResult> results = new ArrayList<TestResult>();
	private List<SubExecution> subExecutions = new ArrayList<SubExecution>();

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

	public Timestamp getExecTime() {
		return execTime;
	}

	public void setExecTime( Timestamp execTime ) {
		this.execTime = execTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration( int duration ) {
		this.duration = duration;
	}

	public int getTotalCnt() {
		return this.results.size();
	}

	public String getSw() {
		return sw;
	}

	public void setSw( String sw ) {
		this.sw = sw;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public int getPassCnt() {
		int cnt = 0;
		for ( TestResult tc : results ) {
			if ( tc.getResult() != null && tc.getResult().equalsIgnoreCase( TestResult.PASS ) ) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getOriPassCnt() {
		int cnt = 0;
		for ( TestResult tc : results ) {
			if ( tc.getOriResult() != null && tc.getOriResult().equalsIgnoreCase( TestResult.PASS ) ) {
				cnt++;
			}
		}
		return cnt;
	}

	public int getFailCnt() {
		int cnt = 0;
		for ( TestResult tc : results ) {
			if ( tc.getResult() != null && tc.getResult().equalsIgnoreCase( TestResult.FAIL ) ) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getOriFailCnt() {
		int cnt = 0;
		for ( TestResult tc : results ) {
			if ( tc.getOriResult() != null && tc.getOriResult().equalsIgnoreCase( TestResult.FAIL ) ) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getNoResultCnt() {
		int cnt = 0;
		for ( TestResult tc : results ) {
			if ( tc.getResult() != null && tc.getResult().equalsIgnoreCase( TestResult.NORESULT ) ) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getOriNoResultCnt() {
		int cnt = 0;
		for ( TestResult tc : results ) {
			if ( tc.getOriResult() != null && tc.getOriResult().equalsIgnoreCase( TestResult.NORESULT ) ) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public Map<String,Float> getPassRateByFeatureGroup() {
		Map<String,Float> prs = new HashMap<String,Float>();
		Map<String,int[]> stat = new HashMap<String,int[]>();
		Testset ts = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "select * from STAT_TESTCASES where id=?";
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );

			if(testsetId != 0 && (ts = StatisticManager.getTestsetById( testsetId ))!=null) {
				for(TestResult tr : this.results) {
					TestCase tc = ts.getTestCaseById( tr.getCaseId() );
					if(tc == null) {
						tc = StatisticManager.getTestCaseById( ps,tr.getCaseId() );
						ps.clearParameters();
					}
					if(tc != null) {
						String fg = tc.getFeatureGroup();
						if(fg!=null) {
							int[] vals = stat.get( fg );
							if(vals==null) {
								vals = new int[2];
								stat.put( fg, vals );
							}
							vals[0] += 1;
							if(tr.getResult().equals( TestResult.PASS )) {
								vals[1] += 1;
							}
						}
					}
				}
			} else {
				for(TestResult tr : this.results) {
					TestCase tc = StatisticManager.getTestCaseById( ps, tr.getCaseId() );
					ps.clearParameters();
					if(tc != null) {
						String fg = tc.getFeatureGroup();
						if(fg!=null) {
							int[] vals = stat.get( fg );
							if(vals==null) {
								vals = new int[2];
								stat.put( fg, vals );
							}
							vals[0] += 1;
							if(tr.getResult().equals( TestResult.PASS )) {
								vals[1] += 1;
							}
						}
					}
				}
			}
			
			for(Entry<String,int[]> entry:stat.entrySet()) {
				String fg = entry.getKey();
				int[] vals = entry.getValue();
				if(vals!=null&&vals[0]!=0) {
					prs.put( fg, (float)vals[1]/(float)vals[0] );
				}
			}
		} catch ( SQLException e ) {
			LogUtils.getStatLog().error( "get PassRate By FeatureGroup Failed.", e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return prs;
	}
	
	public Map<String,Float> getOriPassRateByFeatureGroup() {
		Map<String,Float> prs = new HashMap<String,Float>();
		Map<String,int[]> stat = new HashMap<String,int[]>();
		Testset ts = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "select * from STAT_TESTCASES where id=?";
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );

			if(testsetId != 0 && (ts = StatisticManager.getTestsetById( testsetId ))!=null) {
				for(TestResult tr : this.results) {
					TestCase tc = ts.getTestCaseById( tr.getCaseId() );
					if(tc == null) {
						tc = StatisticManager.getTestCaseById( ps,tr.getCaseId() );
						ps.clearParameters();
					}
					if(tc != null) {
						String fg = tc.getFeatureGroup();
						if(fg!=null) {
							int[] vals = stat.get( fg );
							if(vals==null) {
								vals = new int[2];
								stat.put( fg, vals );
							}
							vals[0] += 1;
							if(tr.getOriResult().equals( TestResult.PASS )) {
								vals[1] += 1;
							}
						}
					}
				}
			} else {
				for(TestResult tr : this.results) {
					TestCase tc = StatisticManager.getTestCaseById( ps, tr.getCaseId() );
					ps.clearParameters();
					if(tc != null) {
						String fg = tc.getFeatureGroup();
						if(fg!=null) {
							int[] vals = stat.get( fg );
							if(vals==null) {
								vals = new int[2];
								stat.put( fg, vals );
							}
							vals[0] += 1;
							if(tr.getOriResult().equals( TestResult.PASS )) {
								vals[1] += 1;
							}
						}
					}
				}
			}
			
			for(Entry<String,int[]> entry:stat.entrySet()) {
				String fg = entry.getKey();
				int[] vals = entry.getValue();
				if(vals!=null&&vals[0]!=0) {
					prs.put( fg, (float)vals[1]/(float)vals[0] );
				}
			}
		} catch ( SQLException e ) {
			LogUtils.getStatLog().error( "get Original PassRate By FeatureGroup Failed.", e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return prs;
	}

	
	public float getPassRate() {
		return (float)getPassCnt()/(float)getTotalCnt();
	}
	
	public float getOriPassRate() {
		return (float)getOriPassCnt()/(float)getTotalCnt();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}
	
	public int getTestsetId() {
		return testsetId;
	}

	public void setTestsetId( int testsetId ) {
		this.testsetId = testsetId;
	}
	
	public String getProduct() {
		return product;
	}

	public void setProduct( String product ) {
		this.product = product;
	}

	public List<TestResult> getResults() {
		return results;
	}

	public void setResults( List<TestResult> results ) {
		this.results = results;
	}

	public TestResult getResult( int caseId ) {
		for ( TestResult tc : results ) {
			if ( tc.getCaseId()== caseId ) {
				return tc;
			}
		}
		return null;
	}

	public void addResult( TestResult tc ) {
		if ( tc != null )
			results.add( tc );
	}

	public String toString() {
		return CommonUtils.toJson( this );
	}
	
	public List<SubExecution> getSubExecutions() {
		return subExecutions;
	}

	public void setSubExecutions( List<SubExecution> subExecutions ) {
		this.subExecutions = subExecutions;
	}
	
	public void addSubExecution( SubExecution se ) {
		this.subExecutions.add( se );
	}
	
	public SubExecution getSubExecution( int subId ) {
		for( SubExecution se : this.subExecutions ) {
			if(se.getSubId()==subId)
				return se;
		}
		return null;
	}
	
	/**
	 * Transform a test result list to a testcase result list;
	 *  
	 * @return testCaseResult list.
	 */
	public List<TestCaseResult> transform(){
		Map<Integer,TestCaseResult> map = new HashMap<Integer,TestCaseResult>();
		for( TestResult tr : results) {
			TestCase tc = StatisticManager.getTestCaseById(tr.getCaseId());
			if(tc==null || tc.getQcid() == 0) {
				LogUtils.getStatLog().error( "TestResult["+tr+"] can't map to valid test case" );
				continue;
			}
			int qcId = tc.getQcid();
			TestCaseResult tcr = map.get( qcId );
			if( tcr == null) {
				tcr = new TestCaseResult();
				tcr.setQcId( qcId );
				tcr.setFeature( tc.getFeature() );
				tcr.setFeatureGroup( tc.getFeatureGroup() );
				tcr.setCaseName( tc.getCaseName() );
				tcr.setResult( tr.getResult() );
				tcr.addScript( tc.getCaseName(), tr.getResult() );
				if( tr.getBugId() != 0 )
					tcr.addBug( tr.getBugId(), tr.getBugInfo() );
				map.put( qcId, tcr );
			}else {
				tcr.addScript( tc.getCaseName(), tr.getResult() );
				// check Result
				if(tcr.getResult().equalsIgnoreCase( TestResult.FAIL )||tr.getResult().equalsIgnoreCase( TestResult.FAIL )) {
					tcr.setResult( TestResult.FAIL );
				}else if(tcr.getResult().equalsIgnoreCase( TestResult.NORESULT )||tr.getResult().equalsIgnoreCase( TestResult.NORESULT )) {
					tcr.setResult( TestResult.NORESULT );
				}else
					tcr.setResult( TestResult.PASS );
				if( tr.getBugId() != 0 )
					tcr.addBug( tr.getBugId(), tr.getBugInfo() );
			}
			
		}
		return new ArrayList<TestCaseResult>(map.values());
	}
}
