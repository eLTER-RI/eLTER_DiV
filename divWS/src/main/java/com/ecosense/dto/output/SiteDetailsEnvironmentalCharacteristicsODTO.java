package com.ecosense.dto.output;

import java.io.Serializable;

public class SiteDetailsEnvironmentalCharacteristicsODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Double airTemperatureAvgMonthly;
	private Double airTemperatureAvg;
	private String airTemperatureUnit;
	
	private Double precipitationAvgMonthly;
	private Double precipitationAnnual;
	private String precipitationUnit;

	private String biogeographicalRegion;
	private String biome;
	private String landforms;
	private String geoBonBiome;
	private String geology;
	private String hydrology;
	private String soils;
	private String vegetation;

	private String ecosystemAndLanduse;
	private String eunisHabitat;

	public SiteDetailsEnvironmentalCharacteristicsODTO() {

	}



	public Double getAirTemperatureAvg() {
		return airTemperatureAvg;
	}

	public void setAirTemperatureAvg(Double airTemperatureAvg) {
		this.airTemperatureAvg = airTemperatureAvg;
	}

	public String getAirTemperatureUnit() {
		return airTemperatureUnit;
	}

	public void setAirTemperatureUnit(String airTemperatureUnit) {
		this.airTemperatureUnit = airTemperatureUnit;
	}

	public Double getPrecipitationAnnual() {
		return precipitationAnnual;
	}

	public void setPrecipitationAnnual(Double precipitationAnnual) {
		this.precipitationAnnual = precipitationAnnual;
	}

	public String getPrecipitationUnit() {
		return precipitationUnit;
	}

	public void setPrecipitationUnit(String precipitationUnit) {
		this.precipitationUnit = precipitationUnit;
	}

	public String getBiogeographicalRegion() {
		return biogeographicalRegion;
	}

	public void setBiogeographicalRegion(String biogeographicalRegion) {
		this.biogeographicalRegion = biogeographicalRegion;
	}

	public String getBiome() {
		return biome;
	}

	public void setBiome(String biome) {
		this.biome = biome;
	}

	public String getLandforms() {
		return landforms;
	}

	public void setLandforms(String landforms) {
		this.landforms = landforms;
	}

	public String getGeoBonBiome() {
		return geoBonBiome;
	}

	public void setGeoBonBiome(String geoBonBiome) {
		this.geoBonBiome = geoBonBiome;
	}

	public String getGeology() {
		return geology;
	}

	public void setGeology(String geology) {
		this.geology = geology;
	}

	public String getHydrology() {
		return hydrology;
	}

	public void setHydrology(String hydrology) {
		this.hydrology = hydrology;
	}

	public String getSoils() {
		return soils;
	}

	public void setSoils(String soils) {
		this.soils = soils;
	}

	public String getVegetation() {
		return vegetation;
	}

	public void setVegetation(String vegetation) {
		this.vegetation = vegetation;
	}

	public String getEcosystemAndLanduse() {
		return ecosystemAndLanduse;
	}

	public void setEcosystemAndLanduse(String ecosystemAndLanduse) {
		this.ecosystemAndLanduse = ecosystemAndLanduse;
	}

	public String getEunisHabitat() {
		return eunisHabitat;
	}

	public void setEunisHabitat(String eunisHabitat) {
		this.eunisHabitat = eunisHabitat;
	}



	public Double getAirTemperatureAvgMonthly() {
		return airTemperatureAvgMonthly;
	}



	public Double getPrecipitationAvgMonthly() {
		return precipitationAvgMonthly;
	}



	public void setAirTemperatureAvgMonthly(Double airTemperatureAvgMonthly) {
		this.airTemperatureAvgMonthly = airTemperatureAvgMonthly;
	}



	public void setPrecipitationAvgMonthly(Double precipitationAvgMonthly) {
		this.precipitationAvgMonthly = precipitationAvgMonthly;
	}
	
	

}
