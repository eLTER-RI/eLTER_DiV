package com.ecosense.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.teiid.geo.GeometryTransformUtils;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterSatelliteIDTO;
import com.ecosense.dto.output.SatelliteODTO;
import com.ecosense.exception.SimpleException;

public class Utils {
	
	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;

	public static XMLGregorianCalendar getXMLGregorianCalendarInUTCFromDate(Date date) throws Exception {
		GregorianCalendar gc = new GregorianCalendar();  
	    gc.setTime(date);  
		
		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance()  
	            .newXMLGregorianCalendar();  
		
	    xmlDate.setYear(gc.get(Calendar.YEAR));  
	    xmlDate.setMonth(gc.get(Calendar.MONTH) + 1);  
	    xmlDate.setDay(gc.get(Calendar.DAY_OF_MONTH));  
	    xmlDate.setHour(gc.get(Calendar.HOUR_OF_DAY));  
	    xmlDate.setMinute(gc.get(Calendar.MINUTE));  
	    xmlDate.setSecond(gc.get(Calendar.SECOND));  
	    xmlDate.setTimezone(0);

	    return xmlDate;
	}
	
	public static BoundingBoxDTO setBB(BoundingBoxDTO newPolygonBB, BoundingBoxDTO currentBB) {
		if (newPolygonBB.getMaxX() > currentBB.getMaxX()) {
			currentBB.setMaxX(newPolygonBB.getMaxX());
		}
		
		if (newPolygonBB.getMaxY() > currentBB.getMaxY()) {
			currentBB.setMaxY(newPolygonBB.getMaxY());
		}
		
		if (newPolygonBB.getMinX() < currentBB.getMinX()) {
			currentBB.setMinX(newPolygonBB.getMinX());
		}
		
		if (newPolygonBB.getMinY() < currentBB.getMinY()) {
			currentBB.setMinY(newPolygonBB.getMinY());
		}
		
		return currentBB;
	}
	
	public static Double roundToNumOfDecimals(Double number, Integer numberOfDecimals) {
		return new BigDecimal(number).setScale(numberOfDecimals, RoundingMode.HALF_UP).doubleValue();
	}
	
	public static Geometry changeCRSto32634ofGeometry(Geometry polygon4326) throws SimpleException {
		polygon4326.setSRID(4326);
		
		Geometry polygon32634 = null;
		try {
			polygon32634 = GeometryTransformUtils.transform(polygon4326, "+proj=longlat +datum=WGS84 +no_defs", // EPSG:4326
					"+proj=utm +zone=34 +datum=WGS84 +units=m +no_defs" // EPSG:32634
			);
			
		} catch (Exception e) {
			throw new SimpleException(SimpleResponseDTO.PARSE_EXCEPTION);
		}
		
		return polygon32634;
	}
	
}
