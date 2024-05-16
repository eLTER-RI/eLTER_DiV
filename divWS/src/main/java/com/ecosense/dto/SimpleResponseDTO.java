package com.ecosense.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Integer OK = 200;
	public static final Integer GENERAL_SERVER_ERROR = 0;
	public static final Integer DATA_NOT_COMPLETE = 1;
	public static final Integer DATA_NOT_EXIST = 2;
	public static final Integer PARSE_EXCEPTION = 3;
	public static final Integer NOT_ALLOWED = 4;

	private Integer status;

	public SimpleResponseDTO() {
		this.status = OK;
	}

	public SimpleResponseDTO(Integer status) {
		super();
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SimpleResponseDB [status=" + status + "]";
	}

}

