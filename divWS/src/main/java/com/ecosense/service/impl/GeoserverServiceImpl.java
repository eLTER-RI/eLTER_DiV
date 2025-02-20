package com.ecosense.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ecosense.entity.BoundingBox;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;
import com.ecosense.entity.LayerTime;
import com.ecosense.repository.BoundingBoxRepository;
import com.ecosense.repository.LayerGroupRepository;
import com.ecosense.repository.LayerRepository;
import com.ecosense.repository.LayerTimeRepository;
import com.ecosense.service.GeoserverService;
import com.ecosense.utils.GeoserverData;
import com.ecosense.utils.Utils;

/**
 * This class implements the GeoserverService interface and provides methods to interact with Geoserver.
 * It is responsible for refreshing Geoserver data by retrieving layers from Geoserver and updating the database accordingly.
 */
@Service
@Transactional
public class GeoserverServiceImpl implements GeoserverService {

    private final LayerRepository layerRepository;
    private final LayerGroupRepository layerGroupRepository;
    private final BoundingBoxRepository bboxRepository;
    private final LayerTimeRepository layerTimeRepository;
    
    public GeoserverServiceImpl(LayerRepository layerRepository,
                                LayerGroupRepository layerGroupRepository,
                                BoundingBoxRepository bboxRepository,
                                LayerTimeRepository layerTimeRepository) {
        this.layerRepository = layerRepository;
        this.layerGroupRepository = layerGroupRepository;
        this.bboxRepository = bboxRepository;
        this.layerTimeRepository = layerTimeRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(GeoserverServiceImpl.class);

    /**
     * Refreshes the geoserver data by updating the layers in the database based on the data retrieved from Geoserver.
     * This method iterates through the layers obtained from Geoserver and performs the following actions:
     * - If a layer already exists in the database, it updates the existing fields and checks if the bounding box and time series need to be added.
     * - If a layer does not exist in the database, it persists the layer in the database and writes the layer extent.
     *
     * @throws Exception if an error occurs during the refresh process.
     */
    public void refreshGeoserverData() throws Exception {
            List<Layer> layers = layerRepository.findAll();
            List<Map<String, Object>> gsLayerRows = getLayersFromGeoserver(GeoserverData.GEOSERVER_ELTER_GET_CAPABILITIES_URL);
            for (Map<String, Object> gsLayerRow : gsLayerRows) {
                try {
                    boolean existsInDB = false;
                    for (Layer layer : layers) {
                        if (((String) gsLayerRow.get("layerName")).contains(layer.getLayerName())) { // && !layer.getSiteUuid().equals("bcbc866c-3f4f-47a8-bbbc-0a93df6de7b2")) {
                            existsInDB = true;
                            updateExistingFields(layer, gsLayerRow);
                            if (layer.getBoundingBox() == null) {
                                writeLayerExtent(layer, gsLayerRow);
                            }
                            if (gsLayerRow.get("dimensions") != null) {
                                addTimeSeries(layer, (String[]) gsLayerRow.get("dimensions"));
                            }

                            log.info("Layer already existed in DB " + gsLayerRow.get("layerName"));
                            break;
                        }
                    }

                    if (!existsInDB) { // && !gsLayerRow.get("siteUuid").equals("bcbc866c-3f4f-47a8-bbbc-0a93df6de7b2")) {
                        log.info("Layer persisted in DB [LAYER_NAME]:" + gsLayerRow.get("layerName"));
                        Layer layer = saveGeoServerLayer(gsLayerRow);
                        writeLayerExtent(layer, gsLayerRow);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * Retrieves a list of layers from a Geoserver using the provided URL.
     *
     * @param url The URL of the Geoserver.
     * @return A list of maps, where each map represents a layer and its properties.
     * @throws Exception if an error occurs during the retrieval process.
     */
    private List<Map<String, Object>> getLayersFromGeoserver(String url) throws Exception {
        URL geoserverUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) geoserverUrl.openConnection();
        connection.setRequestMethod("GET");

        // Read the response into a string
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // xml document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(response.toString())));
        Element root = document.getDocumentElement();

        String version = root.getAttribute("version");
        String geoUrlWms = url.split("\\?")[0];
        List<Map<String, Object>> gsLayerRows = new ArrayList<>();

        NodeList capabilityNodes = root.getElementsByTagName("Capability");
        if (capabilityNodes.getLength() > 0) {
            Element capabilityElement = (Element) capabilityNodes.item(0);
            NodeList layerNodes = capabilityElement.getElementsByTagName("Layer");

            for (int i = 1; i < layerNodes.getLength(); i++) {
                Element layerElement = (Element) layerNodes.item(i);
                Map<String, Object> gsLayerRow = processLayer(layerElement, version, geoUrlWms);
                if (gsLayerRow != null) {
                    gsLayerRows.add(gsLayerRow);
                }
            }
        }

        return gsLayerRows;
    }

    
    /**
     * Processes a geoserver layer and returns a map containing the layer information.
     *
     * @param geoserverLayerNode The XML element representing the geoserver layer.
     * @param version The version of the layer.
     * @param geoUrlWms The WMS URL of the layer.
     * @return A map containing the layer information, or null if the layer information is incomplete or invalid.
     */
    public Map<String, Object> processLayer(Element geoserverLayerNode, String version, String geoUrlWms) {
        Map<String, Object> row = new HashMap<>();

        String siteUuid = null;
        String layerGroupStr = "None";
        String layerGroupId = null;
        String layerType = "selection";
        String geoUrlWfs = null;
        String layerNameBiggerZoom = null;
        String geoUrlLegendBiggerZoom = null;
        Boolean active = true;
        String uuidNumber = UUID.randomUUID().toString();

        try {
            String code = getTagValue("Name", geoserverLayerNode);
            String name = getTagValue("Title", geoserverLayerNode).replace("_", " ").replace(" - ", "-").replace("(", "").replace(")", "");
            if (name == null || name.isEmpty()) {
                return null;
            }

            Element boundingBoxElement = getChildElementByTagName(geoserverLayerNode, "EX_GeographicBoundingBox");
            double[] minMaxPoints = extractMinMaxPoint(boundingBoxElement);
            if (minMaxPoints == null) {
                return null;
            }
            double minX = minMaxPoints[0];
            double minY = minMaxPoints[1];
            double maxX = minMaxPoints[2];
            double maxY = minMaxPoints[3];

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
                            break;
                        }
                    }
                }
            }

            String[] dimensions = null;
            Element dimensionElement = getChildElementByTagName(geoserverLayerNode, "Dimension");
            if (dimensionElement != null && !dimensionElement.getTextContent().isEmpty()) {
                dimensions = dimensionElement.getTextContent().split(",");
            }

            NodeList keywordListNodes = geoserverLayerNode.getElementsByTagName("KeywordList");
            for (int i = 0; i < keywordListNodes.getLength(); i++) {
                Element keywordListElement = (Element) keywordListNodes.item(i);
                NodeList keywordNodes = keywordListElement.getElementsByTagName("Keyword");
                for (int j = 0; j < keywordNodes.getLength(); j++) {
                    Element keywordElement = (Element) keywordNodes.item(j);
                    String keyword = keywordElement.getTextContent();
                    if (keyword.startsWith("site")) {
                        siteUuid = keyword.split(": ")[1].trim();
                    }
                    if (keyword.startsWith("variable")) {
                        layerGroupStr = keyword.split(": ")[1].trim();
                    }
                }
            }

            List<LayerGroup> layerGroups = layerGroupRepository.findAll();
            for (LayerGroup lGroup : layerGroups) {
                if (lGroup.getName().equalsIgnoreCase(layerGroupStr)) {
                    layerGroupId = lGroup.getId().toString();
                }
            }

            if (layerGroupId == null) {
                LayerGroup layerGroupToSave = new LayerGroup(layerGroupStr, "fa fa-object-group");
                layerGroupId = layerGroupRepository.save(layerGroupToSave).getId().toString();
            }

            String abstractText = getTagValue("Abstract", geoserverLayerNode);

            row.put("name", name);
            row.put("geoUrlWms", geoUrlWms);
            row.put("geoUrlWfs", geoUrlWfs);
            row.put("geoUrlLegend", geoUrlLegend);
            row.put("layerName", code);
            row.put("uuid", uuidNumber);
            row.put("layerNameBiggerZoom", layerNameBiggerZoom);
            row.put("dimensions", dimensions);
            row.put("geoUrlLegendBiggerZoom", geoUrlLegendBiggerZoom);
            row.put("code", code);
            row.put("siteUuid", siteUuid);
            row.put("minX", minX);
            row.put("maxX", maxX);
            row.put("minY", minY);
            row.put("maxY", maxY);
            row.put("version", version);
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

    /**
     * Retrieves the text content of the first element with the specified tag name from the given XML element.
     *
     * @param tag the tag name to search for
     * @param element the XML element to search within
     * @return the text content of the first matching element, or an empty string if no matching element is found
     */
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }

    /**
     * Retrieves a child element from the parent element by its tag name.
     *
     * @param parent The parent element.
     * @param tagName The tag name of the child element to retrieve.
     * @return The child element with the specified tag name, or null if not found.
     */
    private Element getChildElementByTagName(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return (Element) nodeList.item(0);
        }
        return null;
    }

