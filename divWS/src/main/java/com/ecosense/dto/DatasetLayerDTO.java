package com.ecosense.dto;

import java.io.Serializable;
import java.util.Objects;

import com.ecosense.dto.output.DatasetODTO;
import com.ecosense.dto.output.SiteODTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DatasetLayerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LayerDTO layer;
    private DatasetODTO dataset;

    @Override
	public final int hashCode() {
		return Objects.hash(layer.getId() * 31 + dataset.getId());
	}

	@Override
	public boolean equals(Object obj) {
		DatasetLayerDTO other = (DatasetLayerDTO) obj;
		if (!layer.getId().equals(other.getLayer().getId())
        || !dataset.getId().equals(other.getDataset().getId()))
			return false;
		return true;
	}

}
