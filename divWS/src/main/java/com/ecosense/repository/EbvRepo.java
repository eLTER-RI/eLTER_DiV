package com.ecosense.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ecosense.dto.EbvDTO;
import com.ecosense.dto.input.EbvFilterIDTO;

@Repository
public interface EbvRepo {

	List<EbvDTO> filterEbv(EbvFilterIDTO ebvFilter);

}
