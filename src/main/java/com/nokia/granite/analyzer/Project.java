package com.nokia.granite.analyzer;

import java.util.ArrayList;
import java.util.List;

public class Project {
	private int id;
	private String name;
	private List<Branch> branches = new ArrayList<Branch>();
	private String repo;
	private int invalid;

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public List<Branch> getBranches() {
		return branches;
	}

	public void setBranches( List<Branch> branches ) {
		this.branches = branches;
	}

	public int getInvalid() {
		return invalid;
	}

	public void setInvalid( int invalid ) {
		this.invalid = invalid;
	}

	public String getRepo() {
		return repo;
	}

	public void setRepo( String repo ) {
		this.repo = repo;
	}

	public void addBranch( Branch b ) {
		if ( b != null )
			branches.add( b );
	}

}
