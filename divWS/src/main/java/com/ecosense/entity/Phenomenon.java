package com.ecosense.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;


/**
 * The persistent class for the phenomenon database table.
 * 
 */
@Entity
@NamedQuery(name="Phenomenon.findAll", query="SELECT p FROM Phenomenon p")
public class Phenomenon implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PHENOMENON_ID_GENERATOR", sequenceName="PHENOMENON_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PHENOMENON_ID_GENERATOR")
	private Integer id;
	
	@Column(name="sos_id")
	private Integer sosId;

	private String domainid;
	
	@Column(name="label")
	private String label;
	
	@Column(name="label_en")
	private String labelEn;

	//bi-directional many-to-one association to Timesery
	@OneToMany(mappedBy="phenomenon", cascade = {CascadeType.REMOVE})
	private List<TimeSeries> timeseries;

	public Phenomenon() {
	}
	
	
	
	public Phenomenon(Integer id, Integer sosId, String domainid, String label, String labelEn) {
		super();
		this.id = id;
		this.sosId = sosId;
		this.domainid = domainid;
		this.label = label;
		this.labelEn = labelEn;
	}



	public Phenomenon(Integer sosId) {
		this.sosId = sosId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getLabelEn() {
		return labelEn;
	}

	public void setLabelEn(String labelEn) {
		this.labelEn = labelEn;
	}

	public String getDomainid() {
		return this.domainid;
	}

	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}

	public List<TimeSeries> getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(List<TimeSeries> timeseries) {
		this.timeseries = timeseries;
	}

	public TimeSeries addTimesery(TimeSeries timesery) {
		getTimeseries().add(timesery);
		timesery.setPhenomenon(this);

		return timesery;
	}

	public TimeSeries removeTimesery(TimeSeries timesery) {
		getTimeseries().remove(timesery);
		timesery.setPhenomenon(null);

		return timesery;
	}

	public Integer getSosId() {
		return sosId;
	}

	public void setSosId(Integer sosId) {
		this.sosId = sosId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainid == null) ? 0 : domainid.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Phenomenon other = (Phenomenon) obj;
		if (!domainid.equals(other.domainid))
			return false;
		if (!label.equals(other.label))
			return false;
//		if (other.labelEn != null && this.labelEn != null && !this.labelEn.equals(other.labelEn))
//			return false;
		return true;
	}
	
	
	
	

}