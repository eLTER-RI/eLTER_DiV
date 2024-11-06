package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PageDTO;

public class SitesODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BoundingBoxDTO boundingBox;
	private List<SiteODTO> sites;

	private PageDTO page;
	
	public SitesODTO() {
		// TODO Auto-generated constructor stub
	}

	public BoundingBoxDTO getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxDTO boundingBox) {
		this.boundingBox = boundingBox;
	}

	public List<SiteODTO> getSites() {
		return sites;
	}

	public void setSites(List<SiteODTO> sites) {
		this.sites = sites;
	}

	public PageDTO getPage() {
		return page;
	}

	public void setPage(PageDTO page) {
		this.page = page;
	}

}
