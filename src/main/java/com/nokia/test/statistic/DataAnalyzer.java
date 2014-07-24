package com.nokia.test.statistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class DataAnalyzer {

	@SuppressWarnings( "deprecation" )
	public static String getTopFailureCases( String product, Timestamp start, Timestamp end, int maxCaseCnt, boolean ori ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String[]> results = new ArrayList<String[]>();
		try {
			conn = CommonUtils.getConnection();
			String sql = "select STAT_RESULTS.caseId, count(STAT_RESULTS.caseId) sum, caseName, STAT_RESULTS.message,STAT_RESULTS.detail from STAT_RESULTS, STAT_TESTCASES "
					+ " where execId in (select id from STAT_EXECUTIONS where product=? and exec_time between ? and ? ) "
					+ " and ";
			if(ori)
				sql += "oriresult";
			else
				sql += "result";
			sql += "='" + TestResult.FAIL + "' and STAT_RESULTS.caseId = STAT_TESTCASES.id  GROUP BY STAT_RESULTS.caseId order by sum desc,STAT_TESTCASES.caseId asc ";
			if ( maxCaseCnt > 0 )
				sql += " limit " + maxCaseCnt;
			LogUtils.getDbLog().debug( "[getTopFailureCases]"+sql );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, product );
			ps.setTimestamp( 2, start );
			ps.setTimestamp( 3, end );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				results.add( new String[] { String.valueOf( rs.getInt( 1 ) ), String.valueOf( rs.getInt( 2 ) ), rs.getString( 3 ), rs.getString( 4 ), rs.getString( 5 ) } );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "getTopFailureCases Got exception.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return CommonUtils.toJson( results );
	}
	
	@SuppressWarnings( "deprecation" )
	public static String getTopFailureCasesByTestset( String product, int tsId, Timestamp start, Timestamp end, int maxCaseCnt, boolean ori ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String[]> results = new ArrayList<String[]>();
		try {
			conn = CommonUtils.getConnection();
			String sql = "select STAT_RESULTS.caseId, count(STAT_RESULTS.caseId) sum, caseName, STAT_RESULTS.message,STAT_RESULTS.detail from STAT_RESULTS, STAT_TESTCASES "
					+ " where execId in (select id from STAT_EXECUTIONS where product=? and testsetId=? and exec_time between ? and ? ) "
					+ " and ";
			if(ori)
				sql += "oriresult";
			else
				sql += "result";
			sql += "='" + TestResult.FAIL + "' and STAT_RESULTS.caseId = STAT_TESTCASES.id  GROUP BY STAT_RESULTS.caseId order by sum desc,STAT_TESTCASES.caseId asc ";
			if ( maxCaseCnt > 0 )
				sql += " limit " + maxCaseCnt;
			LogUtils.getDbLog().debug( "[getTopFailureCases]"+sql );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, product );
			ps.setInt( 2, tsId );
			ps.setTimestamp( 3, start );
			ps.setTimestamp( 4, end );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				results.add( new String[] { String.valueOf( rs.getInt( 1 ) ), String.valueOf( rs.getInt( 2 ) ), rs.getString( 3 ), rs.getString( 4 ), rs.getString( 5 ) } );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "getTopFailureCases Got exception.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return CommonUtils.toJson( results );
	}


	@SuppressWarnings( "deprecation" )
	public static String getTopFailureFeatureGroup( String product, Timestamp start, Timestamp end ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String[]> results = new ArrayList<String[]>();
		try {
			conn = CommonUtils.getConnection();
			String sql = "select featureGroup,count(featureGroup) sum from STAT_RESULTS ,STAT_TESTCASES where "
					+ " (execId in ( select id from STAT_EXECUTIONS where product=? and exec_time between ? and ? ))"
					+ " and result='"+TestResult.FAIL+"' and STAT_RESULTS.caseId=STAT_TESTCASES.id "
					+ " group by featureGroup order by sum desc ";
			LogUtils.getDbLog().debug( "[getTopFailureFeatureGroup]"+sql );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, product );
			ps.setTimestamp( 2, start );
			ps.setTimestamp( 3, end );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				results.add( new String[] { rs.getString( 1 ), String.valueOf( rs.getInt( 2 ) ) } );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "getTopFailureFeatureGroup Got exception.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return CommonUtils.toJson( results );

	}

	@SuppressWarnings( "deprecation" )
	public static String getPassrate( String product, int tsId, Timestamp start, Timestamp end, boolean byFeatureGroup  ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis()+10000000L );

		List<TestExecution> ls = StatisticManager.getExecutionsByTestset( product, tsId, start, end );
		if ( byFeatureGroup ) {
			Map<String,Map<String,Float>> map = new TreeMap<String,Map<String,Float>>(); 
			for ( TestExecution te : ls ) {
				map.put( String.valueOf( te.getExecTime() ), te.getPassRateByFeatureGroup() );
			}
			return CommonUtils.toJson( map );
		} else {
			List<String[]> res = new ArrayList<String[]>();
			for ( TestExecution te : ls ) {
				res.add( new String[] {String.valueOf( te.getExecTime() ), te.getSw(), String.valueOf(te.getPassRate()), String.valueOf(te.getId())} );
			}
			return CommonUtils.toJson( res );
		}
	}
	
	@SuppressWarnings( "deprecation" )
	public static String getOriPassrate( String product, int tsId, Timestamp start, Timestamp end, boolean byFeatureGroup  ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis()+100000000L );

		List<TestExecution> ls = StatisticManager.getExecutionsByTestset( product, tsId, start, end );
		if ( byFeatureGroup ) {
			Map<String,Map<String,Float>> map = new TreeMap<String,Map<String,Float>>(); 
			for ( TestExecution te : ls ) {
				map.put( String.valueOf( te.getExecTime() ), te.getOriPassRateByFeatureGroup() );
			}
			return CommonUtils.toJson( map );
		} else {
			List<String[]> res = new ArrayList<String[]>();
			for ( TestExecution te : ls ) {
				res.add( new String[] {String.valueOf( te.getExecTime() ), te.getSw(), String.valueOf(te.getOriPassRate()), String.valueOf(te.getId())} );
			}
			return CommonUtils.toJson( res );
		}
	}

	@SuppressWarnings( "deprecation" )
	public static String getFailureTrend( String product, Timestamp start, Timestamp end ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Map<String, List<String[]>> results = new HashMap<String, List<String[]>>();
		try {
			conn = CommonUtils.getConnection();
			String sql = "select name,id from STAT_TESTSETS where product=?";
			LogUtils.getDbLog().debug( "[getFailureTrend]"+sql );
			ps = conn.prepareStatement( sql );
			String sql2 = "select execId,count(execId), exec_time, sw from STAT_RESULTS, STAT_EXECUTIONS "
					+ " where STAT_EXECUTIONS.id=STAT_RESULTS.execId and execId in "
					+ " (select id from STAT_EXECUTIONS where testsetId=? and (exec_time between ? and ?) and product=? )" 
					+ " and result='"+TestResult.FAIL+"' group by execId order by exec_time";
			ps2 = conn.prepareStatement( sql2 );
			LogUtils.getDbLog().debug( "[getFailureTrend 2]"+sql2 );
			ps.setString( 1, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				results.put( rs.getString( "name" ), new ArrayList<String[]>() );
				int setId = rs.getInt( "id" );
				ps2.setInt( 1, setId );
				ps2.setTimestamp( 2, start );
				ps2.setTimestamp( 3, end );
				ps2.setString( 4, product );
				rs2 = ps2.executeQuery();
				while ( rs2.next() ) {
					results.get( rs.getString( "name" ) ).add(
							new String[] { String.valueOf( rs2.getInt( 1 ) ), String.valueOf( rs2.getInt( 2 ) ), String.valueOf( rs2.getTimestamp( 3 ) ), rs2.getString( 4 ) } );
				}
				rs2.close();
				ps2.clearParameters();
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "getFailureTrend Got exception.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return CommonUtils.toJson( results );
	}
	
	@SuppressWarnings( "deprecation" )
	public static String getPassTrend( String product, Timestamp start, Timestamp end ) {
		if ( start == null )
			start = new Timestamp( new Date( 100, 0, 1 ).getTime() );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, List<String[]>> results = new HashMap<String, List<String[]>>();
		try {
			conn = CommonUtils.getConnection();
			//String sql = "select STAT_TESTSETS.name,passCnt/(passCnt+failCnt+noresultCnt),exec_time,sw from STAT_EXECUTIONS, STAT_TESTSETS " +
			//		" where STAT_EXECUTIONS.testsetId=STAT_TESTSETS.id and (exec_time between ? and ?) and STAT_EXECUTIONS.product=? order by exec_time";
			String sql = "select C.name,A.pass,A.fail,A.nore,B.exec_time,B.sw, B.id from "+
						" (select execid ,sum(case when result='PASS' then 1 else 0 end) pass ,sum(case when result='FAIL' then 1 else 0 end) fail,sum(case when result='NORESULT' then 1 else 0 end) nore "+
						" from STAT_RESULTS  group by execid) A, STAT_EXECUTIONS B,STAT_TESTSETS C " +
						" where A.execId=B.id and B.testsetId=C.id and (B.exec_time between ? and ?) and B.product=? order by exec_time";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getPassTrend]"+sql );
			ps.setTimestamp( 1, start );
			ps.setTimestamp( 2, end );
			ps.setString( 3, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String name = rs.getString("name");
				if(results.get( name )==null) {
					results.put( name, new ArrayList<String[]>() );
				}
				int pass = rs.getInt(2);
				int fail = rs.getInt(3);
				int norun = rs.getInt(4);
				Timestamp tm = rs.getTimestamp(5);
				String sw = rs.getString(6);
				int teId = rs.getInt(7);
				results.get( name ).add(
						new String[] { String.valueOf( ((float)pass/(float)(pass+fail+norun))*100f ), String.valueOf( tm ), sw, String.valueOf(teId), String.valueOf(pass),String.valueOf(fail),String.valueOf(norun) } );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "getPassTrend Got exception.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return CommonUtils.toJson( results );
	}

}
