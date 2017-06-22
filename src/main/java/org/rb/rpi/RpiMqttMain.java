package org.rb.rpi;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
public class RpiMqttMain {

	/* Class */

	public static void main(String[] args) {
		try {
			RpiMqtt rpiHrw = new RpiMqtt();

			Runtime.getRuntime()
					.addShutdownHook(new Thread() {
						public void run() {
							if (rpiHrw != null) {
								rpiHrw.destroy();
							}
						}
					});

			rpiHrw.init();
			rpiHrw.execute();

		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

}
