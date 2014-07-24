import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nokia.granite.analyzer.CommonUtils;

public class AquaSSResultUpdates {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rst = null;
		String sql = "select caseId,execId from STAT_RESULTS where execId in (select id from STAT_EXECUTIONS where product='aquaSS')";
		String sql2 = "update STAT_RESULTS set caseId=(select id from STAT_TESTCASES where caseId=(select caseId from STAT_TESTCASES WHERE id =?) and product='aquaSS') where caseId=? and execId=?";
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			ps2 = conn.prepareStatement( sql2 );
			rst = ps.executeQuery();
			while ( rst.next() ) {
				ps2.setInt( 1, rst.getInt( "caseId" ) );
				ps2.setInt( 2, rst.getInt( "caseId" ) );
				ps2.setInt( 3, rst.getInt( "execId" ) );
				ps2.executeUpdate();
				ps2.clearParameters();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rst );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}

	}

}
