package org.rb.rpi.driver;

import org.rb.rpi.domain.Facet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import jodd.petite.meta.PetiteDestroyMethod;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
public abstract class GpioDigitalIn extends Facet implements
		GpioPinListenerDigital {

	/* Class */

	private static final long serialVersionUID = -9087391793202101629L;
	private static final Logger LOG = LoggerFactory.getLogger(
			GpioDigitalIn.class);

	/* Inject */

	@PetiteInject
	private transient Pi4jService pi4jService = null;

	/* Instance */

	private transient GpioPinDigitalInput gpio = null;
	private Integer pin = null;

	/**
	 * Default
	 */
	public GpioDigitalIn() {
		super();
	}

	/**
	 * 
	 * @param o
	 */
	public GpioDigitalIn(Facet o) {
		super(o);
	}

	/**
	 * 
	 * @param id
	 */
	public GpioDigitalIn(Long id) {
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
		this.gpio = this.pi4jService.provisionDigitalInputPin(this.pin,
				this.getClass()
						.getSimpleName());
		this.gpio.addListener(this);
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
	public void handleGpioPinDigitalStateChangeEvent(
			GpioPinDigitalStateChangeEvent event) {
		LOG.debug("GPIO event: event = {}; state = {}; ",
				event,
				event.getState());

		String valueOld = this.value;
		String valueNew = this.value;
		
		if (event.getState().isHigh()) {
			LOG.debug("PIN is HIGH. ");
			valueNew = this.valueHigh();
		} else {
			LOG.debug("PIN is LOW. ");
			valueNew = this.valueLow();
		}
		
		if ((valueNew != null && valueOld != null && !valueNew.equals(valueOld))
				|| (valueOld == null)) {			
			LOG.debug("Fire event to FacetValueListeners");
			this.setValue(valueNew);
			this.fireValueListeners(valueOld);
		}
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

	public GpioPinDigitalInput getGpio() {
		return gpio;
	}

	public void setGpio(GpioPinDigitalInput gpio) {
		this.gpio = gpio;
	}

	/* ***** Override ***** */

	@Override
	public String toString() {
		return "GpioDigitalIn [pin=" + pin + ", toString()=" + super.toString()
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
		GpioDigitalIn other = (GpioDigitalIn) obj;
		if (pin == null) {
			if (other.pin != null)
				return false;
		} else if (!pin.equals(other.pin))
			return false;
		return true;
	}

}
