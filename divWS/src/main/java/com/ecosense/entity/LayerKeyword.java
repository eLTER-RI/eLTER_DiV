package com.ecosense.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="layer_keywords")
@Getter
@Setter
@NoArgsConstructor
public class LayerKeyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="LAYER_KEYWORDS_ID_GENERATOR", sequenceName="LAYER_KEYWORDS_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LAYER_KEYWORDS_ID_GENERATOR")
    private Integer id;

    private String keyword;

    @ManyToOne
    @JoinColumn(name = "layer_id")
    private Layer layer;

    public LayerKeyword(String keyword, Layer layer) {
        this.keyword = keyword;
        this.layer = layer;
    }

}
