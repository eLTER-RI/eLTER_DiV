package com.ecosense.dto.output;

import java.io.Serializable;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PointDTO;

public class StationODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private PointDTO point;
	private BoundingBoxDTO boundingBox;
	
	public StationODTO() {
		// TODO Auto-generated constructor stub
	}
	
	public StationODTO(Integer id, String name, PointDTO point) {
		super();
		this.id = id;
		this.name = name;
		this.point = point;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PointDTO getPoint() {
		return point;
	}

	public void setPoint(PointDTO point) {
		this.point = point;
	}

	public BoundingBoxDTO getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxDTO boundingBox) {
		this.boundingBox = boundingBox;
	}
	
}
