package com.ecosense.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "timeio_thing")
@NamedQuery(name="TimeIOThing.findAll", query="SELECT t FROM TimeIOThing t")
public class TimeIOThing implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIMEIO_THING_ID_GENERATOR", sequenceName="TIMEIO_THING_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIMEIO_THING_ID_GENERATOR")
	private Integer id;

	private Integer idIot;
	private String name;
	private String description;

	@ManyToOne
	@JoinColumn(name="timeio_location_id")
	private TimeIOLocation location;

	@OneToMany(mappedBy="thing")
	private List<TimeIODatastream> datastreams;

}
