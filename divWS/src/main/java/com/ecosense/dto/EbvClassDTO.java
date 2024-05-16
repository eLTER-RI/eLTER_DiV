package com.ecosense.dto;

import java.io.Serializable;

import com.ecosense.entity.EbvClass;
import com.ecosense.entity.EbvName;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvClassDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	
	public EbvClassDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public EbvClassDTO(EbvClass entity) {
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