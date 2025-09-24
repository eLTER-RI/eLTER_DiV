package com.ecosense.dto.timeio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeIOObservationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Date phenomenonTime;
	private BigDecimal result;

    private Integer datastreamId;

}
