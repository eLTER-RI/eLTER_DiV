package com.ecosense.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.teiid.geo.GeometryTransformUtils;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.exception.SimpleException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;

	public static Boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

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

	public static Boolean isOlderThanOneDay(Date date) {
		if (date == null) return false;
		
		Date now = new Date();
		long diff = now.getTime() - date.getTime();
		
		return diff > DAY;
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

	public static Integer getTotalPageNum(Integer totalElements, Integer numPerPage) {
		return (totalElements + (numPerPage -1)) / numPerPage;
	}

	/**
	 * Transforms the coordinates from one EPSG (European Petroleum Survey Group) code to another.
	 *
	 * @param minX The minimum X coordinate value.
	 * @param minY The minimum Y coordinate value.
	 * @param maxX The maximum X coordinate value.
	 * @param maxY The maximum Y coordinate value.
	 * @param epsgSource The EPSG code of the source coordinate reference system.
	 * @param epsgTarget The EPSG code of the target coordinate reference system.
	 * @return An array of transformed coordinates [minX, minY, maxX, maxY].
	 * @throws Exception if an error occurs during the transformation process.
	 */
	public static double[] transformEpsg(double minX, double minY, double maxX, double maxY, String epsgSource, String epsgTarget) {
		try {
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
			Point minPoint4326 = geometryFactory.createPoint(new Coordinate(minY, minX));
			Point maxPoint4326 = geometryFactory.createPoint(new Coordinate(maxY, maxX));

			CoordinateReferenceSystem crsSource = CRS.decode(epsgSource);
			CoordinateReferenceSystem crsTarget = CRS.decode(epsgTarget);

			MathTransform transform = CRS.findMathTransform(crsSource, crsTarget);

			Coordinate minCoordTarget = new Coordinate();
			Coordinate maxCoordTarget = new Coordinate();

			JTS.transform(minPoint4326.getCoordinate(), minCoordTarget, transform);
			JTS.transform(maxPoint4326.getCoordinate(), maxCoordTarget, transform);

			return new double[]{
				minCoordTarget.x, minCoordTarget.y,
				maxCoordTarget.x, maxCoordTarget.y
			};
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, Object> mapFromJsonNode(JsonNode jsonNode) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});

		return map;
	}

}