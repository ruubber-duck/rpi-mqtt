package org.rb.rpi.driver;

import org.rb.rpi.domain.Facet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import jodd.petite.meta.PetiteDestroyMethod;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
public abstract class GpioDigitalOut extends Facet {

	/* Class */

	private static final long serialVersionUID = -8285249787287206101L;
	private static final Logger LOG = LoggerFactory.getLogger(
			GpioDigitalOut.class);

	/* Inject */

	@PetiteInject
	private transient Pi4jService pi4jService = null;

	/* Instance */

	private transient GpioPinDigitalOutput gpio = null;
	private Integer pin = null;

	/**
	 * 
	 */
	public GpioDigitalOut() {
		super();
	}

	/**
	 * 
	 * @param o
	 */
	public GpioDigitalOut(GpioDigitalOut o) {
		super(o);
	}

	/**
	 * 
	 * @param id
	 */
	public GpioDigitalOut(Long id) {
		super(id);
	}

	/* ***** Abstract ***** */

	protected abstract String valueHigh();

	protected abstract String valueLow();

	/* ***** Implementation ***** */

	/**
	 * 
	 */
	@PetiteInitMethod
	public void init() {
		this.gpio = this.pi4jService.provisionDigitalOutputPin(this.pin,
				this.getClass()
						.getSimpleName());
	}

	/**
	 * 
	 */
	@PetiteDestroyMethod
	public void destroy() {
	}

	/**
	 * 
	 */
	@Override
	public void setValue(String value) {
		if (value != null && (this.value == null || !value.equals(
				this.value))) {

			if (value.equals(this.valueHigh())) {
				this.gpio.setState(PinState.HIGH);
				LOG.debug("PIN set to HIGH. ");
			} else {
				this.gpio.setState(PinState.LOW);
				LOG.debug("PIN set to LOW. ");
			}
		}
		super.setValue(value);
	}

	/* ***** Get & Set ***** */

	public Pi4jService getPi4jService() {
		return pi4jService;
	}

	public void setPi4jService(Pi4jService pi4jService) {
		this.pi4jService = pi4jService;
	}

	public Integer getPin() {
		return pin;
	}

	public void setPin(Integer pin) {
		this.pin = pin;
	}

	public GpioPinDigitalOutput getGpio() {
		return gpio;
	}

	public void setGpio(GpioPinDigitalOutput gpio) {
		this.gpio = gpio;
	}

	/* ***** Override ***** */

	@Override
	public String toString() {
		return "GpioDigitalOut [pin=" + pin + ", toString()=" + super.toString()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((pin == null) ? 0 : pin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GpioDigitalOut other = (GpioDigitalOut) obj;
		if (pin == null) {
			if (other.pin != null)
				return false;
		} else if (!pin.equals(other.pin))
			return false;
		return true;
	}

}
