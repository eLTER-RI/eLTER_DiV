package com.ecosense.dto;

import java.io.Serializable;

public class EbvMetricsDetailNodeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private EbvMetricDTO metric0;
	private EbvMetricDTO metric1;
	
	public EbvMetricsDetailNodeDTO(EbvMetricDTO metric0, EbvMetricDTO metric1) {
		super();
		this.metric0 = metric0;
		this.metric1 = metric1;
	}
	public EbvMetricDTO getMetric0() {
		return metric0;
	}
	public EbvMetricDTO getMetric1() {
		return metric1;
	}
	public void setMetric0(EbvMetricDTO metric0) {
		this.metric0 = metric0;
	}
	public void setMetric1(EbvMetricDTO metric1) {
		this.metric1 = metric1;
	}
	
	

}
