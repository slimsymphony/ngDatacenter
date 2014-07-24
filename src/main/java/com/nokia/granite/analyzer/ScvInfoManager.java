package com.nokia.granite.analyzer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.nokia.granite.analyzer.ScvInfo.RESULT;

public class ScvInfoManager {
	private static Logger log = LogUtils.getDbLog();

	public static void updateStatus( String refspec ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "update SCV_RECORDS set status='merged' where refspec=?";
			LogUtils.getDbLog().debug( "[update refspec Status]"+sql );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, refspec );
			ps.executeUpdate();
		}catch(Exception e) {
			log.error( "updateStatus failed.refspec="+refspec, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	public static List<Project> getProjects( boolean allProject, boolean includeBranches ){
		List<Project> projects = new ArrayList<Project>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from SCV_PROJECTS where invalid=0 order by id desc";
			if(allProject)
				sql = "select * from SCV_PROJECTS order by id desc";
			log.debug( "[getProjects]:"+sql );
			ps = conn.prepareStatement( sql );
			if(includeBranches) {
				sql = "select * from SCV_BRANCHES where projectId=?";
				ps2 = conn.prepareStatement( sql );
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				Project p = parseProject(rs,ps2);
				if(p!=null)
					projects.add( p );
			}
		}catch(SQLException e) {
			log.error( "get Projects failed", e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
		return projects;
	}
	
	@SuppressWarnings( "deprecation" )
	public static Map<String,int[]> statByCommitor(String project, String branch, Timestamp start, Timestamp end ){
		Map<String,int[]> results = new LinkedHashMap<String,int[]>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();//count(distinct changeId),
			String sql = "select commitor,type,sum(impactCaseCnt) from SCV_RECORDS where project=? and branch=? and status='merged' and  time between ? and ? group by commitor,type  order by commitor";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getByTimeIntervalStatic]"+sql );
			ps.setString( 1, project );
			ps.setString( 2, branch );
			if ( start == null ) {
				Date d = new Date( 100, 0, 1 );
				start = new Timestamp( d.getTime() );
			}
			ps.setTimestamp( 3, start );
			if ( end == null ) {
				end = new Timestamp( System.currentTimeMillis() + 3600*1000*24);
			}
			ps.setTimestamp( 4, end );
			rs = ps.executeQuery();
			String preCommitor = null;
			int addCnt = 0;
			int updateCnt = 0;
			while ( rs.next() ) {
				String commitor = rs.getString( 1 );
				String type = rs.getString( 2 );
				int caseCnt = rs.getInt( 3 );
				if(preCommitor == null) {
					preCommitor = commitor;
					if(type.equalsIgnoreCase( "Add" )) {
						addCnt = caseCnt;
					}else {
						updateCnt += caseCnt;
					}
				}else if( !commitor.equals( preCommitor )) {
					results.put( preCommitor, new int[] {addCnt,updateCnt} );
					addCnt = 0;
					updateCnt = 0;
					preCommitor = commitor;
					if(type.equalsIgnoreCase( "Add" )) {
						addCnt = caseCnt;
					}else {
						updateCnt += caseCnt;
					}
				}else {
					if(type.equalsIgnoreCase( "Add" )) {
						addCnt = caseCnt;
					}else {
						updateCnt += caseCnt;
					}
				}
			}
			results.put( preCommitor, new int[] {addCnt,updateCnt} );
		} catch ( SQLException e ) {
			log.error( "Get ScvInfo By Commitor failed.start=" + start + ",end=" + end, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return results;
	}
	
	private static Project parseProject( ResultSet rs, PreparedStatement ps ) throws SQLException {
		Project p = new Project();
		p.setId( rs.getInt( "id" ) );
		p.setInvalid( rs.getInt("invalid") );
		p.setName( rs.getString( "name" ) );
		if(ps!=null) {
			ResultSet rst = null;
			try {
				ps.setInt( 1, p.getId() );
				rst = ps.executeQuery();
				while(rst.next()) {
					p.addBranch( parseBranch(rst) );
				}
			}finally {
				CommonUtils.closeQuitely( rst );
			}
		}
		return p;
	}

	private static Branch parseBranch( ResultSet rs ) throws SQLException {
		Branch b = new Branch();
		b.setId( rs.getInt( "id" ) );
		b.setName( rs.getString( "name" ) );
		b.setProjectId( rs.getInt( "projectId" ) );
		return b;
	}

	public synchronized static int getNextId(Connection conn, String tableName) {
		boolean needCloseConn = ( conn != null ? false:true );
		String sql = "SHOW TABLE STATUS LIKE '"+tableName+"'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nextId = -1;
		try {
			if( needCloseConn )
				conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			rs.next();
			nextId = rs.getInt("Auto_increment");
		}catch(SQLException e) {
			log.error( "Get Next Id failed.Tablename="+tableName, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			if( needCloseConn )
				CommonUtils.closeQuitely( conn );
		}
		return nextId-1;
	}
	
	public static void handleRetriger(Connection conn, ScvInfo si) {
		boolean needCloseConn = ( conn != null ? false:true );
		String sql = "insert into SCV_RECORDS_TRASH select * from SCV_RECORDS where changeId=? and id!=?"; //commit=? and 
		String sql2 = "delete from SCV_RECORDS where changeId=? and id!=?"; // commit=? and 
		String sql3 = "insert into SCV_CASE_REL_TRASH select * from SCV_CASE_REL where scvId in (select id from SCV_RECORDS where changeId=? and id!=?)";//commit=? and 
		String sql4 = "delete from SCV_CASE_REL where scvId in (select id from SCV_RECORDS where changeId=? and id!=?)"; //commit=? and 
		LogUtils.getDbLog().debug( "[handleRetriger]"+sql );
		LogUtils.getDbLog().debug( "[handleRetriger 2]"+sql2 );
		LogUtils.getDbLog().debug( "[handleRetriger 3]"+sql3 );
		LogUtils.getDbLog().debug( "[handleRetriger 4]"+sql4 );
		PreparedStatement ps = null;
		PreparedStatement pss = null;
		PreparedStatement ps2 = null;
		PreparedStatement pss2 = null;
		try {
			if( needCloseConn )
				conn = CommonUtils.getConnection();
			ps2 = conn.prepareStatement( sql3 );
			pss2 = conn.prepareStatement( sql4 );
			pss2.setString( 1, si.getChangeId() );
			pss2.setInt( 2, si.getId() );
			ps2.setString( 1, si.getChangeId() );
			ps2.setInt( 2, si.getId() );
			ps2.executeUpdate();
			pss2.executeUpdate();
			
			
			ps = conn.prepareStatement( sql );
			pss = conn.prepareStatement( sql2 );
			ps.setString( 1, si.getChangeId() );
			ps.setInt( 2, si.getId() );
			pss.setString( 1, si.getChangeId() );
			pss.setInt( 2, si.getId() );
			ps.executeUpdate();
			pss.executeUpdate();
		}catch(SQLException e) {
			log.error( "handle Retriger failed.ScvInfo="+si, e );
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( pss );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( pss2 );
			if( needCloseConn )
				CommonUtils.closeQuitely( conn );
		}
	}
	
	public static void setResult( int id, String result ) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "update SCV_RECORDS set result=? where id=?" );
			ps.setString( 1, result );
			ps.setInt( 2, id );
			ps.executeUpdate();
		}catch(SQLException e) {
			log.error( "Handle Result update failed,id="+id+",result="+result, e );
			throw e;
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	public synchronized static void ping( ScvInfo si ) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "insert into SCV_RECORDS(type,commit,changeId,gerritId,commitor,impactScriptCnt,branch,subject,project,refspec,url,result,status,impactCaseCnt) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
			setScvInfo( ps, si );
			ps.executeUpdate();
			int nextId = getNextId( conn , "SCV_RECORDS");
			if( nextId <= 0 )
				throw new SQLException("Fail to retrive recent insert record id.");
			si.setId( nextId );
			handleRetriger(conn, si);
			if(si.getCases()!=null&&si.getCases().size()>0) {
				ps2 = conn.prepareStatement( "insert into SCV_CASE_REL(scvid,caseid) values(?,?)" );
				for(String caseId : si.getCases()) {
					ps2.setInt( 1, si.getId() );
					ps2.setString( 2, caseId );
					ps2.executeUpdate();
					ps2.clearParameters();
				}
			}
		} catch ( SQLException e ) {
			log.error( "Ping ScvInfo failed.", e );
			throw e;
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
	}

	private static PreparedStatement getStatementForScvId( Connection conn ) throws SQLException {
		String sql = "select caseId from SCV_CASE_REL where scvId=?";
		PreparedStatement ps = conn.prepareStatement( sql );
		return ps;
	}
	
	public static ScvInfo getById( int id ) {
		ScvInfo si = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from SCV_RECORDS where id=?";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getById]"+sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				si = extractScv( rs );
				PreparedStatement ps2 = null;
				try {
					ps2 = getStatementForScvId(conn);
					si.setCases(getCasesByScvId( ps2, si.getId()));
				}finally {
					CommonUtils.closeQuitely( ps2 );
				}
			}
		} catch ( SQLException e ) {
			log.error( "Get ScvInfo failed.id=" + id, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return si;
	}
	
	public static List<String> getCasesByScvId( PreparedStatement ps, int scvId ) {
		List<String> cases = new ArrayList<String>();
		String sql = "select caseId from SCV_CASE_REL where scvId=?";
		LogUtils.getDbLog().debug( "[getCasesByScvId]"+sql );
		ResultSet rs = null;
		try {
			if( ps==null || ps.isClosed() ) {
				CommonUtils.closeQuitely( ps );
				throw new SQLException("statment closed.");
			}else {
				ps.clearParameters();
			}
			ps.setInt(1,scvId);
			rs = ps.executeQuery();
			while(rs.next()) {
				cases.add(rs.getString( 1 ));
			}
		}catch(Exception e) {
			log.error( "getCasesByScvId failed, scvId:"+scvId, e );
		}finally {
			CommonUtils.closeQuitely( rs );
		}
		return cases;
	}

	@SuppressWarnings( "deprecation" )
	public static List<ScvInfo> getByTimeInterval(  String project, String branch, Timestamp start, Timestamp end ) {
		List<ScvInfo> ss = new ArrayList<ScvInfo>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from SCV_RECORDS where project=? and branch=? and status='merged' and time between ? and ? order by id";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getByTimeInterval]"+sql );
			ps.setString( 1, project );
			ps.setString( 2, branch );
			if ( start == null ) {
				Date d = new Date( 100, 0, 1 );
				start = new Timestamp( d.getTime() );
			}
			ps.setTimestamp( 3, start );
			if ( end == null ) {
				end = new Timestamp( System.currentTimeMillis() + 3600*1000*24);
			}
			ps.setTimestamp( 4, end );
			rs = ps.executeQuery();
			PreparedStatement ps2 = null;
			try {
				ps2 = getStatementForScvId(conn);
				while ( rs.next() ) {
					ScvInfo si = extractScv( rs );
					if ( si != null ) {
						ss.add( si );
						si.setCases( getCasesByScvId( ps2, si.getId()) );
					}
				}
			} finally {
				CommonUtils.closeQuitely( ps2 );
			}
		} catch ( SQLException e ) {
			log.error( "Get ScvInfo By Time Interval failed.start=" + start + ",end=" + end, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ss;
	}
	
	@SuppressWarnings( "deprecation" )
	public static QueryResult getByTimeIntervalStatic( String project, String branch, Timestamp start, Timestamp end ){
		QueryResult qr = new QueryResult();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from SCV_RECORDS where project=? and branch=? and status='merged' and time between ? and ? order by id";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getByTimeIntervalStatic]"+sql );
			ps.setString( 1, project );
			ps.setString( 2, branch );
			if ( start == null ) {
				Date d = new Date( 100, 0, 1 );
				start = new Timestamp( d.getTime() );
			}
			ps.setTimestamp( 3, start );
			if ( end == null ) {
				end = new Timestamp( System.currentTimeMillis() + 3600*1000*24);
			}
			ps.setTimestamp( 4, end );
			rs = ps.executeQuery();
			PreparedStatement ps2 = null;
			try {
				ps2 = getStatementForScvId(conn);
				while ( rs.next() ) {
					ScvInfo si = extractScv( rs );
					if ( si != null ) {
						List<String> cases = getCasesByScvId( ps2, si.getId() );
						si.setCases( cases );
						qr.addScvInfo( si );
						for(String caseId : cases) {
							qr.addCase( si.getType(), caseId, si.getId() );
						}
					}
				}
			}finally {
				CommonUtils.closeQuitely( ps2 );
			}
		} catch ( SQLException e ) {
			log.error( "Get ScvInfo By Time Interval failed.start=" + start + ",end=" + end, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return qr;
	}
	
	@SuppressWarnings( "deprecation" )
	public static List<ScvInfo> getByTypeAndTimeInterval( String project, String branch, String type, Timestamp start, Timestamp end ) {
		List<ScvInfo> ss = new ArrayList<ScvInfo>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from SCV_RECORDS where type=? time between ? and ?  order by id";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getByTypeAndTimeInterval]"+sql );
			ps.setString( 1, type );
			if ( start == null ) {
				Date d = new Date( 100, 0, 1 );
				start = new Timestamp( d.getTime() );
			}
			ps.setTimestamp( 2, start );
			if ( end == null ) {
				end = new Timestamp( System.currentTimeMillis() + 3600*1000*24);
			}
			ps.setTimestamp( 3, end );
			rs = ps.executeQuery();
			PreparedStatement ps2 = null;
			try {
				ps2 = getStatementForScvId(conn);
				while ( rs.next() ) {
					ScvInfo si = extractScv( rs );
					if ( si != null ) {
						ss.add( si );
						si.setCases( getCasesByScvId( ps2, si.getId()) );
					}
				}
			} finally {
				CommonUtils.closeQuitely( ps2 );
			}
		} catch ( SQLException e ) {
			log.error( "Get ScvInfo By type and Time Interval failed.type="+type+",start=" + start + ",end=" + end, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ss;
	}
	
	public static List<ScvInfo> getByTypeCommitor( String project, String branch, String commitor ) {
		List<ScvInfo> ss = new ArrayList<ScvInfo>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select * from SCV_RECORDS where commitor=?  order by id";
			ps = conn.prepareStatement( sql );
			LogUtils.getDbLog().debug( "[getByTypeCommitor]"+sql );
			ps.setString( 1, commitor );
			rs = ps.executeQuery();
			PreparedStatement ps2 = null;
			try {
				ps2 = getStatementForScvId(conn);
				while ( rs.next() ) {
					ScvInfo si = extractScv( rs );
					if ( si != null ) {
						ss.add( si );
						si.setCases( getCasesByScvId( ps2, si.getId() ) );
					}
				}
			}finally {
				CommonUtils.closeQuitely( ps2 );
			}
		} catch ( SQLException e ) {
			log.error( "Get ScvInfo By commitor failed. commitor=" + commitor, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return ss;
	}

	public static ScvInfo extractScv( ResultSet rs ) throws SQLException {
		ScvInfo si = new ScvInfo();
		si.setId( rs.getInt( "id" ) );
		si.setChangeId( rs.getString( "changeId" ) );
		si.setType( rs.getString( "type" ) );
		si.setTime( rs.getTimestamp( "time" ) );
		si.setCommit( rs.getString( "commit" ) );
		si.setGerritId( rs.getInt( "gerritId" ) );
		si.setCommitor( rs.getString( "commitor" ) );
		si.setImpactCaseCnt( rs.getInt( "impactCaseCnt" ) );
		si.setImpactScriptCnt( rs.getInt( "impactScriptCnt" ) );
		si.setBranch( rs.getString("branch") );
		si.setSubject( rs.getString( "subject" ) );
		si.setProject( rs.getString( "project" ) );
		si.setRefspec( rs.getString("refspec") );
		si.setUrl( rs.getString("url") );
		si.setResult( RESULT.parse(rs.getString( "result" ) ) );
		si.setStatus( rs.getString( "status" ) );
		return si;
	}

	private static void setScvInfo( PreparedStatement ps, ScvInfo si ) throws SQLException {
		ps.setString( 1, si.getType() );
		//ps.setTimestamp( 2, si.getTime() );
		ps.setString( 2, si.getCommit() );
		ps.setString( 3, si.getChangeId() );
		ps.setInt( 4, si.getGerritId() );
		ps.setString( 5, si.getCommitor() );
		ps.setInt( 6, si.getImpactScriptCnt() );
		ps.setString( 7, si.getBranch() );
		ps.setString( 8, si.getSubject() );
		ps.setString( 9, si.getProject() );
		ps.setString( 10, si.getRefspec() );
		ps.setString( 11, si.getUrl() );
		ps.setString( 12, si.getResult().name() );
		ps.setString( 13, si.getStatus() );
		ps.setInt( 14, si.getImpactCaseCnt() );
	}
	
	public static int[] getStatByTime(String project, Timestamp start, Timestamp end ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		int[] data = new int[4];
		try {
			conn = CommonUtils.getConnection();
			String sql = "select type,count(type) from SCV_RECORDS where project=? and status='merged' and time between ? and ? group by type";
			String sql2 = "select type,count(type) from SCV_CASE_REL left join SCV_RECORDS on SCV_CASE_REL.scvId=SCV_RECORDS.id where project=? and status='merged' and time between ? and ? group by type";
			ps = conn.prepareStatement( sql );
			ps2 = conn.prepareStatement( sql2 );
			LogUtils.getDbLog().debug( "[getStatByTime]"+sql );
			LogUtils.getDbLog().debug( "[getStatByTime 2]"+sql2 );
			ps.setString( 1, project );
			ps.setTimestamp( 2, start );
			ps.setTimestamp( 3, end );
			ps2.setString( 1, project );
			ps2.setTimestamp( 2, start );
			ps2.setTimestamp( 3, end );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String type = rs.getString( 1 );
				int cnt = rs.getInt( 2 );
				if(type.equalsIgnoreCase( "Add" )) {
					data[0] = cnt;
				}else {
					data[1] += cnt;
				}
			}
			rs2 = ps2.executeQuery();
			while ( rs2.next() ) {
				String type = rs2.getString( 1 );
				int cnt = rs2.getInt( 2 );
				if(type.equalsIgnoreCase( "Add" )) {
					data[2] = cnt;
				}else {
					data[3] += cnt;
				}
			}
		} catch ( SQLException e ) {
			log.error( "get Stat ByTime failed. project=" + project+",start="+start+", end="+end, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( rs2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
		return data;
	}

}
