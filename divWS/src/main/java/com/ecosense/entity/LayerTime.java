package com.ecosense.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="layer_time")
public class LayerTime implements Serializable {
	private static final long serialVersionUID = -6796027104630905161L;
	
	
	@Id
	@SequenceGenerator(name="LAYER_TIME_ID_GENERATOR", sequenceName="LAYER_TIME_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LAYER_TIME_ID_GENERATOR")
	private Integer id;
	
	@Column(name = "available_time")
	private String availableTime;
	
	@ManyToOne
	@JoinColumn(name = "id_layer")
	private Layer layer;
	
	public LayerTime() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public String getAvailableTime() {
		return availableTime;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setAvailableTime(String availableTime) {
		this.availableTime = availableTime;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}
	
	

}