    /**
     * Updates the existing fields of a Layer object with the values from the provided GeoServer layer row.
     * If the corresponding field in the Layer object is null, it will be updated with the value from the GeoServer layer row.
     * After updating the fields, the Layer object is saved in the layer repository.
     *
     * @param layer The Layer object to be updated.
     * @param gsLayerRow The GeoServer layer row containing the values to update the Layer object.
     */
    private void updateExistingFields(Layer layer, Map<String, Object> gsLayerRow) {
        if (layer.getUuid() == null) {
            layer.setUuid(gsLayerRow.get("uuid").toString());
        }

        if (layer.getAbstract() == null) {
            layer.setAbstract(gsLayerRow.get("abstract").toString());
        }

        layerRepository.save(layer);
    }


    /**
     * Extracts the minimum and maximum point coordinates from the given bounding box element.
     *
     * @param boundingBoxElement the XML element representing the bounding box
     * @return an array of doubles containing the minimum and maximum point coordinates in the format [minX, minY, maxX, maxY]
     * @throws Exception if an error occurs during the extraction process
     */
    public double[] extractMinMaxPoint(Element boundingBoxElement) throws Exception {
        double minX = Double.parseDouble(getTagValue("westBoundLongitude", boundingBoxElement));
        double minY = Double.parseDouble(getTagValue("southBoundLatitude", boundingBoxElement));
        double maxX = Double.parseDouble(getTagValue("eastBoundLongitude", boundingBoxElement));
        double maxY = Double.parseDouble(getTagValue("northBoundLatitude", boundingBoxElement));

        String epsgSource = "EPSG:4326";
        String epsgTarget = "EPSG:3857";

        return Utils.transformEpsg(minX, minY, maxX, maxY, epsgSource, epsgTarget);
    }

