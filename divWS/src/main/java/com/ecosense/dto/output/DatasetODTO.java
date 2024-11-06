package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DatasetODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String id;
    private String title;

    private MetadataODTO metadata;

    private List<SiteODTO> sites;

    public DatasetODTO() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SiteODTO> getSites() {
        return sites;
    }

    public void setSites(List<SiteODTO> sites) {
        this.sites = sites;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MetadataODTO getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataODTO metadata) {
        this.metadata = metadata;
    }

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
