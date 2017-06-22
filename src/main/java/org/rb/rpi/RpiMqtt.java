package org.rb.rpi;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.log.LoggerProvider;
import jodd.log.impl.Slf4jLogger;
import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.props.Props;
import jodd.props.PropsEntry;
import jodd.props.PropsUtil;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
public class RpiMqtt {

	/* Class */

	private static final Logger LOG = LoggerFactory.getLogger(RpiMqtt.class);

	public static String ARG_PATH_TO_PROPS = "org.rb.rpi";
	public static String PROPS_ENV_PREFIX = "env";
	public static String PROPS_SYS_PREFIX = "sys";

	private static RpiMqtt instance = null;

	public static RpiMqtt getInstance() {
		if (instance == null) {
			instance = new RpiMqtt();
			instance.init();
		}
		return instance;
	}

	/* Inject */

	protected String classpathProps = null;
	protected String classpathBeans = null;

	/* Instance */

	private volatile boolean runned = false;

	protected Props props = null;
	protected PetiteConfig config = null;
	protected AutomagicPetiteConfigurator configurator = null;
	protected PetiteContainer container = null;

	/**
	 * Default
	 */
	public RpiMqtt() {
		super();
		String thisPackageName = this.getClass()
				.getPackage()
				.getName();

		this.classpathProps = "/" + thisPackageName.replaceAll("\\.",
				"/") + "/rpi-*.prop*";
		this.classpathBeans = thisPackageName + ".*";
	}

	/* ***** Routines ***** */

	/**
	 * 
	 * @param prefix
	 * @param propertyKey
	 * @return
	 */
	private boolean loadExternalProps(String propertyKey) {

		if (this.props.getValue(propertyKey) != null) {
			try {
				String path = this.props.getValue(propertyKey);
				this.props.load(new File(path));
				LOG.info("Properties is loaded from external file {}.",
						path);
				return true;
			} catch (Exception e) {
				LOG.error("Fail to load properties file specified in argument '"
						+ propertyKey + "'. ",
						e);
			}
		}

		return false;
	}

	/**
	 * Logger
	 */
	protected void initLogger() {
		jodd.log.LoggerFactory.setLoggerProvider(
				new LoggerProvider<Slf4jLogger>() {
					@Override
					public Slf4jLogger createLogger(String name) {
						return new Slf4jLogger(LoggerFactory.getLogger(name));
					}
				});
	}

	/**
	 * Properties
	 */
	protected void initProps() {
		this.props = new Props();
		this.props.loadEnvironment(PROPS_ENV_PREFIX);
		this.props.loadSystemProperties(PROPS_SYS_PREFIX);

		String packageName = this.getClass()
				.getPackage()
				.getName();

		Iterator<PropsEntry> it = this.props.iterator();
		while (it.hasNext()) {
			PropsEntry i = it.next();

			if (i.getKey()
					.startsWith(PROPS_ENV_PREFIX + "." + packageName) || i
							.getKey()
							.startsWith(PROPS_SYS_PREFIX + "." + packageName)) {
				String keyNew = i.getKey()
						.replaceAll(PROPS_ENV_PREFIX + ".",
								"")
						.replaceAll(PROPS_SYS_PREFIX + ".",
								"");
				this.props.setValue(keyNew,
						i.getValue());
				LOG.info("Merge propertie from {} to {}",
						i.getKey(),
						keyNew);
			}

			LOG.info("Loaded property: {} = [{}]",
					i.getKey(),
					i.getValue());
		}

		if (!this.loadExternalProps(ARG_PATH_TO_PROPS)) {
			PropsUtil.loadFromClasspath(props,
					this.classpathProps);
			LOG.info("Properties is loaded from classpath {}.",
					this.classpathProps);
		}

	}

	/**
	 * Container
	 */
	protected void initContainer() {
		this.config = new PetiteConfig();
		this.config.setUseFullTypeNames(true);
		this.container = new PetiteContainer(config);
		this.container.defineParameters(props);
		this.configurator = new AutomagicPetiteConfigurator();
		this.configurator.setIncludedEntries(this.classpathBeans);
		this.configurator.configure(this.container);
	}

	/* ***** Implementation ***** */

	/**
	 * 
	 */
	public synchronized void init() {
		LOG.trace(">> init()");

		this.runned = true;
		this.notifyAll();

		this.initLogger();
		this.initProps();
		this.initContainer();

		LOG.trace("<< init()");
	}

	/**
	 * 
	 */
	public synchronized void destroy() {
		LOG.trace(">> destroy()");

		this.container.shutdown();

		this.runned = false;
		this.notifyAll();

		LOG.trace("<< destroy()");
	}

	public synchronized void execute() {
		LOG.trace(">> execute()");

		for (String name : this.container.getBeanNames()) {
			Object bean = this.container.getBean(name);
			LOG.info("Bean '{}' loaded in Petite container: bean = {}; ",
					name,
					bean);
		}

		while (runned) {
			try {
				this.wait(1000L);
			} catch (InterruptedException e) {
				LOG.error("",
						e);
				e.printStackTrace();
			}
		}

		LOG.trace("<< execute()");
	}

	/* ***** Get & Set ***** */

	public Props getProps() {
		return props;
	}

	public void setProps(Props props) {
		this.props = props;
	}

	public PetiteConfig getConfig() {
		return config;
	}

	public void setConfig(PetiteConfig config) {
		this.config = config;
	}

	public AutomagicPetiteConfigurator getConfigurator() {
		return configurator;
	}

	public void setConfigurator(AutomagicPetiteConfigurator configurator) {
		this.configurator = configurator;
	}

	public PetiteContainer getContainer() {
		return container;
	}

	public void setContainer(PetiteContainer container) {
		this.container = container;
	}

}
