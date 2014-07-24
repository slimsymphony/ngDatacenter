import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;
import com.nokia.granite.analyzer.ScvInfo;


public class TestScvManager {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main( String[] args ) throws Exception {
		/*
		ScvInfo si = new ScvInfo();
		si.setChangeId( "c123456" );
		si.setCommit( "y729gd72ssd" );
		si.setCommitor( "Boush" );
		si.setGerritId( 2341 );
		si.setTime( new Timestamp(System.currentTimeMillis()) );
		si.setType( "Add" );
		si.setVersion( 1 );
		ScvInfoManager.ping( si );
		System.out.println(ScvInfoManager.getById(1));
		 */
		//System.out.println(ScvInfoManager.getByTimeInterval( "bsta","evo_BSTA",null, null ));
		//11/12/2012
		/*Timestamp t = new Timestamp(112, 10, 12,0,0,0,0);
		QueryResult qr = ScvInfoManager.getByTimeIntervalStatic("bsta","evo_BSTA",t, null);
		String str = CommonUtils.toJson( qr );
		System.out.println(str);
		QueryResult qr2 = CommonUtils.fromJson( str, QueryResult.class );
		System.out.println( CommonUtils.toJson( qr2 ).equals( str ) );
		System.out.println( "Total Commits :" +qr.getScvInfos().size() );
		
		
		System.out.println( " Added New Case:" +qr.getCases().get( "Add" ).size() );
		for(String caseId: qr.getCases().get( "Add" ).values()) {
			System.out.println( " Add:" + caseId );
		}
		System.out.println( " Updated Old Case:" +qr.getCases().get( "Update" ).size() );
		for(String caseId: qr.getCases().get( "Update" ).values()) {
			System.out.println( " Update:" + caseId );
		}*/
		
		String jsonStr = "{\"type\":\"Update\",\"commit\":\"723d7642c15a11f2b049c06b42bfd8acd6464859\",\"changeId\":\"I34675b09db8bb22b33a869da463c1d4f4ef5f271\",\"commitor\":\"kevin.4.li@nokia.com\",\"gerritId\":2,\"impactScriptCnt\":1,\"branch\":\"evo_BSTA\",\"subject\":\"update: change the title back from 'Phone contacts' to 'All contacts'\",\"refspec\":\"refs/changes/82/6182/2\",\"project\":\"bsta\",\"url\":\"https://gerrit04.nokia.com/6182\",\"cases\":[\"(AUID1030700101-10307) - Phonebook - Create contact to phone memory in Phonebook\"]}";
		ScvInfo scvinfo = CommonUtils.fromJson( jsonStr, ScvInfo.class );
		scvinfo.refreshQcIds();
		for(String str:scvinfo.getCases()) {
			System.out.println(str);
		}
		System.out.println(scvinfo);
		
//		Timestamp start = new Timestamp(112,10,1,0,0,0,0);
//		Timestamp end = new Timestamp(112,11,7,0,0,0,0);;
//		Map<String, int[]> map = ScvInfoManager.statByCommitor("bsta","evo_BSTA",null,null);
//		for(Map.Entry<String, int[]> entry : map.entrySet()) {
//			System.out.println(entry.getKey()+":"+entry.getValue()[0]+"~"+entry.getValue()[1]);
//		}
//		int[] dd = ScvInfoManager.getStatByTime( "bsta", start, end );
//		for(int i:dd) {
//			System.out.println(i);
//		}
		//updateCommitStatus();
	}

	private static void updateCommitStatus() throws Exception {
		File file = new File("commits.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update SCV_RECORDS set status='merged' where commit=?";
			ps = conn.prepareStatement( sql );
			while( (line = br.readLine())!=null ) {
				if(line!=null) {
					line = line.trim();
					if(line.startsWith( "commit " )) {
						line = line.substring( 7 );
						ps.setString( 1, line );
						int ret = ps.executeUpdate();
						System.out.println( "commit:"+line+"----"+ret );
						ps.clearParameters();
					}
				}
			}
		}catch(Exception e) {
			System.err.println( "updateStatus failed.commit="+line );
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

}
