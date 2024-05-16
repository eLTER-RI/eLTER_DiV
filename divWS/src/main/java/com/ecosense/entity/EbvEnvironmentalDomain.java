package com.ecosense.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ebv_environmental_domain database table.
 * 
 */
@Entity
@Table(name="ebv_environmental_domain")
@NamedQuery(name="EbvEnvironmentalDomain.findAll", query="SELECT e FROM EbvEnvironmentalDomain e")
public class EbvEnvironmentalDomain implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id
	@SequenceGenerator(name="EBV_ENVIRONMENTAL_DOMAIN_ID_GENERATOR", sequenceName="EBV_ENVIRONMENTAL_DOMAIN_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EBV_ENVIRONMENTAL_DOMAIN_ID_GENERATOR")
	private Integer id;

	private String name;

	public EbvEnvironmentalDomain() {
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