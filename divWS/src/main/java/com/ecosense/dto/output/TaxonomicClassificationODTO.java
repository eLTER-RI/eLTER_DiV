package com.ecosense.dto.output;

import java.io.Serializable;

public class TaxonomicClassificationODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taxonomicClassificationID;
    private String taxonomicRankName;
    private String taxonomicRankValue;
    private String taxonomicCommonName;

    public TaxonomicClassificationODTO() { }

    public String getTaxonomicClassificationID() {
        return taxonomicClassificationID;
    }

    public void setTaxonomicClassificationID(String taxonomicClassificationID) {
        this.taxonomicClassificationID = taxonomicClassificationID;
    }

    public String getTaxonomicRankName() {
        return taxonomicRankName;
    }

    public void setTaxonomicRankName(String taxonomicRankName) {
        this.taxonomicRankName = taxonomicRankName;
    }

    public String getTaxonomicRankValue() {
        return taxonomicRankValue;
    }

    public void setTaxonomicRankValue(String taxonomicRankValue) {
        this.taxonomicRankValue = taxonomicRankValue;
    }

    public String getTaxonomicCommonName() {
        return taxonomicCommonName;
    }

    public void setTaxonomicCommonName(String taxonomicCommonName) {
        this.taxonomicCommonName = taxonomicCommonName;
    }

}
