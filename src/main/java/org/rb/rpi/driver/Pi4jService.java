package org.rb.rpi.driver;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteDestroyMethod;
import jodd.petite.meta.PetiteInitMethod;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
@PetiteBean
public class Pi4jService {

	/* Instance */

	private GpioController gpioController = null;

	/**
	 * Default
	 */
	public Pi4jService() {
		super();
	}

	/**
	 * 
	 */
	@PetiteInitMethod
	public void init() {
		this.gpioController = GpioFactory.getInstance();
	}

	/**
	 * 
	 */
	@PetiteDestroyMethod
	public void destroy() {
		if (this.gpioController != null) {
			this.gpioController.shutdown();
		}
	}

	/* ***** Implementation ***** */

	/**
	 * 
	 * @return
	 */
	public GpioController getGpioController() {
		return this.gpioController;
	}

	/**
	 * 
	 * @param pin
	 * @param name
	 * @return
	 */
	public GpioPinDigitalOutput provisionDigitalOutputPin(int pin,
			String name) {
		return this.gpioController.provisionDigitalOutputPin(RaspiPin
				.getPinByAddress(pin),
				name,
				PinState.LOW);
	}

	/**
	 * 
	 * @param pin
	 * @param name
	 * @return
	 */
	public GpioPinDigitalInput provisionDigitalInputPin(int pin, String name) {
		return this.gpioController.provisionDigitalInputPin(RaspiPin
				.getPinByAddress(pin),
				name,
				PinPullResistance.PULL_DOWN);
	}

}
