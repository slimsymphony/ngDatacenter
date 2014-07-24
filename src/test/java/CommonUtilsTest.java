import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.TestParser;

public class CommonUtilsTest {

	/**
	 * @param args
	 */
	public static void main( String[] args )throws Exception {
		// testRemoteFetch();
		// tt();
		//testPW();
		//System.out.println(CommonUtils.getEarliestTime("bsta"));
		/*String project = "bsta";
		String start = "11/01/2012";
		String end = "01/05/2013";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Timestamp s = null;
		Timestamp e = null;
		if(start!=null&&!start.trim().isEmpty()){
			s = new Timestamp(sdf.parse(start).getTime());
		}else{
			s = CommonUtils.getEarliestTime(project);
		}
		if(end!=null&&!end.trim().isEmpty()){
			e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
		}else{
			e = new Timestamp(System.currentTimeMillis());
		}
		TreeMap<String,int[]> result = new TreeMap<String,int[]>();
		TreeMap<String,Timestamp[]> map = CommonUtils.parseWeeks(s,e);
		for(Map.Entry<String,Timestamp[]> entry : map.entrySet() ){
			result.put(entry.getKey(),ScvInfoManager.getStatByTime( project, entry.getValue()[0], entry.getValue()[1] ));
		}
		for(Map.Entry<String,int[]> entry : result.entrySet()) {
			System.out.print(entry.getKey()+":\n");
			int[] d = entry.getValue();
			System.out.print("\t Commit added:"+d[0]);
			System.out.print("\t Commit updated:"+d[1]);
			System.out.print("\t Case added:"+d[2]);
			System.out.println("\t Case updated:"+d[3]);
		}*/
		//String s = CommonUtils.getDay3( new Date() );
		//System.out.println(s);
		/*Connection conn = CommonUtils.getConnection();
		PreparedStatement ps = conn.prepareStatement( "update STAT_EXECUTIONS set report=? where id=104" );
		FileReader fr = new FileReader("c:/Granite_njunit.xml");
		ps.setClob( 1, fr );
		ps.executeUpdate();
		fr.close();
		ps.close();
		conn.close();*/
		
	}

	public static void testPW() {
		Timestamp s = CommonUtils.getEarliestTime("bsta");
		Timestamp e = new Timestamp(System.currentTimeMillis());
		TreeMap<String, Timestamp[]> map = CommonUtils.parseWeeks( s, e );
		for(Map.Entry<String, Timestamp[]> entry : map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue()[0]+"~"+entry.getValue()[1]);
		}
		System.out.println("=============================");
		map = CommonUtils.parseWeeks( new Date( 112, 10, 27 ), new Date( 113, 1, 5 ) );
		for(Map.Entry<String, Timestamp[]> entry : map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue()[0]+"~"+entry.getValue()[1]);
		}
		System.out.println("=============================");
		map = CommonUtils.parseWeeks( new Date( 113, 1, 2 ), new Date( 113, 2, 5 ) );
		for(Map.Entry<String, Timestamp[]> entry : map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue()[0]+"~"+entry.getValue()[1]);
		}
	}

	public static void tt() {
		String s = "656767698383737871328473776932766578718565716932657868326765766978686582327378707982776584737978";
		System.out.println( s );
		double d = Double.parseDouble( s );
		System.out.println( String.valueOf( d ) );
		System.out.println( Long.MAX_VALUE );
		String str = "Accessing Time Language and Calendar Information";
		long a = CommonUtils.stringToNum( str );
		System.out.println( a );
		System.out.println( CommonUtils.num2String( a ) );
	}

	public static void testRemoteFetch() {
		byte[] data = CommonUtils
				.fetchRemote( "http://becim010:8007/view/Regression-test/job/Granite-weekly-rfa-beijing-F1/14/artifact/granite/framework/test_results/njunit/Granite_njunit.xml" );
		String content = new String( data );
		// System.out.println(content);
		try {
			System.out.println( TestParser.parseTestExecution( "aquaDs", content ) );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
