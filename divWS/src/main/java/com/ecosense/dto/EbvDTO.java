package com.ecosense.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer ebvId;
	private String title;
	
	public EbvDTO() {
		// TODO Auto-generated constructor stub
	}

	public EbvDTO(Integer id, Integer ebvId, String title) {
		super();
		this.id = id;
		this.ebvId = ebvId;
		this.title = title;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getEbvId() {
		return ebvId;
	}

	public void setEbvId(Integer ebvId) {
		this.ebvId = ebvId;
	}
	
	
	

}
