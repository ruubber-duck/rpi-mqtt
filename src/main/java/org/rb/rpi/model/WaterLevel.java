package org.rb.rpi.model;

import org.rb.rpi.driver.GpioDigitalIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.petite.meta.PetiteBean;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
@PetiteBean
public class WaterLevel extends GpioDigitalIn {

	/* Class */

	private static final long serialVersionUID = -3696817737097776144L;
	private static final Logger LOG = LoggerFactory.getLogger(WaterLevel.class);

	public static final String VALUE_HIGH = "1";
	public static final String VALUE_LOW = "0";

	/**
	 * Default
	 */
	public WaterLevel() {
		super();
	}

	/**
	 * 
	 * @param o
	 */
	public WaterLevel(WaterLevel o) {
		super(o);
	}

	/**
	 * 
	 * @param id
	 */
	public WaterLevel(Long id) {
		super(id);
	}

	/* ***** Implementation ***** */

	@Override
	protected String valueHigh() {
		return VALUE_HIGH;
	}

	@Override
	protected String valueLow() {
		return VALUE_LOW;
	}

}
