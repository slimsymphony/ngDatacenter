package com.nokia.test.statistic;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class StatisticManager {

	public static int getNextSubId( int execId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select max(subid) from stat_sub_executions where execid=?";
		int nextSubId = 0;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, execId );
			rs = ps.executeQuery();
			if(rs.next()) {
				nextSubId = rs.getInt(1);
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get next Sub Id failed, execId=" + execId, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ++nextSubId;
	}
	
	public static List<Product> getProducts( boolean includeInvaid ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Product> products = new ArrayList<Product>();
		try {
			String sql = null;
			if ( includeInvaid )
				sql = "select * from STAT_PRODUCTS order by id";
			else
				sql = "select * from STAT_PRODUCTS where valid=1 order by id";
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				products.add( parseProduct( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get prodcts failed.includeInvaid=" + includeInvaid, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return products;
	}

	private static Product parseProduct( ResultSet rs ) throws SQLException {
		Product p = new Product();
		p.setId( rs.getInt( "id" ) );
		p.setName( rs.getString( "name" ) );
		p.setPlatform( rs.getString( "platform" ) );
		p.setCreated( rs.getTimestamp( "created" ) );
		p.setValid( rs.getInt( "valid" ) );
		return p;
	}

	public synchronized static int getNextId( Connection conn, String tableName ) {
		boolean needCloseConn = ( conn != null ? false : true );
		String sql = "SHOW TABLE STATUS LIKE '" + tableName + "'";
		LogUtils.getDbLog().debug( "[getNextId]" + sql );
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nextId = -1;
		try {
			if ( needCloseConn )
				conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			rs.next();
			nextId = rs.getInt( "Auto_increment" );
		} catch ( SQLException e ) {
			LogUtils.getStatLog().error( "Get Next Id failed.Tablename=" + tableName, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			if ( needCloseConn )
				CommonUtils.closeQuitely( conn );
		}
		return nextId - 1;
	}

	public static Map<String, List<TestCase>> getAllTestcases( String product ) {
		Map<String, List<TestCase>> cases = new HashMap<String, List<TestCase>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from STAT_TESTCASES where product=? order by featureGroup,feature, qcId,caseId";
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, product );
			rs = ps.executeQuery();
			TestCase tc = null;
			while ( rs.next() ) {
				tc = parseTestCase( rs );
				if ( cases.get( tc.getFeatureGroup() ) == null )
					cases.put( tc.getFeatureGroup(), new ArrayList<TestCase>() );
				cases.get( tc.getFeatureGroup() ).add( tc );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get AllTestcases failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cases;
	}

	public static Testset getTestsetByIdOnly( int id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from STAT_TESTSETS where id=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getTestsetById]" + sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return parseTestsetOnly( rs );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get getTestset By Id:" + id, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return null;
	}

	public static Testset getTestsetOnly( String testSetName, String product ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from STAT_TESTSETS where product=? and name=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getTestsetOnly]" + sql );
			ps.setString( 1, product );
			ps.setString( 2, testSetName );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return parseTestsetOnly( rs );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get getTestset Only Failed testSetName:" + testSetName + ",product=" + product, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return null;
	}

	public static Testset getTestsetById( int id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from STAT_TESTSETS where id=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getTestsetById]" + sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return parseTestset( rs, conn );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get getTestset By Id:" + id, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return null;
	}

	public static List<Testset> getTestsetsOnly( int start, int end, String name, String product ) {
		List<Testset> sets = new ArrayList<Testset>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer( "select * from STAT_TESTSETS where 1=1 " );
		try {
			int i = 0;
			if ( start > 0 ) {
				sb.append( " and id>=? " );
			}
			if ( end > 0 ) {
				sb.append( " and id<=? " );
			}
			if ( name != null && !name.trim().isEmpty() ) {
				sb.append( " and name=? " );
			}
			if ( product != null && !product.trim().isEmpty() ) {
				sb.append( " and product=?" );
			}
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sb.toString() );
			LogUtils.getDbLog().debug( "[getTestsets]" + sb.toString() );
			if ( start > 0 ) {
				ps.setInt( ++i, start );
			}
			if ( end > 0 ) {
				ps.setInt( ++i, end );
			}
			if ( name != null && !name.trim().isEmpty() ) {
				ps.setString( ++i, name );
			}
			if ( product != null && !product.trim().isEmpty() ) {
				ps.setString( ++i, product );
			}
			rs = ps.executeQuery();
			while ( rs.next() ) {
				sets.add( parseTestsetOnly( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get getTestsets Only failed. Idstart:" + start + ", idend:" + end + ",name:" + name + ",product:" + product, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return sets;
	}

	public static List<TestCase> getTestcasesByQcId( int qcId, String product ) {
		List<TestCase> cases = new ArrayList<TestCase>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select * from stat_testcases where qcId=? and product=? order by caseId" );
			ps.setInt( 1, qcId );
			ps.setString( 2, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				TestCase tc = parseTestCase( rs );
				if ( tc.getId() > 0 ) {
					cases.add( tc );
				}
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Testcases By QcId failed.qcId=" + qcId + ",product=" + product );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cases;
	}

	public static List<Testset> getTestsets( int start, int end, String name, String product ) {
		List<Testset> sets = new ArrayList<Testset>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer( "select * from STAT_TESTSETS where 1=1 " );
		try {
			int i = 0;
			if ( start > 0 ) {
				sb.append( " and id>=? " );
			}
			if ( end > 0 ) {
				sb.append( " and id<=? " );
			}
			if ( name != null && !name.trim().isEmpty() ) {
				sb.append( " and name=? " );
			}
			if ( product != null && !product.trim().isEmpty() ) {
				sb.append( " and product=?" );
			}
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sb.toString() );
			LogUtils.getDbLog().debug( "[getTestsets]" + sb.toString() );
			if ( start > 0 ) {
				ps.setInt( ++i, start );
			}
			if ( end > 0 ) {
				ps.setInt( ++i, end );
			}
			if ( name != null && !name.trim().isEmpty() ) {
				ps.setString( ++i, name );
			}
			if ( product != null && !product.trim().isEmpty() ) {
				ps.setString( ++i, product );
			}
			rs = ps.executeQuery();
			while ( rs.next() ) {
				sets.add( parseTestset( rs, conn ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get getTestsets failed. Idstart:" + start + ", idend:" + end + ",name:" + name + ",product:" + product, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return sets;
	}

	public static void fetchCaseIdsForTestset( Testset ts ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select id from STAT_TESTCASES where caseId=? and product=?";
			ps = conn.prepareStatement( sql );
			for ( Map.Entry<String, List<TestCase>> entry : ts.getTestcases().entrySet() ) {
				for ( TestCase tc : entry.getValue() ) {
					ps.setString( 1, tc.getCaseId() );
					ps.setString( 2, ts.getProduct() );
					rs = ps.executeQuery();
					if ( rs.next() ) {
						tc.setId( rs.getInt( 1 ) );
					} else {
						LogUtils.getStatLog().error( "Can't fetch testcase Id from tc:" + tc );
					}
					ps.clearParameters();
				}
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "fetch Case Ids For Testset Failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	public static int addTestset( Testset ts ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		PreparedStatement ps6 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		try {
			conn = CommonUtils.getConnection();
			conn.setAutoCommit( false );
			String sql = "insert into STAT_TESTSETS(name,product) values(?,?)";
			LogUtils.getDbLog().debug( "[addTestset]" + sql );
			ps = conn.prepareStatement( sql );
			String sql2 = "select id from STAT_TESTSETS where name=? and product=?";
			ps4 = conn.prepareStatement( sql2 );
			LogUtils.getDbLog().debug( "[addTestset 2]" + sql );
			ps4.setString( 1, ts.getName() );
			ps4.setString( 2, ts.getProduct() );
			rs2 = ps4.executeQuery();
			if ( rs2.next() ) {
				ts.setId( rs2.getInt( 1 ) );
			} else {
				ps.setString( 1, ts.getName() );
				ps.setString( 2, ts.getProduct() );
				ps.executeUpdate();
				ts.setId( getNextId( conn, "STAT_TESTSETS" ) );
				LogUtils.getStatLog().info( "Add new testset:" + ts.getId() + "~" + ts.getName() );
			}
			rs2.close();

			if ( !ts.getTestcases().isEmpty() ) {
				String sql3 = "select id,feature,caseName from STAT_TESTCASES where caseId=? and product=?";
				ps1 = conn.prepareStatement( sql3 );
				String sql4 = "insert into STAT_TESTCASES(caseId,caseName,featureGroup,product,feature,qcid,file,directory,class,method) values(?,?,?,?,?,?,?,?,?,?)";
				ps2 = conn.prepareStatement( sql4 );
				String sql5 = "insert into STAT_TESTSET_CASES(testsetId,testcaseId) values(?,?)";
				ps3 = conn.prepareStatement( sql5 );
				String sql6 = "select count(1) from STAT_TESTSET_CASES where testsetId=? and testcaseId=?";
				ps5 = conn.prepareStatement( sql6 );
				String sql7 = "update  STAT_TESTCASES set feature=?,caseName=?,qcid=?,file=?,directory=?,class=?,method=? where id=?";
				ps6 = conn.prepareStatement( sql7 );
				LogUtils.getDbLog().debug( "[addTestset 3]" + sql3 );
				LogUtils.getDbLog().debug( "[addTestset 4]" + sql4 );
				LogUtils.getDbLog().debug( "[addTestset 5]" + sql5 );
				LogUtils.getDbLog().debug( "[addTestset 6]" + sql6 );
				LogUtils.getDbLog().debug( "[addTestset 7]" + sql7 );
				for ( Entry<String, List<TestCase>> entry : ts.getTestcases().entrySet() ) {
					String featureGroup = entry.getKey();
					List<TestCase> cases = entry.getValue();
					for ( TestCase tc : cases ) {
						ps1.setString( 1, tc.getCaseId() );
						ps1.setString( 2, tc.getProduct() );
						rs1 = ps1.executeQuery();
						if ( rs1.next() ) {
							tc.setId( rs1.getInt( 1 ) );
							String fea = rs1.getString( 2 );
							String caseName = rs1.getString( 3 );
							if ( fea == null || fea.equals( "" ) || !caseName.equals( tc.getCaseName() ) ) {
								ps6.setString( 1, tc.getFeature() );
								ps6.setString( 2, tc.getCaseName() );
								ps6.setInt( 3, tc.getQcid() );
								ps6.setString( 4, tc.getFile() );
								ps6.setString( 5, tc.getDirectory() );
								ps6.setString( 6, tc.getTestClass() );
								ps6.setString( 7, tc.getMethod() );
								ps6.setInt( 8, tc.getId() );
								ps6.executeUpdate();
								ps6.clearParameters();
								LogUtils.getStatLog().info( "Fix added feature or update caseName for: " + tc );
							}
						} else {
							ps2.setString( 1, tc.getCaseId() );
							ps2.setString( 2, tc.getCaseName() );
							ps2.setString( 3, featureGroup );
							ps2.setString( 4, tc.getProduct() );
							ps2.setString( 5, tc.getFeature() );
							String scid = tc.getCaseId();
							int qcid = CommonUtils.parseInt( scid.substring( scid.indexOf( "-" ) + 1, scid.length() ).trim(), 0 );
							tc.setQcid( qcid );
							ps2.setInt( 6, qcid );
							ps2.setString( 7, tc.getFile() );
							ps2.setString( 8, tc.getDirectory() );
							ps2.setString( 9, tc.getTestClass() );
							ps2.setString( 10, tc.getMethod() );
							ps2.executeUpdate();
							tc.setId( getNextId( conn, "STAT_TESTCASES" ) );
							ps2.clearParameters();
							LogUtils.getStatLog().info( "Add new testcase: " + tc );
						}
						rs1.close();
						ps1.clearParameters();

						ps5.setInt( 1, ts.getId() );
						ps5.setInt( 2, tc.getId() );
						rs3 = ps5.executeQuery();
						rs3.next();
						int cnt = rs3.getInt( 1 );
						rs3.close();
						ps5.clearParameters();
						if ( cnt == 0 ) {
							ps3.setInt( 1, ts.getId() );
							ps3.setInt( 2, tc.getId() );
							ps3.executeUpdate();
							ps3.clearParameters();
							LogUtils.getStatLog().info( "Add new testcase and testset releation for: " + tc );
						}
					}
				}
			}
			conn.commit();
			conn.setAutoCommit( true );
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			LogUtils.getStatLog().error( "Add testset failed:" + ts, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps6 );
			CommonUtils.closeQuitely( ps5 );
			CommonUtils.closeQuitely( ps4 );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps1 );
			CommonUtils.closeQuitely( ps3 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ts.getId();
	}

	private static Testset parseTestsetOnly( ResultSet rs ) throws SQLException {
		Testset ts = new Testset();
		ts.setId( rs.getInt( "id" ) );
		ts.setName( rs.getString( "name" ) );
		ts.setProduct( rs.getString( "product" ) );
		return ts;
	}

	private static Testset parseTestset( ResultSet rs, Connection conn ) throws SQLException {
		Testset ts = new Testset();
		ts.setId( rs.getInt( "id" ) );
		ts.setName( rs.getString( "name" ) );
		ts.setProduct( rs.getString( "product" ) );
		PreparedStatement ps = null;
		ResultSet rst = null;
		try {
			String sql = "select * from STAT_TESTCASES where id in (select testcaseId from STAT_TESTSET_CASES where testsetId=?) order by featureGroup,feature,id";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, ts.getId() );
			rst = ps.executeQuery();
			while ( rst.next() ) {
				TestCase tc = parseTestCase( rst );
				ts.addTestCase( tc.getFeatureGroup(), tc );
			}
		} catch ( Exception e ) {

		} finally {
			CommonUtils.closeQuitely( rst );
			CommonUtils.closeQuitely( ps );
		}
		return ts;
	}

	private static TestCase parseTestCase( ResultSet rs ) throws SQLException {
		TestCase cas = new TestCase();
		cas.setId( rs.getInt( "id" ) );
		cas.setCaseId( rs.getString( "caseId" ) );
		cas.setCaseName( rs.getString( "caseName" ) );
		cas.setFeatureGroup( rs.getString( "featureGroup" ) );
		cas.setFeature( rs.getString( "feature" ) );
		cas.setProduct( rs.getString( "product" ) );
		cas.setMethod( rs.getString( "method" ) );
		cas.setPath( rs.getString( "path" ) );
		cas.setTestClass( rs.getString( "class" ) );
		int qcId = rs.getInt( "qcid" );
		if(qcId == 0) {
			qcId = CommonUtils.parseInt( cas.getCaseId().substring( cas.getCaseId().indexOf( "-" )+1 ).trim(), 0  );
		}
		cas.setQcid( qcId );
		cas.setQcIdentifier( rs.getInt( "qcIdentifier" ) );
		cas.setFile( rs.getString( "file" ) );
		cas.setDirectory( rs.getString( "directory" ) );
		return cas;
	}

	public static void appendExecutionResults( TestExecution te, SubExecution se ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		boolean ret = false;
		try {
			conn = CommonUtils.getConnection();
			conn.setAutoCommit( false );
			String sql = "select max(id) from STAT_EXECUTIONS where product=? and name like ?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[appendExecutionResults]" + sql );
			ps.setString( 1, te.getProduct() );
			ps.setString( 2, te.getName() + "%" );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				int execId = rs.getInt( 1 );
				se.setExecId( execId );
				rs.close();
				if ( !te.getResults().isEmpty() ) {
					ret = appendSubExecution( se );
					String sql2 = "insert into STAT_RESULTS(caseId,result,execId,duration,message,detail,reference,subid) values(?,?,?,?,?,?,?,?)";
					ps2 = conn.prepareStatement( sql2 );
					LogUtils.getDbLog().debug( "[appendExecutionResults 2]" + sql2 );
					for ( TestResult tr : te.getResults() ) {
						try {
							ps2.setInt( 1, tr.getCaseId() );
							ps2.setString( 2, tr.getResult() );
							ps2.setInt( 3, execId );
							ps2.setInt( 4, tr.getDuration() );
							ps2.setString( 5, CommonUtils.getMax( tr.getMessage(), 250 ) );
							ps2.setString( 6, CommonUtils.getMax( tr.getDetail(), 500 ) );
							ps2.setString( 7, tr.getReference() );
							ps2.setInt( 8, se.getSubId() );
							ps2.executeUpdate();
						} catch ( Exception ex ) {
							LogUtils.getStatLog().error( "[append test result] failed when insert test result.tr=" + tr, ex );
						} finally {
							ps2.clearParameters();
						}
					}
					conn.commit();
					conn.setAutoCommit( true );
					LogUtils.getStatLog().info( "add sub execution result:"+ret );
				}
			}
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			LogUtils.getStatLog().error( "Append execution result failed, te=" + te, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	public static void updateExecutionResults( TestExecution te ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			conn.setAutoCommit( false );
			String sql = "select max(id) from STAT_EXECUTIONS where product=? and name like ?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[updateExecutionResults]" + sql );
			ps.setString( 1, te.getProduct() );
			ps.setString( 2, te.getName() + "%" );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				int execId = rs.getInt( 1 );
				rs.close();
				if ( !te.getResults().isEmpty() ) {
					String sql2 = "update STAT_RESULTS set result=?, message='',detail='',duration=?,reference=? where  execId=? and caseId=?";
					ps2 = conn.prepareStatement( sql2 );
					LogUtils.getDbLog().debug( "[updateExecutionresults 2]" + sql2 );
					for ( TestResult tr : te.getResults() ) {
						if ( TestResult.PASS.equalsIgnoreCase( tr.getResult() ) ) {
							ps2.setString( 1, tr.getResult() );
							ps2.setInt( 2, tr.getDuration() );
							ps2.setString( 3, tr.getReference() );
							ps2.setInt( 4, execId );
							ps2.setInt( 5, tr.getCaseId() );
							ps2.executeUpdate();
							ps2.clearParameters();
						}
					}
					conn.commit();
					conn.setAutoCommit( true );
				}
			}
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			LogUtils.getStatLog().error( "Update execution result failed, te=" + te, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	public static int addExecution( TestExecution te, String originalContent ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			conn.setAutoCommit( false );
			String sql = "insert into STAT_EXECUTIONS(exec_time,passCnt,failCnt,noresultCnt,url,testsetId,product,name,duration,sw,type,report,source) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[addExecution]" + sql );
			ps.setTimestamp( 1, te.getExecTime() );
			ps.setInt( 2, te.getPassCnt() );
			ps.setInt( 3, te.getFailCnt() );
			ps.setInt( 4, te.getNoResultCnt() );
			ps.setString( 5, te.getUrl() );
			ps.setInt( 6, te.getTestsetId() );
			ps.setString( 7, te.getProduct() );
			ps.setString( 8, te.getName() );
			ps.setInt( 9, te.getDuration() );
			ps.setString( 10, te.getSw() );
			ps.setString( 11, te.getType() );
			ps.setClob( 12, new StringReader( originalContent ) );
			ps.setString( 13, te.getFrom() );
			ps.executeUpdate();
			te.setId( getNextId( conn, "STAT_EXECUTIONS" ) );
			if ( !te.getResults().isEmpty() ) {
				String sql2 = "insert into STAT_RESULTS(caseId,result,execId,duration,message,detail,reference) values(?,?,?,?,?,?,?)";
				ps2 = conn.prepareStatement( sql2 );
				LogUtils.getDbLog().debug( "[addExecution 2]" + sql2 );
				for ( TestResult tr : te.getResults() ) {
					try {
						tr.setExecId( te.getId() );
						ps2.setInt( 1, tr.getCaseId() );
						ps2.setString( 2, tr.getResult() );
						ps2.setInt( 3, tr.getExecId() );
						ps2.setInt( 4, tr.getDuration() );
						ps2.setString( 5, CommonUtils.getMax( tr.getMessage(), 250 ) );
						ps2.setString( 6, CommonUtils.getMax( tr.getDetail(), 500 ) );
						ps2.setString( 7, tr.getReference() );
						ps2.executeUpdate();
					} catch ( Exception ioe ) {
						LogUtils.getStatLog().error( "Insert stat_result failed:" + tr, ioe);
					} finally {
						ps2.clearParameters();
					}
				}
			}
			conn.commit();
			conn.setAutoCommit( true );
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			LogUtils.getStatLog().error( "Add execution failed, te=" + te, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return te.getId();
	}

	public static int addExecution( TestExecution te ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			conn.setAutoCommit( false );
			String sql = "insert into STAT_EXECUTIONS(exec_time,passCnt,failCnt,noresultCnt,url,testsetId,product,name,duration,sw,type,source) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[addExecution]" + sql );
			ps.setTimestamp( 1, te.getExecTime() );
			ps.setInt( 2, te.getPassCnt() );
			ps.setInt( 3, te.getFailCnt() );
			ps.setInt( 4, te.getNoResultCnt() );
			ps.setString( 5, te.getUrl() );
			ps.setInt( 6, te.getTestsetId() );
			ps.setString( 7, te.getProduct() );
			ps.setString( 8, te.getName() );
			ps.setInt( 9, te.getDuration() );
			ps.setString( 10, te.getSw() );
			ps.setString( 11, te.getType() );
			ps.setString( 12, te.getFrom() );
			ps.executeUpdate();
			te.setId( getNextId( conn, "STAT_EXECUTIONS" ) );
			if ( !te.getResults().isEmpty() ) {
				String sql2 = "insert into STAT_RESULTS(caseId,result,execId,duration,message,detail,reference) values(?,?,?,?,?,?,?)";
				ps2 = conn.prepareStatement( sql2 );
				LogUtils.getDbLog().debug( "[addExecution 2]" + sql2 );
				for ( TestResult tr : te.getResults() ) {
					try {
						tr.setExecId( te.getId() );
						ps2.setInt( 1, tr.getCaseId() );
						ps2.setString( 2, tr.getResult() );
						ps2.setInt( 3, tr.getExecId() );
						ps2.setInt( 4, tr.getDuration() );
						ps2.setString( 5, CommonUtils.getMax( tr.getMessage(), 250 ) );
						ps2.setString( 6, CommonUtils.getMax( tr.getDetail(), 500 ) );
						ps2.setString( 7, tr.getReference() );
						ps2.executeUpdate();
					} catch ( Exception e ) {
						LogUtils.getStatLog().error( "Add TestResult to DB failed. teId:" + te.getId() + ",tr=" + tr, e );
					} finally {
						ps2.clearParameters();
					}
				}
			}
			conn.commit();
			conn.setAutoCommit( true );
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			LogUtils.getStatLog().error( "Add execution failed, te=" + te, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return te.getId();
	}

	public static int getCaseAvgDuration( int caseId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select avg(duration) from stat_results where caseId=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, caseId );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return ( int ) Math.ceil( rs.getFloat( 1 ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "get Case Average Duration: Case[" + caseId + "] failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return 0;
	}

	public static int getCaseAvgDuration( PreparedStatement ps, int caseId ) {
		ResultSet rs = null;
		try {
			ps.setInt( 1, caseId );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return ( int ) Math.ceil( rs.getFloat( 1 ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "get Case Average Duration: Case[" + caseId + "] failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			try {
				ps.clearParameters();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static TestExecution getExecutionById( int teId ) {
		TestExecution te = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from STAT_EXECUTIONS where id=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getExecutionById]" + sql );
			ps.setInt( 1, teId );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				te = parseExecution( rs, conn );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Execution By Id:" + teId + " failed.", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return te;
	}

	public static List<TestExecution> getExecutions( String product, Timestamp start, Timestamp end ) {
		List<TestExecution> exs = new ArrayList<TestExecution>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			if ( start == null )
				start = new Timestamp( System.currentTimeMillis() - 10000000000L );
			if ( end == null )
				end = new Timestamp( System.currentTimeMillis() );
			String sql = "select * from STAT_EXECUTIONS where (exec_time between ? and ?) ";
			if ( product != null ) {
				sql += " and product=?";
			}
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getExecutions]" + sql );
			ps.setTimestamp( 1, start );
			ps.setTimestamp( 2, end );
			if ( product != null )
				ps.setString( 3, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				exs.add( parseExecution( rs, conn ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Execution between [" + start + ":" + end + "]", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return exs;
	}

	public static Map<Integer, List<TestExecution>> getExecutionGroup( String product, Timestamp start, Timestamp end ) {
		Map<Integer, List<TestExecution>> exs = new HashMap<Integer, List<TestExecution>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			if ( start == null )
				start = new Timestamp( System.currentTimeMillis() - 10000000000L );
			if ( end == null )
				end = new Timestamp( System.currentTimeMillis() );
			String sql = "select * from STAT_EXECUTIONS where (exec_time between ? and ?) ";
			if ( product != null ) {
				sql += " and product=? ";
			}
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getExecutionGroup]" + sql );
			ps.setTimestamp( 1, start );
			ps.setTimestamp( 2, end );
			if ( product != null )
				ps.setString( 3, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				TestExecution te = parseExecution( rs, conn );
				if ( exs.get( te.getTestsetId() ) == null ) {
					exs.put( te.getTestsetId(), new ArrayList<TestExecution>() );
				}
				exs.get( te.getTestsetId() ).add( te );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Execution between [" + start + ":" + end + "]", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return exs;
	}

	public static List<TestExecution> getExecutionsByTestset( String product, int tsId, String type, Timestamp start, Timestamp end ) {
		List<TestExecution> exs = new ArrayList<TestExecution>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			if ( start == null )
				start = new Timestamp( System.currentTimeMillis() - 10000000000L );
			if ( end == null )
				end = new Timestamp( System.currentTimeMillis() );
			String sql = "select * from STAT_EXECUTIONS where testsetId=? and (exec_time between ? and ?) ";
			if ( product != null ) {
				sql += " and product=? ";
			}
			if( type !=null && !type.trim().isEmpty())
				sql += " and type=? ";
			sql += " order by exec_time";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getExecutionsByTestset]" + sql );
			ps.setInt( 1, tsId );
			ps.setTimestamp( 2, start );
			ps.setTimestamp( 3, end );
			if ( product != null )
				ps.setString( 4, product );
			if( type !=null && !type.trim().isEmpty())
				ps.setString( 5, type );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				exs.add( parseExecution( rs, conn ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Execution between [" + start + ":" + end + "]", e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return exs;
	}
	
	public static List<TestExecution> getExecutionsByTestset( String product, int tsId, Timestamp start, Timestamp end ) {
		return getExecutionsByTestset(product, tsId, "" ,start, end);
	}

	private static TestExecution parseExecution( ResultSet rs, Connection conn ) throws SQLException {
		TestExecution te = new TestExecution();
		te.setName( rs.getString( "name" ) );
		te.setId( rs.getInt( "id" ) );
		te.setExecTime( rs.getTimestamp( "exec_time" ) );
		te.setUrl( rs.getString( "url" ) );
		te.setTestsetId( rs.getInt( "testsetId" ) );
		te.setProduct( rs.getString( "product" ) );
		te.setSw( rs.getString( "sw" ) );
		te.setType( rs.getString( "type" ) );
		te.setFrom( rs.getString("source") );
		PreparedStatement ps = null;
		ResultSet rst = null;
		try {
			// String sql = "select * from STAT_RESULTS where execId=? order by featureGroup,caseId";
			String sql = "select STAT_RESULTS.* from STAT_RESULTS,STAT_TESTCASES where execId=? and STAT_RESULTS.caseId=STAT_TESTCASES.id order by featureGroup,feature,qcid,STAT_TESTCASES.caseId";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[parseExecution]" + sql );
			ps.setInt( 1, te.getId() );
			rst = ps.executeQuery();
			while ( rst.next() ) {
				TestResult tr = new TestResult();
				tr.setCaseId( rst.getInt( "caseId" ) );
				tr.setResult( rst.getString( "result" ) );
				tr.setMessage( rst.getString( "message" ) );
				tr.setDetail( rst.getString( "detail" ) );
				tr.setExecId( te.getId() );
				tr.setDuration( rst.getInt( "duration" ) );
				tr.setBugId( rst.getInt( "bugId" ) );
				tr.setBugInfo( rst.getString( "bugInfo" ) );
				tr.setReference( rst.getString( "reference" ) );
				tr.setOriResult( rst.getString( "oriresult" ) );
				tr.setSubId( rst.getInt( "subid" ) );
				te.addResult( tr );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "parse testExecution failed, TestExecution=" + te, e );
		} finally {
			CommonUtils.closeQuitely( rst );
			CommonUtils.closeQuitely( ps );
		}
		return te;
	}

	public static TestCase getTestCaseById( int id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		TestCase cas = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from STAT_TESTCASES where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				cas = parseTestCase( rs );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get TestCase  by id failed, id:" + id, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cas;
	}

	public static Testset createRerunTestset( int execId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Testset ts = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select stat_testcases.* from stat_results left join stat_testcases on stat_results.caseid=stat_testcases.id where execId=? and result!='PASS'";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, execId );
			rs = ps.executeQuery();
			ts = new Testset();
			while ( rs.next() ) {
				TestCase tc = parseTestCase( rs );
				ts.addTestCase( tc.getFeatureGroup(), tc );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Create Rerun testset failed,execId=" + execId, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ts;
	}

	public static TestCase getTestCaseById( PreparedStatement ps, int id ) {
		TestCase cas = null;
		ResultSet rs = null;
		try {
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				cas = parseTestCase( rs );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get TestCase  by id failed, id:" + id, e );
		} finally {
			CommonUtils.closeQuitely( rs );
		}
		return cas;
	}

	public static int addTestCase( TestCase ts ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "insert into STAT_TESTCASES(caseId,caseName,featureGroup,product,feature,file,directory,class,method,qcid) values(?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[addTestCase]" + sql );
			ps.setString( 1, ts.getCaseId() );
			ps.setString( 2, ts.getCaseName() );
			ps.setString( 3, ts.getFeatureGroup() );
			ps.setString( 4, ts.getProduct() );
			ps.setString( 5, ts.getFeature() );
			ps.setString( 6, ts.getFile() );
			ps.setString( 7, ts.getDirectory() );
			ps.setString( 8, ts.getTestClass() );
			ps.setString( 9, ts.getMethod() );
			ps.setInt( 10, ts.getQcid() );
			ps.executeUpdate();
			ts.setId( getNextId( conn, "STAT_TESTCASES" ) );
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Add TestCase failed, ts:" + ts, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ts.getId();
	}

	public static void bindBugInfo( int execId, int caseId, int bugId, String bugInfo ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update STAT_RESULTS set bugId=?,bugInfo=? where execId=? and caseId=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[bindBugInfo]" + sql );
			ps.setInt( 1, bugId );
			ps.setString( 2, bugInfo );
			ps.setInt( 3, execId );
			ps.setInt( 4, caseId );
			ps.executeUpdate();
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Bind BugInfo failed, execId:" + execId + ",caseId:" + caseId + ",bugId:" + bugId + ",bugInfo:" + bugInfo, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	public static List<String> getFeatureGroupByExecution( int teId ) {
		List<String> fgs = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select distinct featureGroup from STAT_TESTCASES,STAT_RESULTS where STAT_TESTCASES.id=STAT_RESULTS.caseId and STAT_RESULTS.execId=? order by featureGroup";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getFeatureGroupByExecution]" + sql );
			ps.setInt( 1, teId );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String fg = rs.getString( 1 );
				fgs.add( fg );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get FeatureGroup by test execution failed, teId:" + teId, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return fgs;
	}

	public static TestCase getTestCaseByCaseId( String caseId, String product ) {
		TestCase cas = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from STAT_TESTCASES where caseId=? and product=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getTestCaseByCaseId]" + sql );
			ps.setString( 1, caseId );
			ps.setString( 2, product );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				cas = parseTestCase( rs );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get TestCase by caseId failed, caseId:" + caseId, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cas;
	}

	public static void updateTestResult( int execId, int caseId, String result ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update STAT_RESULTS set result=? where execId=? and caseId=?";
			if ( result.equals( TestResult.PASS ) ) {
				sql = "update STAT_RESULTS set result=?, bugId=0, bugInfo='' where execId=? and caseId=?";
			}
			LogUtils.getDbLog().debug( "[updateTestResult]" + sql );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, result.toUpperCase() );
			ps.setInt( 2, execId );
			ps.setInt( 3, caseId );
			ps.executeUpdate();
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Update Test Result failed, execId:" + execId + " caseId:" + caseId + ",result:" + result, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	public static Map<String, Map<Integer, String>> getSwVersionsByProduct( String product ) {
		Map<String, Map<Integer, String>> sws = new LinkedHashMap<String, Map<Integer, String>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select STAT_TESTSETS.name,sw,STAT_EXECUTIONS.id from STAT_EXECUTIONS left join STAT_TESTSETS on STAT_EXECUTIONS.testsetId=STAT_TESTSETS.id where  STAT_EXECUTIONS.product=? group by testsetId,sw order by exec_time desc";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getSwVersionsByProduct]" + sql );
			ps.setString( 1, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String ts = rs.getString( 1 );
				String sw = rs.getString( 2 );
				int teId = rs.getInt( 3 );
				if ( sws.get( ts ) == null ) {
					sws.put( ts, new LinkedHashMap<Integer, String>() );
				}
				sws.get( ts ).put( teId, sw );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "get Sw Versions By Product Failed. product:" + product, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return sws;
	}

	public static Map<TestCase, Integer> getTopDurationTestcases( String product, int topCount, Timestamp start, Timestamp end ) {
		Map<TestCase, Integer> cases = new LinkedHashMap<TestCase, Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if ( start == null )
			start = new Timestamp( System.currentTimeMillis() - 10000000000L );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		try {
			conn = CommonUtils.getConnection();
			String sql = "select caseId,duration from STAT_RESULTS where exists (select id from STAT_TESTCASES where product=? and id=STAT_RESULTS.caseId) and execId in (select id from stat_executions where exec_time between ? and ?) order by duration desc";
			LogUtils.getDbLog().debug( "[getTopDurationTestcases 1]" + sql );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, product );
			ps.setTimestamp( 2, start );
			ps.setTimestamp( 3, end );
			rs = ps.executeQuery();
			Map<Integer, Integer> keys = new LinkedHashMap<Integer, Integer>();
			while ( rs.next() ) {
				int caseId = rs.getInt( 1 );
				int dur = rs.getInt( 2 );
				if ( !keys.containsKey( caseId ) )
					keys.put( caseId, dur );
				if ( keys.size() == topCount )
					break;
			}
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			sql = "select * from STAT_TESTCASES where id=?";
			LogUtils.getDbLog().debug( "[getTopDurationTestcases 2]" + sql );
			ps = conn.prepareStatement( sql );
			for ( int cid : keys.keySet() ) {
				ps.setInt( 1, cid );
				rs = ps.executeQuery();
				if ( rs.next() )
					cases.put( parseTestCase( rs ), keys.get( cid ) );
				else
					LogUtils.getStatLog().warn( "Case [" + cid + "] can't find case info." );
				CommonUtils.closeQuitely( rs );
				ps.clearParameters();
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "get Top Duration Testcases failed.product=" + product + ",count=" + topCount, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cases;
	}

	public static Map<TestCase, Integer> getTopDurationTestcasesInTestset( int tsId, int topCount, Timestamp start, Timestamp end ) {
		Map<TestCase, Integer> cases = new LinkedHashMap<TestCase, Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if ( start == null )
			start = new Timestamp( System.currentTimeMillis() - 10000000000L );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() );
		try {
			conn = CommonUtils.getConnection();
			String sql = "select caseId,duration from STAT_RESULTS where exists (select * from STAT_TESTSET_CASES where testsetId=? and testcaseId=STAT_RESULTS.caseId) and execId in (select id from stat_executions where exec_time between ? and ?) order by duration desc";
			LogUtils.getDbLog().debug( "[getTopDurationTestcases 1]" + sql );
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, tsId );
			rs = ps.executeQuery();
			Map<Integer, Integer> keys = new LinkedHashMap<Integer, Integer>();
			while ( rs.next() ) {
				int caseId = rs.getInt( 1 );
				int dur = rs.getInt( 2 );
				if ( !keys.containsKey( caseId ) )
					keys.put( caseId, dur );
				if ( keys.size() == topCount )
					break;
			}
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			sql = "select * from STAT_TESTCASES where id=?";
			LogUtils.getDbLog().debug( "[getTopDurationTestcases 2]" + sql );
			ps = conn.prepareStatement( sql );
			for ( int cid : keys.keySet() ) {
				ps.setInt( 1, cid );
				rs = ps.executeQuery();
				if ( rs.next() )
					cases.put( parseTestCase( rs ), keys.get( cid ) );
				else
					LogUtils.getStatLog().warn( "Case [" + cid + "] can't find case info." );
				CommonUtils.closeQuitely( rs );
				ps.clearParameters();
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "get Top Duration Testcases failed.tsId=" + tsId + ",count=" + topCount, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cases;
	}
	
	public static Map<Object[], Map<Integer, String>> getBugInfosByProduct( boolean swErrorOnly, String product ) {
		Map<Object[], Map<Integer, String>> bugs = new LinkedHashMap<Object[], Map<Integer, String>>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			conn = CommonUtils.getConnection();
			String con = null;
			if( swErrorOnly )
				con = "result='FAIL'";
			else
				con = "result!='PASS'";
			ps = conn.prepareStatement( "select bugId,bugInfo,count(bugId) cnt from stat_results where execId in (select id from stat_executions where product=?) and bugId>0 and " + con + " group by bugId  order by cnt desc" );
			ps2 = conn.prepareStatement( "select distinct stat_results.caseId, casename from stat_results left join stat_testcases on stat_results.caseId=stat_testcases.id where bugId=? and product=?" );
			ps.setString( 1, product );
			rs = ps.executeQuery();
			int bugId = 0;
			int occur = 0;
			String bugInfo = null;
			Map<Integer, String> val = null;
			while ( rs.next() ) {
				bugId = rs.getInt( 1 );
				bugInfo = rs.getString( 2 );
				occur = rs.getInt( 3 );
				Object[] key = { bugId, bugInfo, occur };
				val = new LinkedHashMap<Integer, String>();
				bugs.put( key, val );
				ps2.setInt( 1, bugId );
				ps2.setString( 2, product );
				rs2 = ps2.executeQuery();
				while ( rs2.next() ) {
					val.put( rs2.getInt( 1 ), rs2.getString( 2 ) );
				}
				CommonUtils.closeQuitely( rs2 );
				ps2.clearParameters();
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Bugs Info failed.product="+product+",swErrorOnly="+swErrorOnly, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( rs2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
		return bugs;
	}

	public static Map<Object[], Map<Integer, String>> getBugInfos( boolean swErrorOnly ) {
		Map<Object[], Map<Integer, String>> bugs = new LinkedHashMap<Object[], Map<Integer, String>>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			conn = CommonUtils.getConnection();
			String con = null;
			if( swErrorOnly )
				con = "result='FAIL'";
			else
				con = "result!='PASS'";
			ps = conn.prepareStatement( "select bugId,bugInfo,count(bugId) cnt from stat_results where bugId>0 and " + con + " group by bugId  order by cnt desc" );
			ps2 = conn.prepareStatement( "select distinct stat_results.caseId, casename from stat_results left join stat_testcases on stat_results.caseId=stat_testcases.id where bugId=?" );
			rs = ps.executeQuery();
			int bugId = 0;
			int occur = 0;
			String bugInfo = null;
			Map<Integer, String> val = null;
			while ( rs.next() ) {
				bugId = rs.getInt( 1 );
				bugInfo = rs.getString( 2 );
				occur = rs.getInt( 3 );
				Object[] key = { bugId, bugInfo, occur };
				val = new LinkedHashMap<Integer, String>();
				bugs.put( key, val );
				ps2.setInt( 1, bugId );
				rs2 = ps2.executeQuery();
				while ( rs2.next() ) {
					val.put( rs2.getInt( 1 ), rs2.getString( 2 ) );
				}
				CommonUtils.closeQuitely( rs2 );
				ps2.clearParameters();
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Bugs Info failed.swErrorOnly="+swErrorOnly, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( rs2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
		return bugs;
	}

	public static Map<Integer, String> getCaseBugHistory( int caseId ) {
		Map<Integer, String> buginfos = new HashMap<Integer, String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select bugId,bugInfo from stat_results where caseId=? and bugId>0 and result='FAIL' group by bugId";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, caseId );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				buginfos.put( rs.getInt( 1 ), rs.getString( 2 ) );
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Get Case Bug History failed.caseId=" + caseId, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return buginfos;
	}

	public static List<TestResult> getCaseExecutionHistory(int caseId){
		List<TestResult> results = new ArrayList<TestResult>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from stat_results where caseId=? order by execId";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, caseId );
			rst = ps.executeQuery();
			while(rst.next()) {
				TestResult tr = new TestResult();
				tr.setCaseId( rst.getInt( "caseId" ) );
				tr.setResult( rst.getString( "result" ) );
				tr.setMessage( rst.getString( "message" ) );
				tr.setDetail( rst.getString( "detail" ) );
				tr.setExecId( rst.getInt( "execId" ) );
				tr.setDuration( rst.getInt( "duration" ) );
				tr.setBugId( rst.getInt( "bugId" ) );
				tr.setBugInfo( rst.getString( "bugInfo" ) );
				tr.setReference( rst.getString( "reference" ) );
				tr.setOriResult( rst.getString( "oriresult" ) );
				tr.setSubId( rst.getInt("subId") );
				results.add(tr);
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error("Get Case execution History failed.caseId="+caseId, e);
		}finally {
			CommonUtils.closeQuitely( rst );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return results;
	}
	
	public static Map<String,List<Object[]>> getBugTrendByProduct( boolean swErrorOnly, String product, Timestamp start, Timestamp end ){
		Map<String,List<Object[]>> bugs = new LinkedHashMap<String,List<Object[]>>();
		String con = null;
		if( swErrorOnly )
			con = "result='FAIL'";
		else
			con = "result!='PASS'";
		if ( start == null )
			start = new Timestamp( System.currentTimeMillis() - 10000000000L );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() + 1000000L );
		String sql = "select sw,bugId,bugInfo from stat_results left join stat_executions on execid=id where execId in (select id from stat_executions where product=? and exec_time between ? and ?) and bugId>0 and "+con+" order by exec_time";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, product );
			ps.setTimestamp( 2, start );
			ps.setTimestamp( 3, end );
			rs = ps.executeQuery();
			while(rs.next()) {
				String sw = rs.getString( 1 );
				int bugId = rs.getInt( 2 );
				String bugInfo = rs.getString( 3 );
				if(bugs.get( sw )==null) {
					bugs.put(sw, new ArrayList<Object[]>());
				}
				
				boolean dup = false;
				for(Object[] arr : bugs.get(sw)) {
					if(((Integer)arr[0]).intValue() == bugId) {
						dup = true;
						break;
					}
				}
				if(!dup)
					bugs.get( sw ).add( new Object[]{bugId,bugInfo} );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error("get Bug Trend By Product failed.product="+product, e);
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return bugs;
	}
	
	public static Map<String,List<Object[]>> getBugTrend( boolean swErrorOnly, Timestamp start, Timestamp end  ){
		Map<String,List<Object[]>> bugs = new LinkedHashMap<String,List<Object[]>>();
		String con = null;
		if( swErrorOnly )
			con = "result='FAIL'";
		else
			con = "result!='PASS'";
		if ( start == null )
			start = new Timestamp( System.currentTimeMillis() - 10000000000L );
		if ( end == null )
			end = new Timestamp( System.currentTimeMillis() + 1000000L );
		String sql = "select sw,bugId,bugInfo from stat_results left join stat_executions on execid=id where execId in (select id from stat_executions where exec_time between ? and ?) and bugId>0 and "+con+" order by exec_time";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			ps.setTimestamp( 1, start );
			ps.setTimestamp( 2, end );
			rs = ps.executeQuery();
			while(rs.next()) {
				String sw = rs.getString( 1 );
				int bugId = rs.getInt( 2 );
				String bugInfo = rs.getString( 3 );
				if(bugs.get( sw )==null) {
					bugs.put(sw, new ArrayList<Object[]>());
				}
				bugs.get( sw ).add( new Object[]{bugId,bugInfo} );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error("get Bug Trend failed.", e);
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return bugs;
	}
	
	public static boolean appendSubExecution( SubExecution se ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "insert into stat_sub_executions(execId,subid,url,report) values(?,?,?,?)" );
			ps.setInt( 1, se.getExecId() );
			int subId = getNextSubId(se.getExecId());
			se.setSubId( subId );
			ps.setInt( 2, subId );
			ps.setString( 3, se.getUrl() );
			ps.setClob( 4, new StringReader(se.getReport()) );
			int ret = ps.executeUpdate();
			if(ret == 1)
				return true;
		}catch(Exception e) {
			LogUtils.getStatLog().error( "append Sub execution failed. se:" + se, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return false;
	}
	
	public static SubExecution getSubExecutionsByExecIdAndSubId( int execId, int subId ){
		SubExecution se = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select exectime,url from stat_sub_executions where execId=? and subId=?" );
			ps.setInt( 1, execId );
			ps.setInt( 2, subId );
			rs = ps.executeQuery();
			if( rs.next()) {
				se = new SubExecution();
				se.setExecId( execId );
				se.setSubId( subId );
				se.setExecTime( rs.getTimestamp(1) );
				se.setUrl( rs.getString(2) );
				return se;
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "Get Sub execution by execId&SubID failed. execId:" + execId+",subid:"+subId, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		
		return se;
	}
	
	public static String getSubExecutionReport( int execId, int subId ) {
		String ret = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select report from stat_sub_executions where execId=? and subid=?" );
			ps.setInt( 1, execId );
			ps.setInt( 2, subId );
			rs = ps.executeQuery();
			if(rs.next()) {
				Reader r = null;
				try {
					r = rs.getClob( 1 ).getCharacterStream();
					StringWriter sw = new StringWriter();
					IOUtils.copy( r, sw );
					ret = sw.toString();
				}finally {
					CommonUtils.closeQuitely( r );
				}
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "Get Sub execution report failed. subid:" + subId+",execId:"+execId, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ret;
	}
	
	public static List<SubExecution> getSubExecutionsByExecId( int execId ){
		List<SubExecution> subs = new ArrayList<SubExecution>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select subid,exectime,url from stat_sub_executions where execId=? order by subId" );
			ps.setInt( 1, execId );
			rs = ps.executeQuery();
			while( rs.next()) {
				SubExecution se = new SubExecution();
				se.setExecId( execId );
				se.setSubId( rs.getInt(1) );
				se.setExecTime( rs.getTimestamp(2) );
				se.setUrl( rs.getString(3) );
				subs.add( se );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "Get Sub execution by execId failed. execId:" + execId, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		
		return subs;
	}
	
	public static List<TestExecution> getExecutionInfosBySw( String sw, String testsetName ){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TestExecution> tes = new ArrayList<TestExecution>();
		try {
			conn = CommonUtils.getConnection();
			if(testsetName==null || testsetName.trim().isEmpty()) {
				ps = conn.prepareStatement( "select id from stat_executions where sw=? and testsetid in (select id from stat_testsets where name not like '%fota%') order by id" );
				ps.setString( 1, sw );
			} else {
				ps = conn.prepareStatement( "select id from stat_executions where sw=? and testsetid in (select id from stat_testsets where name=?) order by id" );
				ps.setString( 1, sw );
				ps.setString( 2, testsetName );
			}
			
			rs = ps.executeQuery();
			while( rs.next() ) {
				tes.add( StatisticManager.getExecutionById( rs.getInt( 1 ) ) );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "getExecutionInfosBySw sw:" + sw+",testsetName="+testsetName, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return tes;
	}
	
	public static List<String> getSWVersions() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> sws = new ArrayList<String>();
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select distinct(sw) from stat_executions where sw like 'NG%' order by id desc" );
			rs = ps.executeQuery();
			while( rs.next() ) {
				sws.add( rs.getString( 1 ) );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "getSWVersions failed", e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return sws;
	}
	
	public static List<String> getTestsetNames() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> tns = new ArrayList<String>();
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select distinct(name) from stat_testsets order by id desc" );
			rs = ps.executeQuery();
			while( rs.next() ) {
				tns.add( rs.getString( 1 ) );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "getTestsetNames failed", e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return tns;
	}
	
	public static List<TestExecution> queryExecutionBySw_Product_Name( String sw, String product, String name ){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TestExecution> tes = new ArrayList<TestExecution>();
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select id from stat_executions where sw=? and product=? and name=? order by id" );
			ps.setString( 1, sw );
			ps.setString( 2, product );
			ps.setString( 3, name );
			rs = ps.executeQuery();
			while( rs.next() ) {
				tes.add( StatisticManager.getExecutionById( rs.getInt( 1 ) ) );
			}
		}catch(Exception e) {
			LogUtils.getStatLog().error( "queryExecutionBySw_Product_Name sw:" + sw+",product="+product+",name=" + name, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return tes;
	}
}
