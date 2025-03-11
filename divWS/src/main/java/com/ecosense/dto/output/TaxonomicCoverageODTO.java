package com.ecosense.dto.output;

import java.io.Serializable;

public class TaxonomicCoverageODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;

    private TaxonomicClassificationODTO taxonomicClassification;

    public TaxonomicCoverageODTO() { }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaxonomicClassificationODTO getTaxonomicClassification() {
        return taxonomicClassification;
    }

    public void setTaxonomicClassification(TaxonomicClassificationODTO taxonomicClassification) {
        this.taxonomicClassification = taxonomicClassification;
    }

}

