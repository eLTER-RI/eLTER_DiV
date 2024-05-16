package com.ecosense.dto;

import java.io.Serializable;

public class EbvDatasetDetailNodeDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String pathname;
	private String download;
	private String metadataJson;
	private String metadataXml;

	public EbvDatasetDetailNodeDTO(String pathname, String download, String metadataJson, String metadataXml) {
		super();
		this.pathname = pathname;
		this.download = download;
		this.metadataJson = metadataJson;
		this.metadataXml = metadataXml;
	}

	public String getPathname() {
		return pathname;
	}

	public String getDownload() {
		return download;
	}

	public String getMetadataJson() {
		return metadataJson;
	}

	public String getMetadataXml() {
		return metadataXml;
	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public void setMetadataJson(String metadataJson) {
		this.metadataJson = metadataJson;
	}

	public void setMetadataXml(String metadataXml) {
		this.metadataXml = metadataXml;
	}

}
