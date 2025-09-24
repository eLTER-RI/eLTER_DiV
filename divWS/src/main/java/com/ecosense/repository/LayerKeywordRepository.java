package com.ecosense.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerKeyword;

public interface LayerKeywordRepository extends JpaRepository<LayerKeyword, Integer> {
    
    LayerKeyword findByLayerAndKeyword(Layer layer, String keyword);

    void deleteByLayer(Layer layer);

}
