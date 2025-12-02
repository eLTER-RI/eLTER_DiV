package com.ecosense.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;


/**
 * The persistent class for the ebv_name database table.
 * 
 */
@Entity
@Table(name="ebv_name")
@NamedQuery(name="EbvName.findAll", query="SELECT e FROM EbvName e")
public class EbvName implements Serializable {
	private static final long serialVersionUID = 1L;


	@Id
	@SequenceGenerator(name="EBV_NAME_ID_GENERATOR", sequenceName="EBV_NAME_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EBV_NAME_ID_GENERATOR")
	private Integer id;

	private String name;
	
	@ManyToOne
	@JoinColumn(name="ebv_class_id")
	private EbvClass ebvClass;
	
	
	@OneToMany(mappedBy="ebvName")
	private List<Ebv> ebvs;

	public EbvName() {
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

	public EbvClass getEbvClass() {
		return ebvClass;
	}

	public void setEbvClass(EbvClass ebvClass) {
		this.ebvClass = ebvClass;
	}
	
	
	
	

}