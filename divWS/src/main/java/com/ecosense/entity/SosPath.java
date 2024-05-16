package com.ecosense.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sos_path database table.
 * 
 */
@Entity
@Table(name="sos_path")
@NamedQuery(name="SosPath.findAll", query="SELECT s FROM SosPath s")
public class SosPath implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SOS_PATH_ID_GENERATOR", sequenceName="SOS_PATH_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SOS_PATH_ID_GENERATOR")
	private Integer id;

	private String title;

	private String url;
	
	private Boolean active;

	//bi-directional many-to-one association to Station
	@OneToMany(mappedBy="sosPath")
	private List<Station> stations;

	public SosPath() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Station> getStations() {
		return this.stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public Station addStation(Station station) {
		getStations().add(station);
		station.setSosPath(this);

		return station;
	}

	public Station removeStation(Station station) {
		getStations().remove(station);
		station.setSosPath(null);

		return station;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	

}