package org.rb.rpi.domain;

import java.io.Serializable;
import java.util.Date;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;

/**
 * 
 * @author ruubber.duck@gmail.com
 * 
 */
public class AbstractDomain implements Serializable, Domain {

	/* Class */

	private static final long serialVersionUID = 7266891612247145275L;

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_VERSION = "version";
	public static final String COLUMN_TIMESTAMP = "tm";

	/* Instance */

	@DbId(value = COLUMN_ID)
	protected Long id = null;

	@DbColumn(value = COLUMN_VERSION)
	protected Integer version = null;

	@DbColumn(value = COLUMN_TIMESTAMP)
	protected Date tm = null;

	/**
	 * Default
	 */
	public AbstractDomain() {
		super();
	}

	/**
	 * 
	 * @param id
	 */
	public AbstractDomain(Long id) {
		super();
		this.id = id;
	}

	/**
	 * 
	 * @param o
	 */
	public AbstractDomain(AbstractDomain o) {
		super();
		this.merge(o);
	}

	/* ***** Implementation ***** */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#merge(org.rb.rpi.domain.AbstractDomain)
	 */
	@Override
	public void merge(AbstractDomain o) {
		this.id = o.id;
		this.version = o.version;
		this.tm = o.tm;
	}

	/* ***** Get & Set ***** */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#getVersion()
	 */
	@Override
	public Integer getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#setVersion(java.lang.Integer)
	 */
	@Override
	public void setVersion(Integer version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#getTm()
	 */
	@Override
	public Date getTm() {
		return tm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rb.rpi.domain.Domain#setTm(java.util.Date)
	 */
	@Override
	public void setTm(Date tm) {
		this.tm = tm;
	}

	/* ***** Override ***** */

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new AbstractDomain(this);
	}

	@Override
	public String toString() {
		return "AbstractDomain [id=" + id + ", version=" + version + ", tm="
				+ tm + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((tm == null) ? 0 : tm.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDomain other = (AbstractDomain) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (tm == null) {
			if (other.tm != null)
				return false;
		} else if (!tm.equals(other.tm))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
