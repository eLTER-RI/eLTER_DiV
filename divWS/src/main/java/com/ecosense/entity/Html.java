package com.ecosense.entity;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@NamedQuery(name="Html.findAll", query="SELECT e FROM Html e")
public class Html implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="part_of_app")
	private String partOfApp;
	
	private String html;
	
	private String height;
	
	public Html() {
		// TODO Auto-generated constructor stub
	}

	public String getPartOfApp() {
		return partOfApp;
	}

	public String getHtml() {
		return html;
	}

	public void setPartOfApp(String partOfApp) {
		this.partOfApp = partOfApp;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
	

}
