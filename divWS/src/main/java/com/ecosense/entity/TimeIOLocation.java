package com.ecosense.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "timeio_location")
@NamedQuery(name="TimeIOLocation.findAll", query="SELECT t FROM TimeIOLocation t")
public class TimeIOLocation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIMEIO_LOCATION_ID_GENERATOR", sequenceName="TIMEIO_LOCATION_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIMEIO_LOCATION_ID_GENERATOR")
	private Integer id;

	@Column(name="id_iot")
	private Integer idIot;

	@Column(name="name")
	private String name;

	@Column(columnDefinition = "geometry")
	private Geometry coordinates;

	@OneToMany(mappedBy="location")
	private List<TimeIOThing> things;

}
