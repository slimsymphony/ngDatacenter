import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.DurationSplitHelper;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.Testset;

public class DurationSplitHelperTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		FileReader fr = new FileReader("feature_test.testset");
		StringWriter sw = new StringWriter();
		char[] data = new char[100];
		int read = 0;
		while( -1 != (read = fr.read(data)) ) {
			 sw.write( data, 0, read );
		}
		fr.close();
		String content = sw.toString();
		if ( !content.startsWith( "<!" ) ) {
			content = content.substring( content.indexOf( "<" ) );
		}
		Testset ts = TestParser.parseTestset( "aquaDS", content );
		ts.setName( "feature_test.testset" );
		StatisticManager.fetchCaseIdsForTestset(ts);
		List<Testset> tss = DurationSplitHelper.split( ts, 6 );
		Map<String,InputStream> mps = new HashMap<String,InputStream>();
		for(Testset tts : tss) {
			System.out.println("---"+tts.getTestCaseCount()+"---");
			FileWriter fw = new FileWriter(tts.getName());
			fw.write( tts.toTestset() );
			fw.close();
			mps.put( tts.getName(), new ByteArrayInputStream(tts.toTestset().getBytes()) );
		}
		
		byte[] zipbin = CommonUtils.zipFiles( mps );
		FileOutputStream fs = new FileOutputStream("split.zip");
		fs.write( zipbin );
		fs.close();

	}
}
