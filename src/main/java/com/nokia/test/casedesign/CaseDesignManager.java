package com.nokia.test.casedesign;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LDAPAuthenticator;
import com.nokia.granite.analyzer.LogUtils;
import com.nokia.test.qc.QcHelper;

public class CaseDesignManager {
	private static Map<String,Integer> columnTypes = new HashMap<String,Integer>();
	static{
		columnTypes.put("designer",java.sql.Types.VARCHAR);
		columnTypes.put("reviewer",java.sql.Types.VARCHAR);
		columnTypes.put("approver",java.sql.Types.VARCHAR);
		columnTypes.put("isreviewed",java.sql.Types.INTEGER);
		columnTypes.put("isapproved",java.sql.Types.INTEGER);
		columnTypes.put("issynchronized",java.sql.Types.INTEGER);
		columnTypes.put("featureGroup",java.sql.Types.VARCHAR);
		columnTypes.put("feature",java.sql.Types.VARCHAR);
		columnTypes.put("validFor",java.sql.Types.VARCHAR);
		columnTypes.put("automationState",java.sql.Types.INTEGER);
		columnTypes.put("name",java.sql.Types.VARCHAR);
		columnTypes.put("content",java.sql.Types.CLOB);
		columnTypes.put("condition",java.sql.Types.VARCHAR);
		columnTypes.put("srtlevel",java.sql.Types.VARCHAR);
		columnTypes.put("type",java.sql.Types.VARCHAR);
		
	};
	
