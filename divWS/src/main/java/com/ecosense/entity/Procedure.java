package com.ecosense.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;


/**
 * The persistent class for the procedure database table.
 * 
 */
@Entity
@NamedQuery(name="Procedure.findAll", query="SELECT p FROM Procedure p")
public class Procedure implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PROCEDURE_ID_GENERATOR", sequenceName="PROCEDURE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PROCEDURE_ID_GENERATOR")
	private Integer id;

	@Column(name="sos_id")
	private Integer sosId;

	private String label;

	//bi-directional many-to-one association to Timesery
	@OneToMany(mappedBy="procedure", cascade = {CascadeType.REMOVE})
	private List<TimeSeries> timeseries;

	public Procedure() {
	}
	
	public Procedure(Integer sosId) {
		this.sosId = sosId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<TimeSeries> getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(List<TimeSeries> timeseries) {
		this.timeseries = timeseries;
	}

	public TimeSeries addTimesery(TimeSeries timesery) {
		getTimeseries().add(timesery);
		timesery.setProcedure(this);

		return timesery;
	}

	public TimeSeries removeTimesery(TimeSeries timesery) {
		getTimeseries().remove(timesery);
		timesery.setProcedure(null);

		return timesery;
	}

	public Integer getSosId() {
		return sosId;
	}

	public void setSosId(Integer sosId) {
		this.sosId = sosId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((timeseries == null) ? 0 : timeseries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Procedure other = (Procedure) obj;
		if (!label.equals(other.label))
			return false;
		return true;
	}

}