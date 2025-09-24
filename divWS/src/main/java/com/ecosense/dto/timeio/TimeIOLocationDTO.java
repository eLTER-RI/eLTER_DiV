package com.ecosense.dto.timeio;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.PointDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeIOLocationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer idIot;
    private String name;

    private PointDTO coordinates;

    private List<TimeIOThingDTO> things;
}
