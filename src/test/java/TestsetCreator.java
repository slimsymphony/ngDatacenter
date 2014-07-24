import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestCase;
import com.nokia.test.statistic.Testset;


public class TestsetCreator {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception{
		String product = "aquaDS";
		BufferedReader br = new BufferedReader(new FileReader("c:/frt.txt"));
		String line = null;
		Testset ts =  new Testset();
		ts.setName( "frt.testset" );
		ts.setProduct( product );
		int cnt = 0;
		while((line = br.readLine())!=null) {
			int qcid = CommonUtils.parseInt( line.trim(), 0);
			for(TestCase tc : StatisticManager.getTestcasesByQcId(qcid,product)) {
				cnt ++;
				ts.addTestCase( tc.getFeatureGroup(), tc );
			}
		}
		System.out.println(cnt);
		String tscontent = ts.toTestset();
		FileWriter fw = new FileWriter("frt.testset");
		fw.write( tscontent );
		fw.close();
	}

}
