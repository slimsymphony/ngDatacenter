import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.TestResult;

public class FailureTestResultImport {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Connection conn = null;
		try {
			conn = CommonUtils.getConnection();
			// handle( conn );
			// handlePart( conn );
			emptycaseImport( 581,conn );
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( conn );
		}
	}
	
	
	
	
	private static void emptycaseImport( int execId, Connection conn ) throws IOException {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( "select id from stat_testcases where caseid=? and product=?" );
			ps2 = conn.prepareStatement( "select count(1) from stat_results where execId=? and caseId=?" );
			ps3 = conn.prepareStatement( "insert into stat_results(caseId,result,execId,duration) values(?,?,?,0)" );
			File file = new File( "C:\\Temp\\results\\granite\\framework\\test_results\\njunit\\Granite_njunit.xml" );
			FileReader fr = new FileReader( file );
			StringWriter sw = new StringWriter();
			IOUtils.copy( fr, sw );
			fr.close();
			TestExecution te = TestParser.parseTestExecution( "orionSS", sw.toString() );
			for(TestResult tr:te.getResults()) {
				
			}
			/*FileReader fr = new FileReader( file );
			BufferedReader br = new BufferedReader( fr );
			String line = null;
			while ( ( line = br.readLine() ) != null ) {
				if ( line.indexOf( "<testcase" ) >= 0 ) {
					String caseId = line.substring( line.indexOf( "(" ) + 1, line.indexOf( ")" ) );
					int cid = getCaseId( caseId, "orionDS", ps );
					if(cid==0) {
						System.out.println(caseId+" invalid");
						continue;
					}
					ps2.setInt( 1, execId );
					ps2.setInt( 2, cid );
					rs = ps2.executeQuery();
					rs.next();
					int cnt = rs.getInt( 1 );
					if(cnt==0) {
						ps3.setInt( 1, cid );
						ps3.setString( 2, TestResult.NORESULT );
						ps3.setInt( 3, execId );
						ps3.executeUpdate();
						ps3.clearParameters();
					}
					continue;
				}
			}
			fr.close();*/
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps3 );
			CommonUtils.closeQuitely( ps );
		}
	}

	private static int getCaseId( String caseId, String product, PreparedStatement ps ) {
		ResultSet rs = null;
		try {
			ps.setString( 1, caseId );
			ps.setString( 2, product );
			rs = ps.executeQuery();
			if ( rs.next() )
				return rs.getInt( 1 );
			else
				return 0;
		} catch ( Exception e ) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				ps.clearParameters();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}
			CommonUtils.closeQuitely( rs );
		}
	}
	
	private static void handlePart( Connection conn ) throws SQLException {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			ps = conn.prepareStatement( "select id from stat_testcases where caseid=? and product=?" );
			ps2 = conn.prepareStatement( "insert into STAT_RESULTS(caseId,result,execId,duration,subid) values(?,?,?,?,?)" );
			TestExecution te = new TestExecution();
			int execId = 569;
			te.setId( execId );
			te.setExecTime( new Timestamp( System.currentTimeMillis() ) );
			te.setSw( "NG1.3rel-0.1319.0" );
			te.setName( "Weekly-regression-orionDS~6" );
			te.setUrl( "" );
			te.setProduct( "orionDS" );
			te.setTestsetId( 24 );
			te.setType( "weekly" );
			File folder = new File( "C:\\Temp\\test_results\\xml" );
			for ( File file : folder.listFiles() ) {
				String caseName = null;
				TestResult tr = new TestResult();
				// tr.setExecId( 325 );
				if ( file.isDirectory() ) {
					caseName = file.getName();
					tr.setResult( TestResult.NORESULT );
				} else if ( file.isFile() ) {
					caseName = file.getName();
					parseResult( file, tr );
				} else {
					System.out.println( "Filename invalid:" + file.getAbsolutePath() );
					continue;
				}
				if ( tr.getResult() == null )
					tr.setResult( TestResult.NORESULT );
				String caseId = caseName.substring( caseName.indexOf( "(" ) + 1, caseName.indexOf( ")" ) );
				// int qcId = CommonUtils.parseInt( caseId.substring( caseId.indexOf( "-" ) + 1 ).trim(), 0 );
				int cid = getCaseId( caseId, te.getProduct(), ps );
				if ( cid == 0 ) {
					System.out.println( caseId + " invalid case." );
					continue;
				}
				tr.setCaseId( cid );
				ps2.setInt( 1, tr.getCaseId() );
				ps2.setString( 2, tr.getResult() );
				ps2.setInt(3,execId);
				ps2.setInt(4, 0 );
				ps2.setInt(5, 1);
				ps2.executeUpdate();
				ps2.clearParameters();
				//te.addResult( tr );
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2);
		}
	}

	private static void handle( Connection conn ) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( "select id from stat_testcases where caseid=? and product=?" );
			TestExecution te = new TestExecution();
			te.setId( 569 );
			te.setExecTime( new Timestamp( System.currentTimeMillis() ) );
			te.setSw( "NG1.3rel-0.1319.0" );
			te.setName( "Weekly-regression-orionDS~6" );
			te.setUrl( "" );
			te.setProduct( "orionDS" );
			te.setTestsetId( 24 );
			te.setType( "weekly" );
			File folder = new File( "C:\\Temp\\test_results\\xml" );
			for ( File file : folder.listFiles() ) {
				String caseName = null;
				TestResult tr = new TestResult();
				// tr.setExecId( 325 );
				if ( file.isDirectory() ) {
					caseName = file.getName();
					tr.setResult( TestResult.NORESULT );
				} else if ( file.isFile() ) {
					caseName = file.getName();
					parseResult( file, tr );
				} else {
					System.out.println( "Filename invalid:" + file.getAbsolutePath() );
					continue;
				}
				if ( tr.getResult() == null )
					tr.setResult( TestResult.NORESULT );
				String caseId = caseName.substring( caseName.indexOf( "(" ) + 1, caseName.indexOf( ")" ) );
				// int qcId = CommonUtils.parseInt( caseId.substring( caseId.indexOf( "-" ) + 1 ).trim(), 0 );
				int cid = getCaseId( caseId, te.getProduct(), ps );
				if ( cid == 0 ) {
					System.out.println( caseId + " invalid case." );
					continue;
				}
				tr.setCaseId( cid );
				te.addResult( tr );
			}
			StatisticManager.addExecution( te );
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( ps );
		}
	}

	private static void parseResult( File file, TestResult tr ) throws IOException {
		FileReader fr = new FileReader( file );
		BufferedReader br = new BufferedReader( fr );
		String line = null;
		while ( ( line = br.readLine() ) != null ) {
			if ( line.indexOf( "<testcase" ) >= 0 ) {
				String result = line.substring( line.indexOf( "result=" ) + 8, line.indexOf( "\" id=" ) );
				if ( result.equals( "Failed" ) ) {
					tr.setResult( TestResult.FAIL );
				} else {
					tr.setResult( TestResult.PASS );
				}
				break;
			}
		}
		fr.close();
	}
}
