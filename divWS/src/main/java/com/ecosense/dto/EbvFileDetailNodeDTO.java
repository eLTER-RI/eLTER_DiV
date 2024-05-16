package com.ecosense.dto;

import java.io.Serializable;

public class EbvFileDetailNodeDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String download;
	
	public EbvFileDetailNodeDTO(String download) {
		super();
		this.download = download;
	}

	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}
	
	

}
