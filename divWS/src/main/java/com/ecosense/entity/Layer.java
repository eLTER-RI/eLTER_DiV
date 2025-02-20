package com.ecosense.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="layer")
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

	@OneToMany(mappedBy = "layer")
	private List<LayerTime> layerTimes;
	
	@ManyToOne
	@JoinColumn(name = "layer_group_id")
	private LayerGroup layerGroup;
	
	@ManyToOne
	@JoinColumn(name = "bounding_box_id")
	private BoundingBox boundingBox;
	
	public Layer() {}

	public Integer getId() {
		return id;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getLayerName() {
		return layerName;
	}

	public String getGeoUrlWms() {
		return geoUrlWms;
	}

	public String getGeoUrlWfs() {
		return geoUrlWfs;
	}

	public String getGeoUrlLegend() {
		return geoUrlLegend;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setGeoUrlWms(String geoUrlWms) {
		this.geoUrlWms = geoUrlWms;
	}

	public void setGeoUrlWfs(String geoUrlWfs) {
		this.geoUrlWfs = geoUrlWfs;
	}

	public void setGeoUrlLegend(String geoUrlLegend) {
		this.geoUrlLegend = geoUrlLegend;
	}

	public String getGeoUrlLegendBiggerZoom() {
		return geoUrlLegendBiggerZoom;
	}

	public String getLayerNameBiggerZoom() {
		return layerNameBiggerZoom;
	}

	public void setGeoUrlLegendBiggerZoom(String geoUrlLegendBiggerZoom) {
		this.geoUrlLegendBiggerZoom = geoUrlLegendBiggerZoom;
	}

	public void setLayerNameBiggerZoom(String layerNameBiggerZoom) {
		this.layerNameBiggerZoom = layerNameBiggerZoom;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTime() {
		return time;
	}

	public Boolean getActive() {
		return active;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getAbstract() {
		return abstractStr;
	}

	public void setAbstract(String abstractStr) {
		this.abstractStr = abstractStr;
	}

	public List<LayerTime> getLayerTimes() {
		return layerTimes;
	}

	public void setLayerTimes(List<LayerTime> layerTimes) {
		this.layerTimes = layerTimes;
	}

	public String getLayerGroupTmp() {
		return layerGroupTmp;
	}

	public void setLayerGroupTmp(String layerGroupTmp) {
		this.layerGroupTmp = layerGroupTmp;
	}

	public String getLayerType() {
		return layerType;
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public LayerGroup getLayerGroup() {
		return layerGroup;
	}

	public void setLayerGroup(LayerGroup layerGroup) {
		this.layerGroup = layerGroup;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public String getSiteUuid() {
		return siteUuid;
	}
	
	public void setSiteUuid(String siteUuid) {
		this.siteUuid = siteUuid;
	}
	
}
