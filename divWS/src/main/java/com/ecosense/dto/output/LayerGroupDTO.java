package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ecosense.dto.LayerDTO;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;

public class LayerGroupDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String iconClass;
	private LayerGroupDTO layerGroupParent;
	
	private List<LayerDTO> layers;
	
	
	public LayerGroupDTO() {
		// TODO Auto-generated constructor stub
	}

	public LayerGroupDTO(LayerGroup layerGroup) {
		this.id = layerGroup.getId();
		this.name = layerGroup.getName();
		this.iconClass = layerGroup.getIconClass();
	}
	
	public LayerGroupDTO(LayerGroup layerGroup, boolean withFk) {
		this(layerGroup);
		
		if (withFk) {
			
//			if (layerGroup.getLayerGroupParent() != null) {
//				this.layerGroupParent = new LayerGroupDTO(layerGroup);
//			}
			
			if (layerGroup.getLayers() != null && !layerGroup.getLayers().isEmpty()) {
				this.layers = new ArrayList<>();
				for(Layer layer : layerGroup.getLayers()) {
					this.layers.add(new LayerDTO(layer));
				}
			}
		}
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LayerGroupDTO getLayerGroupParent() {
		return layerGroupParent;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLayerGroupParent(LayerGroupDTO layerGroupParent) {
		this.layerGroupParent = layerGroupParent;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<LayerDTO> getLayers() {
		return layers;
	}

	public void setLayers(List<LayerDTO> layers) {
		this.layers = layers;
	}

	public String getIconClass() {
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}


	
	
	
	
	

}
