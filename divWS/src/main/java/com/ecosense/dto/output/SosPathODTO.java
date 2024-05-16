package com.ecosense.dto.output;

import java.io.Serializable;

public class SosPathODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String url;
	private String title;
	
	public SosPathODTO() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}

