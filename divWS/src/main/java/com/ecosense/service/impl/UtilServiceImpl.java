package com.ecosense.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.output.HtmlDto;
import com.ecosense.entity.Html;
import com.ecosense.repository.HtmlRepository;
import com.ecosense.service.UtilService;

@Service
public class UtilServiceImpl implements UtilService {
	
	@Autowired
	private HtmlRepository htmlRepository;

	@Override
	public HtmlDto getHtml(String partOfApp) {
		Html html = htmlRepository.getOne(partOfApp);
		
		return new HtmlDto(html);
	}

}
