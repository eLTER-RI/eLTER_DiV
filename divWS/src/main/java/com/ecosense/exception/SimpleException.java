package com.ecosense.exception;

public class SimpleException extends Exception {

	private static final long serialVersionUID = 1L;

	private Integer simpleResponseStatus;

	public SimpleException(Integer simpleResponseStatus) {
		this.simpleResponseStatus = simpleResponseStatus;
	}

	public Integer getSimpleResponseStatus() {
		return simpleResponseStatus;
	}

	public void setSimpleResponseCode(Integer simpleResponseStatus) {
		this.simpleResponseStatus = simpleResponseStatus;
	}

}
