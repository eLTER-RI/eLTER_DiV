package com.ecosense.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.output.DatasetODTO;
import com.ecosense.dto.output.KeywordODTO;
import com.ecosense.entity.BoundingBox;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;
import com.ecosense.entity.LayerKeyword;
import com.ecosense.entity.LayerTime;
import com.ecosense.repository.BoundingBoxRepository;
import com.ecosense.repository.LayerGroupRepository;
import com.ecosense.repository.LayerKeywordRepository;
import com.ecosense.repository.LayerRepository;
import com.ecosense.repository.LayerTimeRepository;
import com.ecosense.service.DatasetService;
import com.ecosense.service.GeoserverCache;
import com.ecosense.service.GeoserverService;
import com.ecosense.utils.APICallService;
import com.ecosense.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@Transactional
public class GeoserverServiceImpl implements GeoserverService {

    @Autowired private LayerRepository layerRepository;
    @Autowired private LayerGroupRepository layerGroupRepository;
    @Autowired private BoundingBoxRepository bboxRepository;
    @Autowired private LayerTimeRepository layerTimeRepository;
    @Autowired private LayerKeywordRepository layerKeywordRepository;

    @Autowired private DatasetService datasetService;
    @Autowired private APICallService apiCallService;

    @Autowired private GeoserverCache geoserverCache;

    private static final Logger log = LoggerFactory.getLogger(GeoserverServiceImpl.class);

    @Override
    public void refreshGeoserverData() throws Exception {
        try {
            List<DatasetODTO> datasets = datasetService.getAllExternalDatasets();
            for (DatasetODTO dataset: datasets) {
                    try {
                        saveOrUpdateLayer(dataset);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error processing dataset: " + dataset.getTitle() + " - " + e.getMessage());
                    }
            }

            if (!datasets.isEmpty())    deleteAllInactiveLayers();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            geoserverCache.clear();
        }
    }

