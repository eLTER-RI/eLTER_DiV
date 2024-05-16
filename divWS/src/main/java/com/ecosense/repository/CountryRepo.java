package com.ecosense.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ecosense.entity.Country;
import com.ecosense.exception.SimpleException;

@Repository
public interface CountryRepo {

	Country insertCountry(Country country);

	Country findCountry(String countryName) throws SimpleException;
	
	List<Country> getAllCountries();
	
}
