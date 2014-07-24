import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nokia.granite.analyzer.CommonUtils;

public class AppendFailureTestCases {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( "errorTestMethods.txt" ) );
		String line = null;
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select id from stat_testcases where product='aquaDS' and method=?" );
			ps1 = conn.prepareStatement( "select count(1) from stat_results where caseid=? and execId=381" );
			ps2 = conn.prepareStatement( "insert into stat_results(caseId,result,execId,duration,message,detail) values(?,'NORESULT',381,0,? ,'')" );
			while ( ( line = br.readLine() ) != null ) {
				ps.setString( 1, line.trim() );
				rs = ps.executeQuery();
				int cid = 0;
				if(rs.next()) {
					cid = rs.getInt( 1 );
				}
				CommonUtils.closeQuitely( rs );
				ps.clearParameters();
				if(cid==0)
					continue;
				ps1.setInt( 1, cid );
				rs = ps1.executeQuery();
				rs.next();
				int cnt = rs.getInt( 1 );
				if(cnt == 0) {
					ps2.setInt( 1, cid );
					ps2.setString( 2, "Exit: evo-home could not be reached&#xA;Granite will reset the phone in order to recover from the situation!" );
					ps2.executeUpdate();
					ps2.clearParameters();
				}
				ps1.clearParameters();
			}
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
	}

}
