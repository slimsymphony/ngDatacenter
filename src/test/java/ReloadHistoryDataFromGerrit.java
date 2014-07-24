import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;

import com.nokia.granite.analyzer.CommonUtils;


public class ReloadHistoryDataFromGerrit {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			//conn.setAutoCommit( false );
			ps = conn.prepareStatement( "select id,commit from SCV_RECORDS where result is null" );
			ps2 = conn.prepareStatement( "update SCV_RECORDS set result=? where id=?" );
			rs = ps.executeQuery();
			while(rs.next()) {
				int id = rs.getInt( "id" );
				String commit = rs.getString( "commit" );
				String result = getResult(commit);
				System.out.println("result:"+result);
				ps2.setString( 1, result );
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

	private static String getResult( String commit ) throws IOException, InterruptedException {
		String result = "UNKNOWN";
		String[] arr = new String[] {"ssh", "-p", "29418", "gerrit04.nokia.com", "gerrit query --format TEXT project:bsta commit:"+commit};
		ProcessBuilder pb = new ProcessBuilder(arr);
		Process proc = pb.start();
		InputStream in  = proc.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy( in, baos );
		in.close();
		int ret = proc.waitFor();
		if(ret == 0) {
			String output = baos.toString( "UTF-8" );
			String tmp = output.substring( output.indexOf( "status:" )+7, output.indexOf( "type:" ) ).trim();
			if(tmp.equals( "MERGED" ))
				result = "SUCCESS";
			else 
				result = "UNSTABLE";
		}
		return result;
	}

}
