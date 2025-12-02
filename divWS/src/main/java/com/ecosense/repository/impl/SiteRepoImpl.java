package com.ecosense.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.teiid.geo.GeometryTransformUtils;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterSiteIDTO;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.SiteRepo;


@Repository
@Transactional
public class SiteRepoImpl implements SiteRepo {

	@PersistenceContext
	private EntityManager em;
	

	@Override
	@Transactional
	public void insertSite(Site site) {
		em.persist(site);
		em.flush();
	}
	
	@Override
	public List<Site> getAllPolygons() throws SimpleException {
		List<Site> sites = em.createQuery("SELECT site FROM Site site "
//										+ "WHERE site.polygon IS NOT null AND site.point IS NOT null"
				, Site.class)
				.getResultList();
		
		return sites;
		
	}

	@Override
	public Site getSite(String idSuffix) {
		List<Site> sites = em.createQuery("SELECT site FROM Site site WHERE site.idSuffix LIKE :idSuffix", Site.class)
				.setParameter("idSuffix", idSuffix)
				.getResultList();
		
		return sites.isEmpty() ? null : sites.get(0);
	}

	@Override
	public Site getSite(Integer id) {
		List<Site> sites = em.createQuery("SELECT site FROM Site site WHERE site.id = :id", Site.class)
				.setParameter("id", id)
				.getResultList();
		
		return sites.isEmpty() ? null : sites.get(0);
	}

	@Override
	@Transactional
	public void updateSite(Site siteFromDB) {
		em.merge(siteFromDB);
		em.flush();
	}

	@Override
	public List<Site> getAllPoints() {
		List<Site> sites = em.createQuery("SELECT new Site(site.id, site.idSuffix, site.title, site.point) FROM Site site "
				+ "WHERE site.polygon IS NOT null AND site.point IS NOT null", Site.class)
				.getResultList();
		
		return sites;
	}

	@Override
	public List<String> getAllTitles() {
		List<String> sites = em.createQuery("SELECT site.title FROM Site site ORDER BY site.title", String.class)
				.getResultList();
		
		return sites;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> filterSites(FilterSiteIDTO filterIDTO) {
		StringBuilder querySB = new StringBuilder("SELECT distinct s.id, s.point, s.polygon FROM Site s  ");
		
		if (filterIDTO.getActivitiesId() != null && !filterIDTO.getActivitiesId().isEmpty()) {
			querySB.append("LEFT JOIN s.activities a ");
		}
		
		if (filterIDTO.getCountriesId() != null && !filterIDTO.getCountriesId().isEmpty()) {
			querySB.append("LEFT JOIN s.countries c ");
		}
		
//		querySB.append("WHERE s.polygon IS NOT null ");
		querySB.append("WHERE 1=1 ");
		Map<String, Object> parametars = new HashMap<>();
		
		if (filterIDTO.getActivitiesId() != null && !filterIDTO.getActivitiesId().isEmpty()) {
			querySB.append("AND a.id IN ( :activities) ");
			parametars.put("activities", filterIDTO.getActivitiesId());
		}
		
		if (filterIDTO.getCountriesId() != null && !filterIDTO.getCountriesId().isEmpty()) {
			querySB.append("AND c.id IN ( :countries) ");
			parametars.put("countries", filterIDTO.getCountriesId());
		}
		
		if (filterIDTO.getTitle() != null && !filterIDTO.getTitle().isEmpty()) {
			querySB.append("AND s.title =:title ");
			parametars.put("title", filterIDTO.getTitle());
		}
		
		Query query = em.createQuery(querySB.toString());
		
		for (String key : parametars.keySet()) {
			query.setParameter(key, parametars.get(key));
		}
		
		List<Object[]> sites = query.getResultList();
		
		return sites;
	}
	
	@Override
	public BoundingBoxDTO getBoundingBox32643(Integer siteId) throws SimpleException {
		List<Site> sites = em.createQuery("SELECT site FROM Site site WHERE site.id = :id", Site.class)
		.setParameter("id", siteId)
		.getResultList();
		
		Geometry geom4326 = null;
		if (sites.get(0).getPolygon() != null) {
			geom4326 = sites.get(0).getPolygon();
		}
		geom4326.setSRID(4326);

		Geometry geom32634;
		try {
			geom32634 = GeometryTransformUtils.transform(geom4326, "+proj=longlat +datum=WGS84 +no_defs", // EPSG:4326
					"+proj=utm +zone=34 +datum=WGS84 +units=m +no_defs" // EPSG:32634
			);
		} catch (Exception e) {
			throw new SimpleException(SimpleResponseDTO.PARSE_EXCEPTION);
		}

		BoundingBoxDTO bbox;
		if (sites.get(0).getPolygon() == null) {
			Polygon circle = (Polygon) geom32634;
			bbox = new BoundingBoxDTO(circle.getEnvelopeInternal().getMinX(), circle.getEnvelopeInternal().getMinY(),
					circle.getEnvelopeInternal().getMaxX(), circle.getEnvelopeInternal().getMaxY()); // TODO: ako je point treba smisliti koliki uzeti BB
		} else {
			Polygon polygon = (Polygon) geom32634;
			bbox = new BoundingBoxDTO(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(),
					polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY());
		}

		return bbox;
	}
	
	@Override
	public BoundingBoxDTO getBoundingBox4326(Integer siteId) throws SimpleException {
		List<Site> sites = em.createQuery("SELECT site FROM Site site WHERE site.id = :id", Site.class)
		.setParameter("id", siteId)
		.getResultList();
		
		Geometry geom4326 = null;
		if (sites.get(0).getPolygon() != null) {
			geom4326 = sites.get(0).getPolygon();
		}
		geom4326.setSRID(4326);

		BoundingBoxDTO bbox;
		if (sites.get(0).getPolygon() == null) {
			Polygon circle = (Polygon) geom4326;
			bbox = new BoundingBoxDTO(circle.getEnvelopeInternal().getMinX(), circle.getEnvelopeInternal().getMinY(),
					circle.getEnvelopeInternal().getMaxX(), circle.getEnvelopeInternal().getMaxY()); 
		} else {
			try {
				Polygon polygon = (Polygon) geom4326;

				bbox = new BoundingBoxDTO(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(),
						polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY());
			} catch (ClassCastException e) {
				MultiPolygon polygon = (MultiPolygon) geom4326;
				
				bbox = new BoundingBoxDTO(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(),
						polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY());
			}
		}

		return bbox;
	}
	
	@Override
	public void createCircle(Site site, Double r) {
		Point point = (Point) site.getPoint();
		em.createQuery("update Site set circle = geometry(create_circle(:x, :y, :r)) where id = :id")
		.setParameter("x", point.getX())
		.setParameter("y", point.getY())
		.setParameter("r", r)
		.setParameter("id", site.getId())
		.executeUpdate();
	}
	
}
