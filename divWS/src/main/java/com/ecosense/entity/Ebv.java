package com.ecosense.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the ebv database table.
 * 
 */
@Entity
@NamedQuery(name="Ebv.findAll", query="SELECT e FROM Ebv e")
public class Ebv implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="EBV_ID_GENERATOR", sequenceName="EBV_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EBV_ID_GENERATOR")
	private Integer id;

	@Column(name="creator_name")
	private String creatorName;

	@Column(name="date_created")
	private Date dateCreated;

	private String title;
	
	@Column(name="ebv_id")
	private Integer ebvId;

	//bi-directional many-to-one association to EbvClass
	@ManyToOne
	@JoinColumn(name="ebv_class_id")
	private EbvClass ebvClass;

	//bi-directional many-to-one association to EbvEntityType
	@ManyToOne
	@JoinColumn(name="ebv_entity_type_id")
	private EbvEntityType ebvEntityType;

	//bi-directional many-to-one association to EbvName
	@ManyToOne
	@JoinColumn(name="ebv_name_id")
	private EbvName ebvName;

	//bi-directional many-to-one association to EbvSpatialScope
	@ManyToOne
	@JoinColumn(name="ebv_spatial_scope_id")
	private EbvSpatialScope ebvSpatialScope;

	public Ebv() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCreatorName() {
		return this.creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public EbvClass getEbvClass() {
		return this.ebvClass;
	}

	public void setEbvClass(EbvClass ebvClass) {
		this.ebvClass = ebvClass;
	}

	public EbvEntityType getEbvEntityType() {
		return this.ebvEntityType;
	}

	public void setEbvEntityType(EbvEntityType ebvEntityType) {
		this.ebvEntityType = ebvEntityType;
	}

	public EbvName getEbvName() {
		return this.ebvName;
	}

	public void setEbvName(EbvName ebvName) {
		this.ebvName = ebvName;
	}

	public EbvSpatialScope getEbvSpatialScope() {
		return this.ebvSpatialScope;
	}

	public void setEbvSpatialScope(EbvSpatialScope ebvSpatialScope) {
		this.ebvSpatialScope = ebvSpatialScope;
	}

	public Integer getEbvId() {
		return ebvId;
	}

	public void setEbvId(Integer ebvId) {
		this.ebvId = ebvId;
	}
	
	

}