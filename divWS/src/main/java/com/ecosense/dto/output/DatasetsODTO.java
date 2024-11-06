package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PageDTO;

public class DatasetsODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private BoundingBoxDTO boundingBox;
	private List<DatasetODTO> datasets;

	private PageDTO page;

    public DatasetsODTO() { }    

    public BoundingBoxDTO getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBoxDTO boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<DatasetODTO> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DatasetODTO> datasets) {
        this.datasets = datasets;
    }

    public PageDTO getPage() {
        return page;
    }

    public void setPage(PageDTO page) {
        this.page = page;
    }

}

