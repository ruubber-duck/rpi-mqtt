package org.rb.rpi.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
public interface Domain extends Serializable, Cloneable {

	/**
	 * 
	 * @param o
	 */
	public void merge(AbstractDomain o);

	public Long getId();

	public void setId(Long id);

	public Integer getVersion();

	public void setVersion(Integer version);

	public Date getTm();

	public void setTm(Date tm);

}