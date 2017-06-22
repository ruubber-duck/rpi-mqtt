package org.rb.rpi.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.rb.rpi.domain.Facet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteDestroyMethod;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
@PetiteBean
public class MqttService implements MqttCallback, Facet.FacetValueListener {

	/* Class */

	private static final Logger LOG = LoggerFactory.getLogger(
			MqttService.class);

	/**
	 * 
	 * @author ruubber.duck@gmail.com
	 *
	 */
	public static interface MqttValueListener {
		public boolean flush(Facet command);
	}
	
	/* Inject */
	
	@PetiteInject
	protected Set<Facet> facets = new HashSet<Facet>();

	/* Instance */
	
	protected String brokerHost = null;
	protected Integer brokerPort = null;
	protected Integer brokerPortSecure = null;
	protected Integer wsPort = null;
	protected Integer wsPortSecure = null;
	protected String login = null;
	protected String password = null;
	protected String rootTopic = null;
	protected String clientId = null;

	private Set<MqttValueListener> mqttListeners =
			new HashSet<MqttValueListener>();
	
	private HashMap<String, Facet> subsribersMap = new HashMap<String, Facet>();
	
	private HashMap<String, Date> mqttSession = new HashMap<String, Date>();
	
	private MemoryPersistence mqttPersistence = null;
	private MqttClient mqttClient = null;
	
	/**
	 * Default
	 */
	public MqttService() {
		super();
	}
	
	/* ***** Routines ***** */
	
	private synchronized void addMqttSession(String topic) {
		synchronized (this.mqttSession) {
			this.mqttSession.put(topic, Calendar.getInstance().getTime());
		}
	}
	
	private synchronized void delMqttSession(String topic) {
		synchronized (this.mqttSession) {
			this.mqttSession.remove(topic);
		}
	}
	
