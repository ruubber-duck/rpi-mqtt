package org.rb.rpi.model;

import org.rb.rpi.domain.Facet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.petite.meta.PetiteBean;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
@PetiteBean
public class WaterPumpMsg extends Facet {

	/* Class */

	private static final long serialVersionUID = -6586631413936041681L;
	private static final Logger LOG = LoggerFactory.getLogger(
			WaterPumpMsg.class);

	/**
	 * Default
	 */
	public WaterPumpMsg() {
		super();
	}

	/**
	 * 
	 * @param o
	 */
	public WaterPumpMsg(WaterPumpMsg o) {
		super(o);
	}

	/**
	 * 
	 * @param id
	 */
	public WaterPumpMsg(Long id) {
		super(id);
	}

}
