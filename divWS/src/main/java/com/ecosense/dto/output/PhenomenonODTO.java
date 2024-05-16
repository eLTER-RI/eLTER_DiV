package com.ecosense.dto.output;

import java.io.Serializable;

public class PhenomenonODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String domainId;
	private Integer id;
	
	public PhenomenonODTO() { }

	public PhenomenonODTO(Integer id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
