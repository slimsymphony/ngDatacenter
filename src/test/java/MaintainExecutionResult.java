import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nokia.granite.analyzer.CommonUtils;


public class MaintainExecutionResult {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select A.execId,A.a,B.a,C.a from " +
					" (select execId,result,count(result) a from STAT_RESULTS group by execId,result having result='PASS') A left join" +
					" (select execId,result,count(result) a from STAT_RESULTS group by execId,result having result='FAIL') B on A.execId=B.execId left join" +
					" (select execId,result,count(result) a from STAT_RESULTS group by execId,result having result='NORESULT') C on A.execId=C.execId" );
			ps2 = conn.prepareStatement( "update STAT_EXECUTIONS set passcnt=?,failcnt=?,noresultcnt=? where id=?" );
			rs = ps.executeQuery();
			while(rs.next()) {
				ps2.setInt(1, rs.getInt( 2 ));
				ps2.setInt(2, rs.getInt( 3 ));
				ps2.setInt(3, rs.getInt( 4 ));
				ps2.setInt(4, rs.getInt( 1 ));
				ps2.executeUpdate();
				ps2.clearParameters();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	
	}

}
