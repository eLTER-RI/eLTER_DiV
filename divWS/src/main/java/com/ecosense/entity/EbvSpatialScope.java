package com.ecosense.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the ebv_spatial_scope database table.
 * 
 */
@Entity
@Table(name="ebv_spatial_scope")
@NamedQuery(name="EbvSpatialScope.findAll", query="SELECT e FROM EbvSpatialScope e")
public class EbvSpatialScope implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id
	@SequenceGenerator(name="EBV_SPATIAL_SCOPE_ID_GENERATOR", sequenceName="EBV_SPATIAL_SCOPE_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EBV_SPATIAL_SCOPE_ID_GENERATOR")
	private Integer id;

	private String name;
	
	@OneToMany(mappedBy="ebvSpatialScope")
	private List<Ebv> ebvs;

	public EbvSpatialScope() {
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

	public List<Ebv> getEbvs() {
		return ebvs;
	}

	public void setEbvs(List<Ebv> ebvs) {
		this.ebvs = ebvs;
	}
	
	

}