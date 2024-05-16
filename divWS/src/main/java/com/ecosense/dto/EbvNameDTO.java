package com.ecosense.dto;

import java.io.Serializable;

import com.ecosense.entity.EbvName;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvNameDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private EbvClassDTO ebvClass;
	
	public EbvNameDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public EbvNameDTO(EbvName entity) {
		this(entity, false);
	}
	
	public EbvNameDTO(EbvName entity, boolean includeFks) {
		this.id = entity.getId();
		this.name = entity.getName();
		
		if (includeFks) {
			this.ebvClass = new EbvClassDTO(entity.getEbvClass());
		}
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

	public EbvClassDTO getEbvClass() {
		return ebvClass;
	}

	public void setEbvClass(EbvClassDTO ebvClass) {
		this.ebvClass = ebvClass;
	}
	
	
	
	

}
