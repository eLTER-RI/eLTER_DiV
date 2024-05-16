package com.ecosense.dto;

import java.io.Serializable;

import com.ecosense.entity.EbvSpatialScope;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvSpatialScopeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	
	public EbvSpatialScopeDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public EbvSpatialScopeDTO(EbvSpatialScope entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}