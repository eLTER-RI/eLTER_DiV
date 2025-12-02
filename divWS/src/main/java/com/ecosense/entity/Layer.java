package com.ecosense.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="layer")
@Getter
@Setter
@NoArgsConstructor
public class Layer implements Serializable {

	private static final long serialVersionUID = 7289883279704215812L;

	@Id
	@SequenceGenerator(name="LAYER_ID_GENERATOR", sequenceName="LAYER_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LAYER_ID_GENERATOR")
	private Integer id;

	@Column(name="uuid")
	private String uuid;

	private String name;

	private String code;

	private String version;
	
	@Column(name="layer_name")
	private String layerName;

	@Column(name="img_url")
	private String imgUrl;
	
	@Column(name="layer_type")
	private String layerType;
	
	@Column(name="layer_group")
	private String layerGroupTmp;

	@Column(name="geo_url_wms")
	private String geoUrlWms;

	@Column(name="geo_url_wfs")
	private String geoUrlWfs;

	@Column(name="geo_url_legend")
	private String geoUrlLegend;
	
	@Column(name="geo_url_legend_bigger_zoom")
	private String geoUrlLegendBiggerZoom;
	
	@Column(name="layer_name_bigger_zoom")
	private String layerNameBiggerZoom;
	
	@Column(name="time_tmp_column")
	private String time;

	@Column(name="abstract")
	private String abstractStr;
	
	@Column(name="active")
	private Boolean active;

	@Column(name="site_uuid")
	private String siteUuid;

	@Column(name="manually_added")
	private Boolean manuallyAdded;

	@Column(name="last_checked")
	private Timestamp lastChecked;

	@Column(name="error_on_last_update")
	private Boolean errorOnLastUpdate;

	@JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> jsonDataset;

	@OneToMany(mappedBy = "layer")
	private List<LayerTime> layerTimes;
	
	@ManyToOne
	@JoinColumn(name = "layer_group_id")
	private LayerGroup layerGroup;
	
	@ManyToOne
	@JoinColumn(name = "bounding_box_id")
	private BoundingBox boundingBox;

	@OneToMany(mappedBy = "layer")
	private List<LayerKeyword> layerKeywords;

}
