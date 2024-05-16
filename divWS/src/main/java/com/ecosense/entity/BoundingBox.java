package com.ecosense.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "bounding_box")
@NamedQuery(name="BoundingBox.findAll", query="SELECT a FROM BoundingBox a")
public class BoundingBox {
	
	@Id
	@SequenceGenerator(name="BOUNDING_BOX_ID_SEQ_GENERATOR", sequenceName="BOUNDING_BOX_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BOUNDING_BOX_ID_SEQ_GENERATOR")
	private Integer id;
	
	@Column(name="min_x")
	private Double minX; 
	
	@Column(name="min_y")
	private Double minY; 

	@Column(name="max_x")
	private Double maxX; 

	@Column(name="max_y")
	private Double maxY; 
	
	@OneToMany(mappedBy = "boundingBox")
	private List<Layer> layers;
	
	public BoundingBox() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public Double getMinX() {
		return minX;
	}

	public Double getMinY() {
		return minY;
	}

	public Double getMaxX() {
		return maxX;
	}

	public Double getMaxY() {
		return maxY;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public void setLayers(List<Layer> layers) {
		this.layers = layers;
	}
	
	

}
