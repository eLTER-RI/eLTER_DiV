package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PageDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DatasetsODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private BoundingBoxDTO boundingBox;
	private List<DatasetODTO> datasets;

	private PageDTO page;

}

