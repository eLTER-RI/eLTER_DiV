package com.ecosense.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ecosense.dto.output.LayerGroupDTO;
import com.ecosense.entity.BoundingBox;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
	
	@Override
	public final int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		LayerDTO other = (LayerDTO) obj;
		if (!id.equals(other.id))
			return false;
		return true;
	}

}
