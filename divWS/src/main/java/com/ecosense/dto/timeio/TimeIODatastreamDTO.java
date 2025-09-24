package com.ecosense.dto.timeio;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeIODatastreamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

	private Integer idIot;
	private String name;
	private String description;
	private String unitMeasName;
	private String unitMeasSymbol;

	private TimeIOThingDTO fkThing;

}