	private synchronized boolean isMqttSession(String topic) {
		synchronized (this.mqttSession) {
			return this.mqttSession.containsKey(topic);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private synchronized boolean mqttConnect() {
		String brokerUri = "tcp://" + this.brokerHost + ":" + this.brokerPort;
		
		try {
			this.mqttPersistence = new MemoryPersistence();
			this.mqttClient = new MqttClient(
					brokerUri,
					clientId,
					mqttPersistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setUserName(this.login);
			connOpts.setPassword(this.password.toCharArray());
			
			LOG.info("Connecting to broker: uri = {}; clientID = {}; ", brokerUri, clientId);
			this.mqttClient.connect(connOpts);
			this.mqttClient.setCallback(this);
			
			LOG.info("Connection success: uri = {}; clientID = {}; ", brokerUri, clientId);			
		} catch (MqttException e) {
			LOG.warn("Connection success: uri = {}; clientID = {}; ", brokerUri, clientId);
			return false;
		} 
		
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	private synchronized boolean mqttDisconnect() {
		try {
			if (this.mqttClient != null) {
				this.mqttClient.disconnect();
				this.mqttClient = null;
			}
			
			LOG.info("Disconnecting from broker: mqttClient = {};", this.mqttClient);
		} catch (MqttException e) {
			LOG.warn("Disconnection error: mqttClient = {}; ", this.mqttClient);
			return false;
		} 
		
		try {
			if (this.mqttPersistence != null) {
				this.mqttPersistence.clear();
				this.mqttPersistence.close();
				this.mqttPersistence = null;
			}
			
			LOG.info("Disconnecting from broker: mqttPersistence = {};", this.mqttPersistence);
		} catch (MqttException e) {
			LOG.info("Disconnecting error: mqttPersistence = {};", this.mqttPersistence);
			return false;
		} 
		
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	private synchronized boolean isMqttReady() {
		return (this.mqttClient != null && this.mqttClient.isConnected());
	}
	
	/**
	 * 
	 * @return
	 */
	private synchronized boolean mqttReconnect() {		
		if (this.mqttClient == null) {
			throw new IllegalStateException("Reconnection possible only if client is created already. ");
		}
		
		try {
			this.mqttClient.reconnect();
			LOG.info("Reconnecting to broker: mqttClient = {};", this.mqttClient);
		} catch (MqttException e) {
			LOG.warn("Reconnecting error: mqttClient = {}; ", this.mqttClient);
			return false;
		} 
		
		return true;
	}

	/* ***** Implementation ***** */
	
	@PetiteInitMethod
	public void init() {
		LOG.trace(">> init()");
		
		boolean connect = this.mqttConnect();
		LOG.info("MQTT connection is {}. ", connect );
	
		for (Facet facet : this.facets) {
			LOG.info("Add listener to {}", facet);				
			facet.addValueListener(this);
			
			String topic = this.rootTopic + facet.getSid();						
			this.subsribersMap.put(topic, facet);
			
			try {
				this.mqttClient.subscribe(topic);
				LOG.debug("Subscribe: topic = {}; facet = {}; ", topic, facet);
			} catch (MqttException e) {
				LOG.warn("Subscribe error. ", e);
			}
		}
		
		LOG.trace("<< init()");
	}
	
	@PetiteDestroyMethod
	public void destroy() {
		LOG.trace(">> destroy()");
		
		for (Facet facet : this.facets) {
			LOG.info("Del listener to {}", facet);		
			facet.delValueListener(this);
			
			String topic = this.rootTopic + facet.getSid();
			this.subsribersMap.remove(topic);
			
			try {
				this.mqttClient.unsubscribe(topic);
				LOG.debug("Unsubscribe: topic = {}; facet = {}; ", topic, facet);
			} catch (MqttException e) {
				LOG.warn("Unsubscribe error. ", e);
			}
		}
		
		boolean disconnect = this.mqttDisconnect();
		LOG.info("MQTT disconnect is {}. ", disconnect );
		
		LOG.trace("<< destroy()");
	}

	/**
	 * 
	 * @param listener
	 */
	public synchronized void addValueListener(MqttValueListener listener) {
		if (listener != null) {
			this.mqttListeners.add(listener);
		}
	};

	/**
	 * 
	 * @param listener
	 */
	public synchronized void delValueListener(MqttValueListener listener) {
		if (listener != null) {
			this.mqttListeners.remove(listener);
		}
	};
	
	/* ***** Implementation ****** */

	/**
	 * 
	 * @param command
	 * @return
	 */
	public synchronized boolean postValue(Facet command) {
		boolean result = false;
		
		return result;
	}
	
	@Override
	public boolean flushValue(Facet facet, String valueOld) {	
		LOG.trace(">> flush(Facet facet = {}, String valueOld = {})", facet, valueOld);
		
		String topic = this.rootTopic + facet.getSid();
		String payload = "" + facet.getValue();
		
		try {
			LOG.info("Publish payload '{}' to topic '{}'. ", payload, topic);
			
			MqttMessage message = new MqttMessage(payload.getBytes());
			message.setQos(0);
			
			if (this.isMqttReady() || this.mqttReconnect() ) {
				this.mqttClient.publish(topic,
						message);
			}
			
			LOG.info("MQTT publish complite. ");
		} catch (MqttException e) {
			LOG.warn("MQTT publich error. ", e);
			return false;
		}
		
		LOG.trace("<< flush(Facet facet, String valueOld): {}", true);
		return true;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		LOG.trace(">> messageArrived(String topic = {}, MqttMessage message = {})", topic, message);
		
		Facet facet = this.subsribersMap.get(topic);
		LOG.debug("Message arrived: topic = {}; facet = {}; ", topic, facet);
		
		if (facet != null) {
			facet.setValue(new String(message.getPayload()));
		}
		
		LOG.trace("<< messageArrived(String topic, MqttMessage message)");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		LOG.trace(">> deliveryComplete(IMqttDeliveryToken token = {})", token);
		
		LOG.trace("<< deliveryComplete(IMqttDeliveryToken token)");
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		LOG.trace(">> connectionLost(Throwable cause = {})", cause);
		
		LOG.warn("MQTT connection lost. ", cause);		
		boolean reconnect = this.mqttReconnect();
		LOG.info("Reconnection is " + reconnect);		
		
		LOG.trace("<< connectionLost(Throwable cause)");
	}

	/* ***** Get & Set ***** */
	
	public Set<Facet> getFacets() {
		return facets;
	}

	public void setFacets(Set<Facet> facets) {
		this.facets = facets;
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getBrokerHost() {
		return brokerHost;
	}

	public void setBrokerHost(String brokerHost) {
		this.brokerHost = brokerHost;
	}

	public Integer getBrokerPortSecure() {
		return brokerPortSecure;
	}

	public void setBrokerPortSecure(Integer brokerPortSecure) {
		this.brokerPortSecure = brokerPortSecure;
	}

	public Integer getWsPort() {
		return wsPort;
	}

	public void setWsPort(Integer wsPort) {
		this.wsPort = wsPort;
	}

	public Integer getWsPortSecure() {
		return wsPortSecure;
	}

	public void setWsPortSecure(Integer wsPortSecure) {
		this.wsPortSecure = wsPortSecure;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRootTopic() {
		return rootTopic;
	}

	public void setRootTopic(String rootTopic) {
		this.rootTopic = rootTopic;
	}

	/* ***** Override ***** */

	@Override
	public String toString() {
		return "MqttService [brokerHost=" + brokerHost + ", brokerPort="
				+ brokerPort + ", brokerPortSecure=" + brokerPortSecure
				+ ", wsPort=" + wsPort + ", wsPortSecure=" + wsPortSecure
				+ ", login=" + login + ", password=" + password + ", rootTopic="
				+ rootTopic + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brokerHost == null) ? 0
				: brokerHost.hashCode());
		result = prime * result + ((brokerPort == null) ? 0
				: brokerPort.hashCode());
		result = prime * result + ((brokerPortSecure == null) ? 0
				: brokerPortSecure.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((password == null) ? 0
				: password.hashCode());
		result = prime * result + ((rootTopic == null) ? 0
				: rootTopic.hashCode());
		result = prime * result + ((wsPort == null) ? 0 : wsPort.hashCode());
		result = prime * result + ((wsPortSecure == null) ? 0
				: wsPortSecure.hashCode());
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
		MqttService other = (MqttService) obj;
		if (brokerHost == null) {
			if (other.brokerHost != null)
				return false;
		} else if (!brokerHost.equals(other.brokerHost))
			return false;
		if (brokerPort == null) {
			if (other.brokerPort != null)
				return false;
		} else if (!brokerPort.equals(other.brokerPort))
			return false;
		if (brokerPortSecure == null) {
			if (other.brokerPortSecure != null)
				return false;
		} else if (!brokerPortSecure.equals(other.brokerPortSecure))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (rootTopic == null) {
			if (other.rootTopic != null)
				return false;
		} else if (!rootTopic.equals(other.rootTopic))
			return false;
		if (wsPort == null) {
			if (other.wsPort != null)
				return false;
		} else if (!wsPort.equals(other.wsPort))
			return false;
		if (wsPortSecure == null) {
			if (other.wsPortSecure != null)
				return false;
		} else if (!wsPortSecure.equals(other.wsPortSecure))
			return false;
		return true;
	}

	

	

}
