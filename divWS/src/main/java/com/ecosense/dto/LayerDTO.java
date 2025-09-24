package com.ecosense.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ecosense.dto.output.LayerGroupDTO;
import com.ecosense.entity.BoundingBox;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LayerDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String uuid;
	private String name;
	private String version;
	private String code;
	private String layerName;
	private String layerType;
	private LayerGroupDTO layerGroup;
	private String geoUrlWms;
	private String geoUrlWfs;
	private String geoUrlLegend;
	private String time;
	private String imgUrl;
	private BoundingBoxDTO bbox;

	private boolean hasJsonDataset;
	
	private List<String> times;
	
	private String geoUrlLegendBiggerZoom;
	private String layerNameBiggerZoom;
	
	public LayerDTO() {	}
	
	public LayerDTO(Layer layer) {
		this.id = layer.getId();
		this.uuid = layer.getUuid();
		this.name = layer.getName();
		this.layerType = layer.getLayerType();
		this.hasJsonDataset = layer.getJsonDataset() != null && !layer.getJsonDataset().isEmpty();
	}
	
	
	public LayerDTO(Layer layer, boolean moreInfo) {
		this(layer);
		
		if (moreInfo) {
			this.version = layer.getVersion();
			this.code = layer.getCode();
			this.layerName = layer.getLayerName();
			this.geoUrlLegend = layer.getGeoUrlLegend();
			this.geoUrlWfs = layer.getGeoUrlWfs();
			this.geoUrlWms = layer.getGeoUrlWms();
			this.geoUrlLegendBiggerZoom = layer.getGeoUrlLegendBiggerZoom();
			this.layerNameBiggerZoom = layer.getLayerNameBiggerZoom();
			this.time = layer.getTime();
			this.imgUrl = layer.getImgUrl();
			
			if (layer.getBoundingBox() != null) {
				BoundingBox layerBbox = layer.getBoundingBox();
				this.bbox = new BoundingBoxDTO(layerBbox.getMinX(), layerBbox.getMinY(),layerBbox.getMaxX(),layerBbox.getMaxY());
			}
		}
	}
	

	public LayerDTO(Layer layer, boolean moreInfo, boolean withFK) {
		this(layer, moreInfo);
		
		if (withFK) {
			if (layer.getLayerGroup() != null) {
				this.layerGroup = new LayerGroupDTO(layer.getLayerGroup());
			}
		
			if (!layer.getLayerTimes().isEmpty()) {
				times = new ArrayList<>();
				for (LayerTime layerTime : layer.getLayerTimes()) {
					times.add(layerTime.getAvailableTime());
				}
			}
		}
	}


	public Integer getId() {
		return id;
	}


	public String getUuid() {
		return uuid;
	}


	public String getName() {
		return name;
	}


	public String getLayerName() {
		return layerName;
	}


	public String getGeoUrlWms() {
		return geoUrlWms;
	}


	public String getGeoUrlWfs() {
		return geoUrlWfs;
	}


	public String getGeoUrlLegend() {
		return geoUrlLegend;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}


	public void setGeoUrlWms(String geoUrlWms) {
		this.geoUrlWms = geoUrlWms;
	}


	public void setGeoUrlWfs(String geoUrlWfs) {
		this.geoUrlWfs = geoUrlWfs;
	}


	public void setGeoUrlLegend(String geoUrlLegend) {
		this.geoUrlLegend = geoUrlLegend;
	}


	public String getGeoUrlLegendBiggerZoom() {
		return geoUrlLegendBiggerZoom;
	}


	public String getLayerNameBiggerZoom() {
		return layerNameBiggerZoom;
	}


	public void setGeoUrlLegendBiggerZoom(String geoUrlLegendBiggerZoom) {
		this.geoUrlLegendBiggerZoom = geoUrlLegendBiggerZoom;
	}


	public void setLayerNameBiggerZoom(String layerNameBiggerZoom) {
		this.layerNameBiggerZoom = layerNameBiggerZoom;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}

	public List<String> getTimes() {
		return times;
	}


	public void setTimes(List<String> times) {
		this.times = times;
	}


	public String getImgUrl() {
		return imgUrl;
	}


	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}


	public LayerGroupDTO getLayerGroup() {
		return layerGroup;
	}


	public void setLayerGroup(LayerGroupDTO layerGroup) {
		this.layerGroup = layerGroup;
	}

	public String getLayerType() {
		return layerType;
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	public BoundingBoxDTO getBbox() {
		return bbox;
	}

	public void setBbox(BoundingBoxDTO bbox) {
		this.bbox = bbox;
	}

	public boolean isHasJsonDataset() {
		return hasJsonDataset;
	}

	public void setHasJsonDataset(boolean hasJsonDataset) {
		this.hasJsonDataset = hasJsonDataset;
	}
	

}
