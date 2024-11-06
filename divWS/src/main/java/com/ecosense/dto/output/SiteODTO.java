package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.ecosense.dto.PointDTO;
import com.ecosense.dto.PolygonDTO;

public class SiteODTO implements Serializable , Comparable<SiteODTO>{
	
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String idSuffix;
	private Integer id;
	private Date changed;
	private Double area;
	
	private PointDTO point;
	private PolygonDTO polygon;
	private PolygonDTO circle;
	
	List<String> layersSuffixId;
	
	public SiteODTO() {
		// TODO Auto-generated constructor stub
	}
	
	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdSuffix() {
		return idSuffix;
	}

	public void setIdSuffix(String idSuffix) {
		this.idSuffix = idSuffix;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

	public PointDTO getPoint() {
		return point;
	}

	public void setPoint(PointDTO point) {
		this.point = point;
	}

	public PolygonDTO getPolygon() {
		return polygon;
	}

	public void setPolygon(PolygonDTO polygon) {
		this.polygon = polygon;
	}

	public List<String> getLayersSuffixId() {
		if (this.layersSuffixId == null) {
			this.layersSuffixId = new ArrayList<>();
		}
		return layersSuffixId;
	}

	public void setLayersSuffixId(List<String> layersSuffixId) {
		this.layersSuffixId = layersSuffixId;
	}

	public PolygonDTO getCircle() {
		return circle;
	}

	public void setCircle(PolygonDTO circle) {
		this.circle = circle;
	}

	@Override
	public int compareTo(SiteODTO o) {
		if (o.getArea() == null || this.getArea() == null) {
			return 0;
		}
		
		if (o.getArea() > this.getArea())
			return 1;
		else if (o.getArea() < this.getArea())
			return -1;
		else
			return 0;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		SiteODTO other = (SiteODTO) obj;
		if (!id.equals(other.id))
			return false;
		return true;
	}

}
