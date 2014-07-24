import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.BugInfo;


public class ImportBugs {

	public static void main( String[] args ) {
		File file = new File("NG_All_Valid_Bug.csv");
		FileReader fr = null;
		BufferedReader br = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "insert into stat_bugs(id,featureGroup,feature,subFeature,pkgversion,status,resolution,priority,a360expteam,summary,groupId,affectProduct,founder,foundphase,hardware,keywords,rpn,rpnDetection,rpnOccurrence,rpnServerity,serverity,targetMilestone,testEnv,url,errorCategory,interactionLevel,interruptAction,needTrace,relCaseID,relCaseName) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = null;
			while( (line=br.readLine()) != null ) {
				BugInfo bi = new BugInfo();
				parseBugInfo(line,bi);
				insert(ps, bi);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	
	private static void parseBugInfo( String line, BugInfo bi ) {
		String[] arr = line.split( "," );
		//id,featureGroup,feature,subFeature,pkgversion,status,resolution,priority,a360expteam,
		//summary,groupId,affectProduct,founder,foundphase,hardware,keywords,
		//rpn,rpnDetection,rpnOccurrence,rpnServerity,serverity,targetMilestone,
		//url,errorCategory,interactionLevel,interruptAction,needTrace,relCaseID,relCaseName
		bi.setId(CommonUtils.parseInt( arr[0], 0 ));
		bi.setFeatureGroup( arr[1] );
		bi.setPkgversion( arr[2] );
		bi.setFeature( arr[3] );
		bi.setSubFeature( arr[4] );
		bi.setStatus( arr[5] );
		bi.setResolution( arr[6] );
		bi.setPriority( arr[7] );
		bi.setA360expteam( arr[8] );
		bi.setSummary( arr[9] );
		//bi.setGroupId( arr[10] );
		bi.setAffectProduct( arr[11] );
		bi.setFounder( arr[12] );
		bi.setFoundphase( arr[13] );
		bi.setHardware( arr[14] );
		bi.setKeywords( arr[15] );
		/*bi.setRpn( arr[16] );
		bi.setRpnDetection( arr[17] );
		bi.setRpnOccurrence( arr[18] );
		bi.setRpnServerity( arr[19] );
		bi.setServerity( arr[20] );*/
		bi.setTargetMilestone( arr[21] );
		//bi.setTestEnv( arr[22] );
		bi.setUrl( arr[23] );
		bi.setErrorCategory( arr[24] );
		//bi.setInteractionLevel( arr[25] );
		bi.setInterruptAction( arr[26] );
		//bi.setNeedTrace( arr[27] );
		//bi.setRelCaseID( arr[28] );
		bi.setRelCaseName( arr[29] );
	}
	
	private static void setBugInfo( PreparedStatement ps, BugInfo bi ) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 */
	public static void insert( PreparedStatement ps, BugInfo bi ) throws Exception {
		try {
			ps.clearParameters();
			setBugInfo(ps, bi);
			ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

}
