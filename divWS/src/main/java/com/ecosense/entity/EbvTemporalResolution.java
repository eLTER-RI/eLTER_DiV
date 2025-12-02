package com.ecosense.entity;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ebv_temporal_resolution database table.
 * 
 */
@Entity
@Table(name="ebv_temporal_resolution")
@NamedQuery(name="EbvTemporalResolution.findAll", query="SELECT e FROM EbvTemporalResolution e")
public class EbvTemporalResolution implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="EBV_TEMPORAL_RESOLUTION_ID_GENERATOR", sequenceName="EBV_TEMPORAL_RESOLUTION_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EBV_TEMPORAL_RESOLUTION_ID_GENERATOR")
	private Integer id;

	private String name;

	public EbvTemporalResolution() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}