    /**
     * Writes the layer extent to the database.
     *
     * @param layer The layer object.
     * @param gsLayerRow The map containing the layer extent values.
     */
    private void writeLayerExtent(Layer layer, Map<String, Object> gsLayerRow) {
        try {
            double minX = (double) gsLayerRow.get("minX");
            double maxX = (double) gsLayerRow.get("maxX");
            double minY = (double) gsLayerRow.get("minY");
            double maxY = (double) gsLayerRow.get("maxY");

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

    /**
     * Adds time series to the specified layer.
     *
     * @param layer the layer to add time series to
     * @param dimensions the array of dimensions for the time series
     */
    private void addTimeSeries(Layer layer, String[] dimensions) {
        Long countOfLayerTypes = layerTimeRepository.countByLayer(layer);
        if (countOfLayerTypes == 0) {
            for (String dimension : dimensions) {
                layerTimeRepository.save(new LayerTime(layer, dimension));
            }
        }
    }

    /**
        * Saves a GeoServer layer based on the provided map of layer properties.
        *
        * @param gsLayerRow the map containing the layer properties
        * @return the saved Layer object
        */
    private Layer saveGeoServerLayer(Map<String, Object> gsLayerRow) {
        Layer layer = new Layer();
        layer.setName(gsLayerRow.get("name").toString());
        layer.setGeoUrlWms((String) gsLayerRow.get("geoUrlWms"));
        layer.setGeoUrlWfs((String) gsLayerRow.get("geoUrlWfs"));
        layer.setGeoUrlLegend((String) gsLayerRow.get("geoUrlLegend"));
        layer.setLayerName((String) gsLayerRow.get("layerName"));
        layer.setUuid((String) gsLayerRow.get("uuid"));
        layer.setCode((String) gsLayerRow.get("code"));
        layer.setVersion((String) gsLayerRow.get("version"));
        layer.setActive((Boolean) gsLayerRow.get("active"));
        layer.setLayerGroupTmp((String) gsLayerRow.get("layerGroup"));
        layer.setSiteUuid((String) gsLayerRow.get("siteUuid"));
        layer.setAbstract((String) gsLayerRow.get("abstract"));
        layer.setLayerType((String) gsLayerRow.get("layerType"));

        LayerGroup layerGroup = layerGroupRepository.findById(Integer.parseInt((String) gsLayerRow.get("layerGroupId"))).get();
        layer.setLayerGroup(layerGroup);

        layer = layerRepository.save(layer);

        if (gsLayerRow.get("dimensions") != null) {
            String[] dimensions = (String[]) gsLayerRow.get("dimensions");
            for (String dimension : dimensions) {
                layerTimeRepository.save(new LayerTime(layer, dimension));
            }
        }

        return layer;
    }

}
