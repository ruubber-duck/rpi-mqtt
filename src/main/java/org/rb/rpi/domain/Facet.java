package org.rb.rpi.domain;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbTable;

/**
 * 
 * @author ruubber.duck@gmail.com
 *
 */
@DbTable(value = Facet.TABLE)
@XmlRootElement
public class Facet extends AbstractDomain {

	/* Class */

	private static final long serialVersionUID = -6003791855682221873L;

	public static final String TABLE = "rpi_facet";

	public static final String COLUMN_SID = "sid";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_VALUE = "value_str";

	/**
	 * 
	 * @author ruubber.duck@gmail.com
	 *
	 */
	public static interface FacetValueListener {
		public boolean flushValue(Facet facet, String valueOld);
	}

	/* Instance */

	@DbColumn(value = COLUMN_SID)
	protected String sid = null;

	@DbColumn(value = COLUMN_DESC)
	protected String desc = null;

	@DbColumn(value = COLUMN_VALUE)
	protected String value = null;

	private transient List<FacetValueListener> listeners =
			new LinkedList<FacetValueListener>();

	/**
	 * Default
	 */
	public Facet() {
		super();
	}

	/**
	 * 
	 * @param o
	 * @param sid
	 * @param idThing
	 */
	public Facet(Long id) {
		this();
		this.id = id;
	}

	/**
	 * 
	 * @param o
	 */
	public Facet(Facet o) {
		super(o);
		this.merge(o);
	}

	/* ***** Implementation ***** */

	public void merge(Facet o) {
		super.merge(o);
		this.sid = o.sid;
		this.desc = o.desc;
		this.value = o.value;
	}

	/**
	 * 
	 * @param listener
	 */
	public synchronized void addValueListener(FacetValueListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public synchronized void delValueListener(FacetValueListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * 
	 * @param valueOld
	 */
	protected synchronized void fireValueListeners(String valueOld) {
		for (FacetValueListener listener : this.listeners) {
			listener.flushValue(this,
					valueOld);
		}
	}

	/* ***** Get & Set ***** */

	@XmlElement
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@XmlElement
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@XmlElement
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/* ***** Override ***** */

	@XmlElement
	@Override
	public Long getId() {
		return super.getId();
	}

	@XmlElement
	@Override
	public Integer getVersion() {
		return super.getVersion();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Facet(this);
	}

	@Override
	public String toString() {
		return "Facet [sid=" + sid + ", desc=" + desc + ", value=" + value
				+ ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Facet other = (Facet) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (sid == null) {
			if (other.sid != null)
				return false;
		} else if (!sid.equals(other.sid))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
