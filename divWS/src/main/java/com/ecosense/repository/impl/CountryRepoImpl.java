package com.ecosense.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.entity.Country;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.CountryRepo;

@Repository
@Transactional
public class CountryRepoImpl implements CountryRepo {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Country insertCountry(Country country) {
		em.persist(country);
		return country;
	}
	
	@Override
	public Country findCountry(String countryName) throws SimpleException {
		if (countryName == null || countryName.isEmpty()) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_COMPLETE);
		}
		
		List<Country> country = new ArrayList<>();
		
		country = em.createQuery("SELECT country FROM Country country WHERE country.countryName LIKE :countryName", Country.class)
				.setParameter("countryName", countryName)
				.getResultList();
		
		return country.isEmpty() ? null : country.get(0);
	}
	
	@Override
	public List<Country> getAllCountries() {
		
		List<Country> countries = new ArrayList<>();
		countries = em.createNamedQuery("Country.findAllOrderByCountryName", Country.class).getResultList();
		
		return countries;
	}

}
