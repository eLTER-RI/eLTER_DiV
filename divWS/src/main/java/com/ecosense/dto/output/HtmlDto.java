package com.ecosense.dto.output;

import java.io.Serializable;

import com.ecosense.entity.Html;

public class HtmlDto  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String partOfApp;
	private String html;
	private String height;
	
	public HtmlDto() {
		// TODO Auto-generated constructor stub
	}
	
	public HtmlDto(Html htmlEntity) {
		this.partOfApp = htmlEntity.getPartOfApp();
		this.html = htmlEntity.getHtml();
		this.height = htmlEntity.getHeight();
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
