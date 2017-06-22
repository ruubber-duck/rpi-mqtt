package org.rb.rpi.model;

import org.rb.rpi.driver.GpioDigitalOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
@PetiteBean
public class WaterPump extends GpioDigitalOut {

	/* Class */

	private static final long serialVersionUID = 460669400135503364L;
	private static final Logger LOG = LoggerFactory.getLogger(WaterPump.class);

	public static final String VALUE_HIGH = "1";
	public static final String VALUE_LOW = "0";

	/* Inject */

	@PetiteInject
	private WaterLevel waterLevel = null;

	@PetiteInject
	private WaterPumpMsg waterPumpMsg = null;

	/**
	 * Default
	 */
	public WaterPump() {
		super();
	}

	/**
	 * 
	 * @param o
	 */
	public WaterPump(WaterPump o) {
		super(o);
	}

	/**
	 * 
	 * @param id
	 */
	public WaterPump(Long id) {
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

	/* ***** Get & Set ***** */

	public WaterLevel getWaterLevel() {
		return waterLevel;
	}

	public void setWaterLevel(WaterLevel waterLevel) {
		this.waterLevel = waterLevel;
	}

	public WaterPumpMsg getWaterPumpMsg() {
		return waterPumpMsg;
	}

	public void setWaterPumpMsg(WaterPumpMsg waterPumpMsg) {
		this.waterPumpMsg = waterPumpMsg;
	}

}