    private void saveOrUpdateLayer(DatasetODTO dataset) {
        try {
            if (dataset.getLinks() == null || dataset.getLinks().getOar() == null) {
                // log.error("No OAR link for dataset: " + dataset.getTitle());
                return;
            }
            Map<String, String> layerFieldsMap = getLayerDataFromOarAPI(dataset.getLinks().getOar());
            if (layerFieldsMap == null)     return;
            
            String geoserverUrl = layerFieldsMap.get("geoserverUrl");
            String layerName = layerFieldsMap.get("layerName");

            Layer layer = getAndSaveLayerWithDataFromGeoserverAPI(geoserverUrl, layerName);

            if (layer != null) {
                saveLayerWithOarData(layerFieldsMap, layer);

                saveLayerWithDataFromDatasetsAPI(dataset, layer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error processing dataset/layer: " + dataset.getTitle() + " - " + e.getMessage());
        }
    }

    private Layer getOrCreateLayer(String code) {
        Layer layer = layerRepository.findByCode(code);
        if (layer == null) {
            layer = new Layer();
        }

        return layer;
    }

    private void saveLayerWithDataFromDatasetsAPI(DatasetODTO dataset, Layer layer) {
        layer.setName(dataset.getTitle());
        layer.setSiteUuid((dataset.getSites().isEmpty()) ? null : dataset.getSites().get(0).getIdSuffix());
        layer.setJsonDataset(dataset.getJsonDataset());

        layerRepository.save(layer);

        saveKeywordsForLayer(dataset.getMetadata().getKeywords(), layer);
    }

    private void saveKeywordsForLayer(List<KeywordODTO> keywords, Layer layer) {
        if (Utils.isEmpty(keywords))    return;

        List<LayerKeyword> newLayerKeywords = new LinkedList<>();

        for (KeywordODTO keyword : keywords) {
            LayerKeyword existingKeyword = layerKeywordRepository.findByLayerAndKeyword(layer, keyword.getName());
            if (existingKeyword != null)    continue;
            
            LayerKeyword layerKeyword = new LayerKeyword(keyword.getName(), layer);
            newLayerKeywords.add(layerKeyword);
        }

        layer.setLayerKeywords(newLayerKeywords);
        layerKeywordRepository.saveAll(newLayerKeywords);
    }

    private Map<String, String> getLayerDataFromOarAPI(String oarUrl) throws Exception {
        JsonNode oarNode = apiCallService.getRequest(oarUrl);

        if (oarNode.isEmpty()) {
            // log.error("No OAR data for URL: " + oarUrl);
            return null;
        }

        Map<String, String> layerFieldsMap = getLayerFieldsFromOarAndGeoserverUrl(oarNode);

        return layerFieldsMap;
    }

    private Map<String, String> getLayerFieldsFromOarAndGeoserverUrl(JsonNode oarNode) throws Exception {
        JsonNode geoserverNode = oarNode.get(0).get("metadata").get("geoserver");
        JsonNode layersNode = geoserverNode.get("layers");

        Boolean layerNodeIsValid = layersNode == null || !layersNode.isArray() || layersNode.size() == 0;
        if (layerNodeIsValid) {
            throw new Exception("No layers in OAR metadata geoserver for layer");
        }

        JsonNode layerNode = layersNode.get(0);

        Map<String, String> layerFields = new HashMap<>();
        layerFields.put("layerName", layerNode.get("layerName").asText());

        String geoserverUrl = geoserverNode.get("getCapabilitiesUrl").asText();
        layerFields.put("geoUrlWms",geoserverUrl.concat("/wms"));

        layerFields.put("geoserverUrl", geoserverUrl);

        return layerFields;
    }

    private Layer getAndSaveLayerWithDataFromGeoserverAPI(String geoserverUrl, String layerName) throws Exception {
        Element geoserverXmlElement = geoserverCache.getOrFetch(geoserverUrl);

        if (geoserverXmlElement == null) {
            saveLayerWithErrorOnUpdate(layerName);

            return null;
        }
        
        Map<String, Object> layerMap = extractLayerDataFromXml(geoserverXmlElement, layerName);
        
        return saveLayerWithGeoserverData(layerMap);
    }

    private Map<String, Object> extractLayerDataFromXml(Element capabilityElement, String layerName) {
        NodeList layerNodes = capabilityElement.getElementsByTagName("Layer");

            for (int i = 1; i < layerNodes.getLength(); i++) {
                Element layerElement = (Element) layerNodes.item(i);

                boolean sameLayer = checkIfSameLayer(layerElement, layerName);
                
                if (sameLayer) {
                    return processAndFillLayerMap(layerElement);
                }
            }
        
        return null;
    }

    private boolean checkIfSameLayer(Element layerElement, String layerName) {
        String layerNameFromGeoserver = getTagValue("Name", layerElement);
        
        boolean sameLayer = layerName != null && layerNameFromGeoserver.contains(layerName);

        return sameLayer;
    }

    private Map<String, Object> processAndFillLayerMap(Element geoserverLayerElement) {
        Map<String, Object> row = new HashMap<>();

        String layerGroupStr = "None";
        String layerGroupId = null;
        String layerType = "selection";
        String geoUrlWfs = null;
        String layerNameBiggerZoom = null;
        String geoUrlLegendBiggerZoom = null;
        Boolean active = true;
        String uuidNumber = UUID.randomUUID().toString();

        try {
            String code = getTagValue("Name", geoserverLayerElement); // TODO po ovome je jedinstven layer

            String name = getTagValue("Title", geoserverLayerElement).replace("_", " ").replace(" - ", "-").replace("(", "").replace(")", "");
            boolean nameIsEmpty = name == null || name.isEmpty();
            if (nameIsEmpty) {
                return null;
            }

            BoundingBoxDTO bbox = getBboxFromXmlNode(geoserverLayerElement);

            String geoUrlLegend = getGeoUrlLegendFromXmlNode(geoserverLayerElement);

            String[] dimensions = null;
            Element dimensionElement = getChildElementByTagName(geoserverLayerElement, "Dimension");
            if (dimensionElement != null && !dimensionElement.getTextContent().isEmpty()) {
                dimensions = dimensionElement.getTextContent().split(",");
            }

            String layerGroupStrParam = getLayerGroupStrFromXmlNode(geoserverLayerElement);
            if (layerGroupStrParam != null) {
                layerGroupStr = layerGroupStrParam;
            }

            layerGroupId = getLayerGroupId(layerGroupStr);

            String abstractText = getTagValue("Abstract", geoserverLayerElement);

            row.put("geoUrlWfs", geoUrlWfs);
            row.put("geoUrlLegend", geoUrlLegend);
            row.put("uuid", uuidNumber);
            row.put("layerNameBiggerZoom", layerNameBiggerZoom);
            row.put("dimensions", dimensions);
            row.put("geoUrlLegendBiggerZoom", geoUrlLegendBiggerZoom);
            row.put("code", code);
            row.put("minX", bbox.getMinX());
            row.put("maxX", bbox.getMaxX());
            row.put("minY", bbox.getMinY());
            row.put("maxY", bbox.getMaxY());
            row.put("active", active);
            row.put("layerGroup", layerGroupStr);
            row.put("layerGroupId", layerGroupId);
            row.put("layerType", layerType);
            row.put("abstract", abstractText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row.size() > 0 ? row : null;
    }

    private String getLayerGroupStrFromXmlNode(Element geoserverLayerNode) {
        String layerGroupStr = null;
        NodeList keywordListNodes = geoserverLayerNode.getElementsByTagName("KeywordList");
        for (int i = 0; i < keywordListNodes.getLength(); i++) {
            Element keywordListElement = (Element) keywordListNodes.item(i);
            NodeList keywordNodes = keywordListElement.getElementsByTagName("Keyword");
            for (int j = 0; j < keywordNodes.getLength(); j++) {
                Element keywordElement = (Element) keywordNodes.item(j);
                String keyword = keywordElement.getTextContent();
                if (keyword.startsWith("variable")) {
                    layerGroupStr = keyword.split(": ")[1].trim();
                    return layerGroupStr;
                }
            }
        }

        return null;
    }

    private String getLayerGroupId(String layerGroupStr) {
        String layerGroupId = null;

        LayerGroup layerGroup = layerGroupRepository.findByNameIgnoreCase(layerGroupStr).orElse(null);
        if (layerGroup != null) {
            layerGroupId = layerGroup.getId().toString();
        } else {
            LayerGroup layerGroupToSave = new LayerGroup(layerGroupStr, "fa fa-object-group");
            layerGroupId = layerGroupRepository.save(layerGroupToSave).getId().toString();
        }
        
        return layerGroupId;
    }

    private BoundingBoxDTO getBboxFromXmlNode(Element geoserverLayerNode) throws Exception {
        Element boundingBoxElement = getChildElementByTagName(geoserverLayerNode, "EX_GeographicBoundingBox");
        double[] minMaxPoints = extractMinMaxPointFromXmlElement(boundingBoxElement);

        if (minMaxPoints == null) {
            throw new Exception("No bounding box for layer.");
        }
        
        BoundingBoxDTO bbox = new BoundingBoxDTO(minMaxPoints[0], minMaxPoints[1], 
                                                 minMaxPoints[2], minMaxPoints[3]);

        return bbox;
    }

    private String getGeoUrlLegendFromXmlNode(Element geoserverLayerNode) {
        String geoUrlLegend = null;

        NodeList stylesNodeList = geoserverLayerNode.getElementsByTagName("Style");
        if (stylesNodeList.getLength() > 0) {
            for (int i = 0; i < stylesNodeList.getLength(); i++) {
                Element styleElement = (Element) stylesNodeList.item(i);
                Element legendURLElement = getChildElementByTagName(styleElement, "LegendURL");
                if (legendURLElement != null) {
                    Element onlineResourceElement = getChildElementByTagName(legendURLElement, "OnlineResource");
                    if (onlineResourceElement != null) {
                        geoUrlLegend = onlineResourceElement.getAttribute("xlink:href");
                        return geoUrlLegend;
                    }
                }
            }
        }

        return null;
    }

    private Layer saveLayerWithGeoserverData(Map<String, Object> layerMap) {
        if (layerMap == null)   return null;

        Layer layer = getOrCreateLayer(layerMap.get("code").toString());
        Boolean isNewLayer = layer.getId() == null;

        if (isNewLayer) {
            layer = saveLayer(layerMap, layer);
        } else {
            layer = updateLayer(layer, layerMap);
        }

        return layer;
    }

    private Layer saveLayer(Map<String, Object> layerMap, Layer layer) {
        layer.setName(""); // added because of the order of saving, to be updated later from dataset
        layer.setGeoUrlWms((String) layerMap.get("geoUrlWms"));
        layer.setGeoUrlLegend((String) layerMap.get("geoUrlLegend"));
        layer.setUuid((String) layerMap.get("uuid"));
        layer.setCode((String) layerMap.get("code"));
        layer.setActive((Boolean) layerMap.get("active"));
        layer.setLayerGroupTmp((String) layerMap.get("layerGroup"));
        layer.setAbstractStr((String) layerMap.get("abstract"));
        layer.setLayerType((String) layerMap.get("layerType"));

        layer.setLastChecked(new Timestamp(new Date().getTime()));
        layer.setManuallyAdded(false);
        layer.setErrorOnLastUpdate(false);

        LayerGroup layerGroup = layerGroupRepository.findById(Integer.parseInt((String) layerMap.get("layerGroupId"))).get();
        layer.setLayerGroup(layerGroup);

        layer = layerRepository.save(layer);

        addLayerTimesToLayer(layer, layerMap);
        writeLayerExtentToLayer(layer, layerMap);
        

        log.info("Layer persisted in DB [LAYER_NAME]:" + layer.getCode());

        return layer;
    }

    private void addLayerTimesToLayer(Layer layer, Map<String, Object> layerMap) {
        boolean hasTimeSeries = layerMap.get("dimensions") != null;
        if (!hasTimeSeries)   return;
        
        Long countOfLayerTimes = layerTimeRepository.countByLayer(layer);
        if (countOfLayerTimes == 0) {
            String[] dimensions = (String[]) layerMap.get("dimensions");
            for (String dimension : dimensions) {
                layerTimeRepository.save(new LayerTime(layer, dimension));
            }
        }
    }

    private void writeLayerExtentToLayer(Layer layer, Map<String, Object> layerMap) {
        try {
            double minX = (double) layerMap.get("minX");
            double maxX = (double) layerMap.get("maxX");
            double minY = (double) layerMap.get("minY");
            double maxY = (double) layerMap.get("maxY");

            BoundingBox bbox = new BoundingBox();
            bbox.setMinX(minX);
            bbox.setMaxX(maxX);
            bbox.setMinY(minY);
            bbox.setMaxY(maxY);
            bbox = bboxRepository.save(bbox);

            layer.setBoundingBox(bbox);
            layerRepository.save(layer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Layer updateLayer(Layer layer, Map<String, Object> layerMap) {
        if (layer.getUuid() == null) {
            layer.setUuid(layerMap.get("uuid").toString());
        }

        if (layer.getAbstractStr() == null) {
            layer.setAbstractStr(layerMap.get("abstract").toString());
        }

        if (layer.getBoundingBox() == null) {
            writeLayerExtentToLayer(layer, layerMap);
        }

        layer.setLastChecked(new Timestamp(new Date().getTime()));
        layer.setErrorOnLastUpdate(false);

        addLayerTimesToLayer(layer, layerMap);

        log.info("Layer already existed in DB [LAYER_NAME]:" + layer.getLayerName());

        return layerRepository.save(layer);
    }

    private void saveLayerWithOarData(Map<String, String> layerFieldsMap, Layer layer) {
        layer.setLayerName(layerFieldsMap.get("layerName"));
        layer.setGeoUrlWms(layerFieldsMap.get("geoUrlWms"));

        layerRepository.save(layer);
    }

    private void saveLayerWithErrorOnUpdate(String layerName) {
        Layer layer = layerRepository.findByCode(layerName);
        if (layer != null) {
            layer.setErrorOnLastUpdate(true);
            layer.setLastChecked(new Timestamp(new Date().getTime()));

            layerRepository.save(layer);

            log.info("Layer marked with error on last update: " + layer.getLayerName());
        }
    }

    private void deleteAllInactiveLayers() {
        List<Layer> allLayers = layerRepository.findAll();
        for (Layer layer : allLayers) {
            if (checkIfLayerIsForDeletion(layer)) {
                deleteOneLayer(layer);
            }
        }
    }

    private Boolean checkIfLayerIsForDeletion(Layer layer) {
        return !layer.getManuallyAdded() &&
               !layer.getErrorOnLastUpdate() &&
                (layer.getLastChecked() == null || Utils.isOlderThanOneDay(layer.getLastChecked()));
    }

    private void deleteOneLayer(Layer layer) {
        layerTimeRepository.deleteByLayer(layer);
        layerKeywordRepository.deleteByLayer(layer);
        
        layerRepository.delete(layer);

        log.info("Deleted inactive layer: " + layer.getName());
    }

    private Element getChildElementByTagName(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return (Element) nodeList.item(0);
        }
        return null;
    }

    private double[] extractMinMaxPointFromXmlElement(Element boundingBoxElement) throws Exception {
        double minX = Double.parseDouble(getTagValue("westBoundLongitude", boundingBoxElement));
        double minY = Double.parseDouble(getTagValue("southBoundLatitude", boundingBoxElement));
        double maxX = Double.parseDouble(getTagValue("eastBoundLongitude", boundingBoxElement));
        double maxY = Double.parseDouble(getTagValue("northBoundLatitude", boundingBoxElement));

        String epsgSource = "EPSG:4326";
        String epsgTarget = "EPSG:3857";

        return Utils.transformEpsg(minX, minY, maxX, maxY, epsgSource, epsgTarget);
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }

}
