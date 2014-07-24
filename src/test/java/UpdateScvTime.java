import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.nokia.granite.analyzer.CommonUtils;


public class UpdateScvTime {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		// TODO Auto-generated method stub
		//275
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			//conn.setAutoCommit( false );
			ps = conn.prepareStatement( "select id,time from SCV_RECORDS where id<275" );
			ps2 = conn.prepareStatement( "update SCV_RECORDS set time=? where id=?" );
			rs = ps.executeQuery();
			while(rs.next()) {
				int id = rs.getInt( "id" );
				System.out.println("id:"+id);
				Timestamp time = rs.getTimestamp( "time" );
				System.out.println("before:"+time);
				time = new Timestamp(time.getTime()- (13L*60L*60L*1000L) );
				System.out.println("after:"+time);
				ps2.setTimestamp( 1, time );
				ps2.setInt( 2, id );
				ps2.executeUpdate();
				ps2.clearParameters();
			}
			//conn.commit();
		}catch(Exception e) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
	}

}
