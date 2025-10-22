package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ecosense.dto.LayerDTO;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LayerGroupDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String iconClass;
	private LayerGroupDTO layerGroupParent;
	
	private List<LayerDTO> layers;

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
	
	
	@Override
	public final int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		LayerGroupDTO other = (LayerGroupDTO) obj;
		if (!id.equals(other.id))
			return false;
		return true;
	}

}
