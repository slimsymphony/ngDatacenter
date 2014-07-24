package com.nokia.granite.analyzer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ScvInfo {
	
	/**
	 * Result of scv.
	 *
	 * @author Frank Wang
	 * @since Nov 5, 2012
	 */
	public static enum RESULT {
		SUCCESS,
		UNSTABLE,
		FAILURE,
		ABORTED,
		NOT_BUILT,
		UNKNOWN;
		
		public static RESULT parse( String str ) {
			if(str==null||str.isEmpty())
				return UNKNOWN;
			for( RESULT r : RESULT.values() ) {
				if(r.name().equalsIgnoreCase( str )) {
					return r;
				}
			}
			return UNKNOWN;
		}
	}
	
	private int id;
	private String type;
	private Timestamp time;
	private String commit;
	private String changeId;
	private String commitor;
	private int gerritId;
	private int impactCaseCnt;
	private int impactScriptCnt;
	private String branch;
	private String subject;
	private String project;
	private String refspec;
	private String url;
	private RESULT result = RESULT.UNKNOWN;
	private String status;
	
	
	private List<String> cases = new ArrayList<String>();
	
	private List<Integer> qcIds = new ArrayList<Integer>();

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime( Timestamp time ) {
		this.time = time;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit( String commit ) {
		this.commit = commit;
	}

	public String getChangeId() {
		return changeId;
	}

	public void setChangeId( String changeId ) {
		this.changeId = changeId;
	}

	public String getCommitor() {
		return commitor;
	}

	public void setCommitor( String commitor ) {
		this.commitor = commitor;
	}

	public int getGerritId() {
		return gerritId;
	}

	public void setGerritId( int gerritId ) {
		this.gerritId = gerritId;
	}

	public int getImpactCaseCnt() {
		return impactCaseCnt;
	}

	public void setImpactCaseCnt( int impactCaseCnt ) {
		this.impactCaseCnt = impactCaseCnt;
	}

	public int getImpactScriptCnt() {
		return impactScriptCnt;
	}

	public void setImpactScriptCnt( int impactScriptCnt ) {
		this.impactScriptCnt = impactScriptCnt;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch( String branch ) {
		this.branch = branch;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject( String desc ) {
		this.subject = desc;
	}

	public String getProject() {
		return project;
	}

	public void setProject( String project ) {
		this.project = project;
	}

	public String getRefspec() {
		return refspec;
	}

	public void setRefspec( String refspec ) {
		this.refspec = refspec;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus( String status ) {
		this.status = status;
	}

	public void refreshQcIds() {
		qcIds.clear();
		for( String caseId : cases ) {
			if( caseId.indexOf( "(AUID" )>=0&&caseId.indexOf( "-" )>0&&caseId.indexOf( ")" )>0 ) {
				int qcId = CommonUtils.parseInt( caseId.substring( caseId.indexOf( "-" )+1, caseId.indexOf( ")" ) ), 0 );
				if(qcId > 0) {
					if(!qcIds.contains( qcId )) {
						qcIds.add( qcId );
					}
				}
			}
		}
		this.impactCaseCnt = qcIds.size();
	}
	
	public void addCase( String caseId ) {
		if ( caseId != null && !cases.contains( caseId ) ) {
			if( caseId.indexOf( "(AUID" )>=0&&caseId.indexOf( "-" )>0&&caseId.indexOf( ")" )>0 ) {
				int qcId = CommonUtils.parseInt( caseId.substring( caseId.indexOf( "-" )+1, caseId.indexOf( ")" ) ), 0 );
				if(qcId > 0) {
					if(!qcIds.contains( qcId )) {
						qcIds.add( qcId );
					}
				}
			}
			cases.add( caseId );
		}
	}

	public List<String> getCases() {
		return cases;
	}

	public void setCases( List<String> cases ) {
		this.cases = cases;
	}

	public List<Integer> getQcIds() {
		return qcIds;
	}

	public void setQcIds( List<Integer> qcIds ) {
		this.qcIds = qcIds;
	}

	public RESULT getResult() {
		return result;
	}

	public void setResult( RESULT result ) {
		this.result = result;
	}

	public String toString() {
		return CommonUtils.toJson( this );
	}

}
