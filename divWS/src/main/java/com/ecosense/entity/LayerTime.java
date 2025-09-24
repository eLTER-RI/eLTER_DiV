package com.ecosense.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="layer_time")
@Getter
@Setter
@NoArgsConstructor
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

	public LayerTime(Layer layer, String availableTime) {
		this.layer = layer;
		this.availableTime = availableTime;
	}

}
