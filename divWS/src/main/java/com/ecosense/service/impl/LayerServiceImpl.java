package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.CapabilitiesRequestDTO;
import com.ecosense.dto.LayerDTO;
import com.ecosense.dto.input.FeatureInfoRequestIDTO;
import com.ecosense.dto.output.LayerGroupDTO;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;
import com.ecosense.entity.LayerTime;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.LayerRepository;
import com.ecosense.repository.LayerTimeRepository;
import com.ecosense.service.LayerService;
import com.ecosense.utils.SnowcoverData;

@Service
public class LayerServiceImpl implements LayerService {
	
	@Autowired
	LayerRepository layerRepository;
	
	@Autowired
	LayerTimeRepository layerTimeRepository;
	
	@Autowired
	private @Qualifier("nonSSLRestTemplate") RestTemplate restTemplate;
	
	private final Double LAT_LNG_RADIUS = 0.005;

	@Override
	public List<LayerDTO> getAll(List<String> layertypes, List<Integer> ids, String code) throws SimpleException {
		List<LayerDTO> layers = new ArrayList<>();
		List<Layer> layersFromDB = null;
		
		

		layersFromDB = layerRepository.getAllActive(layertypes, code, ids);
		
		
		for (Layer layer : layersFromDB) {
			layers.add(new LayerDTO(layer, true, true));
		}
		return layers;
	}
	
	@Override
	public String getFeatureInfoHTML(FeatureInfoRequestIDTO firDB) throws Exception {
		Layer layer = layerRepository.findLayerByCode(firDB.getType());
		Double lat = firDB.getLatLng().getLat();
		Double lng = firDB.getLatLng().getLng();

		String queryLayers = "QUERY_LAYERS=" + layer.getLayerName();
		String layers = "LAYERS=" + layer.getLayerName();
		String bbox = "BBOX=" + (lng - LAT_LNG_RADIUS) + "," + (lat - LAT_LNG_RADIUS) + "," + (lng + LAT_LNG_RADIUS) + "," + (lat + LAT_LNG_RADIUS);
		String url = layer.getGeoUrlWfs() + 
				"?service=WMS&VERSION=" + layer.getVersion() + "&request=GetFeatureInfo&crs=CRS:84&WIDTH=825&HEIGHT=842&X=50&Y=50&INFO_FORMAT=text/html&FORMAT=image/png&" + 
				layers + "&" + queryLayers + "&" + bbox;
		
		
		System.out.println(url);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		Document doc = Jsoup.parse(response.getBody());
		
		return doc.toString();
	}

	@Override
	public Boolean addTimeForSnowCover() throws SimpleException {
		System.out.println(" addTimeForSnowCover start");
		String url = SnowcoverData.ELTER_URL + SnowcoverData.GET_CAPABILITIES;
		
		System.out.println(url);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		Document doc = Jsoup.parse(response.getBody());
		
		Elements capability = doc.getElementsByTag("Capability");
		Elements layerTag = capability.get(0).getElementsByTag("Layer");
		
		Elements layers = layerTag.get(0).getElementsByTag("layer");
		
		layers.forEach( layerEl -> {
			Elements layerNameEl = layerEl.getElementsByTag("Name");
			
			String layerName = layerNameEl.get(0).ownText();
			
			Layer layerFromDB = layerRepository.findActiveLayerByCode(layerName);
			
			if (layerFromDB != null) {
				Elements dimensionEl = layerEl.getElementsByTag("Dimension");
				
				if (!dimensionEl.isEmpty()) {
					String[] times = dimensionEl.get(0).ownText().split(",");
					
					for(int i=0; i < times.length; i++) {
						String date = times[i].trim();
						List<LayerTime> layerTimes = layerTimeRepository.findByNameAndTime(date, layerName);
						
						if (layerTimes.isEmpty()) {
							LayerTime layerTime = new LayerTime();
							layerTime.setAvailableTime(date);
							layerTime.setLayer(layerFromDB);
							
							layerTimeRepository.save(layerTime);
							
							System.out.println("New LayerTime with ID " + layerTime.getId());
						}
					}
				}
			}
			
			
		});

		System.out.println(" addTimeForSnowCover end");
		return true;
	}

	@Override
	public List<LayerGroupDTO> getLayerGroups(List<String> layertype, Integer groupdId) throws SimpleException {
		List<LayerGroupDTO> dtos = new ArrayList<>();
		List<LayerGroup> layerGroups = null;
				
		if (layertype != null) {
			layerGroups = layerRepository.getLayerGroupActive(layertype);
		} else if (groupdId != null) {
			layerGroups = layerRepository.getByLayerGroupId(groupdId);
		}
		
		for(LayerGroup lg : layerGroups) {
			dtos.add(new LayerGroupDTO(lg, true));
		}
		
		return dtos;
	}

	@Override
	public String getCapabilities(CapabilitiesRequestDTO capabilitiesRequestDTO) {
		ResponseEntity<String> response = restTemplate.exchange(capabilitiesRequestDTO.getUrl(), HttpMethod.GET, null, String.class);
		Document doc = Jsoup.parse(response.getBody());
		
		String xml = doc.toString();
		xml = xml.replace("\n", "").replace("\r", "");
		xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		
		return xml;
	}


}
