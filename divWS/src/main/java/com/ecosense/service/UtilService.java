package com.ecosense.service;

import org.springframework.stereotype.Service;

import com.ecosense.dto.output.HtmlDto;

@Service
public interface UtilService {

	HtmlDto getHtml(String partOfApp);

}
