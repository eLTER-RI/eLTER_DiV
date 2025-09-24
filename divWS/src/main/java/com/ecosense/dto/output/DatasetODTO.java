package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DatasetODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String id;
    private String title;

    private MetadataODTO metadata;
    private LinkODTO links;
    
    private Map<String, Object> jsonDataset;

    private List<SiteODTO> sites;


    @Override
	public final int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		DatasetODTO other = (DatasetODTO) obj;
		if (!id.equals(other.id))
			return false;
		return true;
	}
    
}