	public static List<String[]> getUsers(){
		List<String[]> users = new ArrayList<String[]>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select noe,name from des_users order by noe" );
			rs = ps.executeQuery();
			while(rs.next()) {
				String[] arr = new String[] {rs.getString(1),rs.getString(2)};
				users.add(arr);
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get User infos failed.", e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return users;
	}
	
	public static void checkExist( LDAPAuthenticator la ) {
		if(la == null)
			throw new NullPointerException("Provided auth info is Null.");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select count(*) from des_users where noe=?" );
			ps.setString( 1, la.getNoe() );
			rs = ps.executeQuery();
			rs.next();
			int cnt = rs.getInt( 1 );
			CommonUtils.closeQuitely( rs );
			if(cnt == 0) {
				CommonUtils.closeQuitely( ps );
				ps = conn.prepareStatement( "insert into des_users(noe,email,name) values(?,?,?)" );
				ps.setString( 1, la.getNoe() );
				ps.setString( 2, la.getMail() );
				ps.setString( 3, la.getFullName() );
				ps.executeUpdate();
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "check USer Exists failed, user:" + la.toString(), e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	public static List<TextCase> query( LinkedHashMap<String,String> condition ){
		List<TextCase> cases = new ArrayList<TextCase>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			StringBuffer sql = new StringBuffer(100);
			sql.append( "select * from des_testcases where " );
			for(Map.Entry<String,String> entry : condition.entrySet()) {
				if( !sql.toString().trim().endsWith( "where" ))
					sql.append( " and " );
				if(entry.getKey().equals("name") || entry.getKey().equals("validFor") || entry.getKey().equals("content")) {
					sql.append( entry.getKey() ).append( " like ?" );
				} else if(entry.getKey().equals( "condition" )){
					sql.append( "'" ).append( entry.getKey() ).append( "'" ).append( "=?" );
				} else
					sql.append( entry.getKey() ).append( "=?" );
			}
			if(sql.toString().endsWith( "where " )) {
				sql.append( " 1=1" );
			}
			LogUtils.getDesignLog().info( sql.toString() );
			ps = conn.prepareStatement( sql.toString() );
			int i=1;
			for(Map.Entry<String,String> entry : condition.entrySet()) {
				if( columnTypes.get( entry.getKey() )!=null) {
					int type = columnTypes.get( entry.getKey() );
					switch(type) {
						case java.sql.Types.VARCHAR:
							if(entry.getKey().equals("name") || entry.getKey().equals("validFor"))
								ps.setString( i++, "%"+entry.getValue().trim()+"%" );
							else
								ps.setString( i++, entry.getValue() );
							break;
						case java.sql.Types.INTEGER:
							ps.setInt( i++, CommonUtils.parseInt( entry.getValue(), 0 ) );
							break;
						case java.sql.Types.CLOB:
							ps.setClob( i++, new StringReader("%"+entry.getValue().trim()+"%") );
							break;
						default:
							ps.setString( i++, entry.getValue() );
					}
				}
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				cases.add(extractTextCase(rs));
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "query text cases failed. condition=" + CommonUtils.toJson( condition ), e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return cases;
	}
	
	public static Map<Integer,String> getSimpleDict(String attribute){
		Map<Integer,String> dict = new HashMap<Integer,String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select id, name from stat_"+attribute+"s";
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while(rs.next()) {
				dict.put( rs.getInt(1), rs.getString( 2 ) );
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get Simple Dict failed.attribute=" + attribute, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return dict;
	}
	
	public static List<String> getSimpleDict(String attribute, String condition){
		List<String> dict = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select name from stat_"+attribute+"s where "+condition;
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while(rs.next()) {
				dict.add( rs.getString( 1 ) );
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get Simple Dict failed.attribute=" + attribute, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return dict;
	}
	
	public static boolean createTextCase( TextCase tc ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "insert into des_testcases(name,content,isreviewed,reviewer,issynchronized,feature,featureGroup,testArea,validFor,designer,traceable,subject,approver,automationOwner,testType,automationState,comments,isApproved,errorId,srtlevel,des_testcases.condition,type)" +
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement( sql );
			injectTextCase(ps,tc);
			ps.executeUpdate();
			tc.setId( CommonUtils.getNextId( conn, "des_testcases" ) );
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Create Text Case failed.tc="+tc, e );
			return false;
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return true;
	}
	
	public static TextCase getTextCaseById(int id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from des_testcases where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if(rs.next()) {
				return extractTextCase( rs );
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get Text Case By Id failed.id=" + id, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return null;
	}
	
	public static TextCase getTextCaseById(int id, boolean includeTrash) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from des_testcases where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if(rs.next()) {
				return extractTextCase( rs );
			}else {
				if(includeTrash) {
					CommonUtils.closeQuitely( rs );
					CommonUtils.closeQuitely( ps );
					ps = conn.prepareStatement( "select * from des_testcases_trash where id=?" );
					ps.setInt( 1, id );
					rs = ps.executeQuery();
					if(rs.next()) {
						return extractTextCase( rs );
					}
				}
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get Text Case By Id failed.id=" + id, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return null;
	}

	private static TextCase extractTextCase( ResultSet rs ) throws Exception {
		TextCase tc = new TextCase();
		tc.setId( rs.getInt( "id" ) );
		tc.setName( rs.getString( "name" ) );
		InputStream in = null;
		try {
			in = rs.getAsciiStream( "content" );
			StringWriter sw = new StringWriter();
			IOUtils.copy( in, sw, "UTF-8" );
			tc.setContent( sw.toString() );
		}finally {
			CommonUtils.closeQuitely( in );
		}
		tc.setIsReviewed(rs.getInt( "isreviewed" ));
		tc.setReviewer( rs.getString( "reviewer" ) );
		tc.setIsSynchronized( rs.getInt( "issynchronized" ) );
		tc.setFeature( rs.getString("feature") );
		tc.setFeatureGroup( rs.getString( "featureGroup" ) );
		tc.setTestArea( rs.getString( "testArea" ) );
		tc.setValidFor( rs.getString( "validFor" ) );
		tc.setDesigner( rs.getString( "designer" ) );
		tc.setTraceable( rs.getInt( "traceable" ) );
		tc.setSubject( rs.getString( "subject" ) );
		tc.setIsApproved( rs.getInt("isApproved") );
		tc.setApprover( rs.getString("approver") );
		tc.setAutomationOwner( rs.getString( "automationOwner" ) );
		tc.setTestType( rs.getString( "testType" ) );
		tc.setAutomationState( rs.getString( "automationState" ) );
		tc.setComments( rs.getString("comments"));
		tc.setErrorId( rs.getInt( "errorId" ) );
		tc.setSrtLevel( rs.getString("srtLevel") );
		tc.setCondition( rs.getString("condition"));
		tc.setType( rs.getString("type") );
		tc.setQcId( rs.getInt( "qcId" ) );
		return tc;
	}

	public static boolean reviewTextCase( int tcId, String reviewer, int reviewResult, String comments ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update des_testcases set reviewer=?, isreviewed=?";
			if( comments != null && !comments.trim().isEmpty() ) {
				sql += ",comments=concat(comments,?) ";
			}
			sql += " where id=?";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, reviewer );
			ps.setInt( 2, reviewResult );
			if( comments != null && !comments.trim().isEmpty() ) {
				ps.setString( 3, "["+reviewer+"]:"+comments+"\n" );
				ps.setInt( 4, tcId );
			}else{
				ps.setInt( 3, tcId );
			}
			ps.executeUpdate();
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Review Text Case failed.id="+tcId+",reviewer="+reviewer+",reviewResult="+reviewResult+",comments="+comments, e );
			return false;
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return true;
	}
	
	public static boolean approveTextCase( int tcId, int isApproved, String approver, String comments ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update des_testcases set approver=?, isApproved=?";
			if( comments != null && !comments.trim().isEmpty() ) {
				sql += " comments=comments+? ";
			}
			sql += " where id=?";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, approver );
			ps.setInt( 2, isApproved );
			if( comments != null && !comments.trim().isEmpty() ) {
				ps.setString( 3, "["+approver+"]:"+comments+"\n" );
				ps.setInt( 4, tcId );
			}else{
				ps.setInt( 3, tcId );
			}
			ps.executeUpdate();
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Approve Text Case failed.id="+tcId+",isApproved="+isApproved+",approver="+approver+",comments="+comments, e );
			return false;
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return true;
	}
	
	public static boolean deleteTextCase( int caseId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			conn = CommonUtils.getConnection();
			conn.setAutoCommit( false );
			String sql = "insert into des_testcases_trash select * from des_testcases where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, caseId );
			sql = "delete from des_testcases where id=?";
			ps2 = conn.prepareStatement( sql );
			ps2.setInt( 1, caseId );
			ps.executeUpdate();
			ps2.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			CommonUtils.rollback( conn );
			LogUtils.getDesignLog().error( "Delete Text Case failed.id="+caseId, e );
			return false;
		}finally {
			try {
				conn.setAutoCommit( false );
			} catch ( SQLException e ) {
				LogUtils.getDbLog().error( "Set Autocommit failed.", e );
			}
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
		return true;
	}
	
	public static boolean updateTextCase( TextCase tc ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update des_testcases set name=?,content=?,feature=?,featureGroup=?,testArea=?,validFor=?,traceable=?,subject=?,automationOwner=?,testType=?,automationState=?,comments=?,errorId=?,srtlevel=?,des_testcases.condition=?,type=?,qcId=? where id=?";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, tc.getName() );
			if( tc.getContent() == null )
				tc.setContent( "" );
			ps.setAsciiStream( 2, new ByteArrayInputStream( tc.getContent().getBytes() ) );
			ps.setString( 3, tc.getFeature() );
			ps.setString( 4, tc.getFeatureGroup() );
			ps.setString( 5, tc.getTestArea() );
			ps.setString( 6, tc.getValidFor() );
			ps.setInt( 7, tc.getTraceable() );
			ps.setString( 8, tc.getSubject() );
			ps.setString( 9, tc.getAutomationOwner() );
			ps.setString( 10, tc.getTestType() );
			ps.setString( 11, tc.getAutomationState() );
			ps.setString( 12, tc.getComments() );
			ps.setInt(13, tc.getErrorId());
			ps.setString( 14, tc.getSrtLevel() );
			ps.setString( 15, tc.getCondition() );
			ps.setString( 16, tc.getType() );
			ps.setInt( 17, tc.getQcId() );
			ps.setInt( 18, tc.getId() );
			ps.executeUpdate();
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Update Text Case failed.tc="+tc, e );
			return false;
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return true;
	}
	
	public static boolean syncToQc( int id, String user, String password ) {
		TextCase tc = getTextCaseById( id );
		QcHelper qc = new QcHelper( user, password, "MASTER", "S40_MASTER", "");
		int qcId = qc.createTestcase( tc );
		if( qcId > 0 )
			return updateTextCase(tc);
		return false;
	}
	
	private static void injectTextCase( PreparedStatement ps, TextCase tc ) throws SQLException {
		ps.setString( 1, tc.getName() );
		if( tc.getContent() == null )
			tc.setContent( "" );
		ps.setAsciiStream( 2, new ByteArrayInputStream( tc.getContent().getBytes() ) );
		ps.setInt( 3, tc.getIsReviewed() );
		ps.setString( 4, tc.getReviewer() );
		ps.setInt( 5, tc.getIsSynchronized() );
		ps.setString( 6, tc.getFeature() );
		ps.setString( 7, tc.getFeatureGroup() );
		ps.setString( 8, tc.getTestArea() );
		ps.setString( 9, tc.getValidFor() );
		ps.setString( 10, tc.getDesigner() );
		ps.setInt( 11, tc.getTraceable() );
		ps.setString( 12, tc.getSubject() );
		ps.setString( 13, tc.getApprover() );
		ps.setString( 14, tc.getAutomationOwner() );
		ps.setString( 15, tc.getTestType() );
		ps.setString( 16, tc.getAutomationState() );
		ps.setString( 17, tc.getComments() );
		ps.setInt(18,tc.getIsApproved());
		ps.setInt(19,tc.getErrorId());
		ps.setString( 20, tc.getSrtLevel() );
		ps.setString( 21, tc.getCondition() );
		ps.setString( 22, tc.getType() );
	}
	
	public static void addOperationRecord( OperationRecord opr ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "insert into des_operation_records(user,operation_type,extension,rel_caseid) values(?,?,?,?)";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, opr.getUser() );
			ps.setString( 2, opr.getOperation_type().name());
			ps.setString( 3, opr.getExtension() );
			ps.setInt( 4, opr.getRel_caseId() );
			ps.executeUpdate();
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Add operation record failed. opr="+opr, e );
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	public static List<OperationRecord> listOperationRecordsByTime( Timestamp start, Timestamp end ) {
		List<OperationRecord> oprs = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from des_operation_records where operation_time between ? and ? order by operation_time desc";
			ps = conn.prepareStatement( sql );
			if ( start == null )
				start = new Timestamp( System.currentTimeMillis() - 10000000000L );
			if ( end == null )
				end = new Timestamp( System.currentTimeMillis() + 1000000L );
			
			ps.setTimestamp( 1, start );
			ps.setTimestamp( 2, end );
			rs = ps.executeQuery();
			while(rs.next()) {
				oprs.add(extractOP(rs));
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Fetch operation records by Time failed. start="+start+",end="+end, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return oprs;
	}
	
	public static List<OperationRecord> getOperationRecordsByCase( int caseId ) {
		List<OperationRecord> oprs = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from des_operation_records where rel_caseId=? order by operation_time desc";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, caseId );
			rs = ps.executeQuery();
			while(rs.next()) {
				oprs.add(extractOP(rs));
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Fetch operation records by case failed. caseId="+caseId, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return oprs;
	}
	
	public static List<OperationRecord> getOperationRecordsByUser( String user ) {
		List<OperationRecord> oprs = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from des_operation_records where user=? order by operation_time desc";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, user );
			rs = ps.executeQuery();
			while(rs.next()) {
				oprs.add(extractOP(rs));
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Fetch operation records by User failed. user="+user, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return oprs;
	}

	private static OperationRecord extractOP( ResultSet rs ) throws SQLException {
		OperationRecord op = new OperationRecord();
		op.setUser( rs.getString("user") );
		op.setOperation_time( rs.getTimestamp( "operation_time" ) );
		op.setOperation_type( OperationRecord.Operation.parse(rs.getString( "operation_type" )) );
		op.setExtension( rs.getString("extension") );
		op.setRel_caseId( rs.getInt( "rel_caseId" ) );
		return op;
	}
}
