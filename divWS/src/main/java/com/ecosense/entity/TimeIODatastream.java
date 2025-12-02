package com.ecosense.entity;

import java.io.Serializable;

import jakarta.persistence.*;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "timeio_datastream")
@NamedQuery(name="TimeIODatastream.findAll", query="SELECT t FROM TimeIODatastream t")
public class TimeIODatastream implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
	@SequenceGenerator(name="TIMEIO_DATASTREAM_ID_GENERATOR", sequenceName="TIMEIO_DATASTREAM_ID_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIMEIO_DATASTREAM_ID_GENERATOR")
	private Integer id;

	private Integer idIot;
	private String name;
	private String description;
	private String unitMeasName;
	private String unitMeasSymbol;

	@ManyToOne
	@JoinColumn(name="timeio_thing_id")
	private TimeIOThing thing;

}
