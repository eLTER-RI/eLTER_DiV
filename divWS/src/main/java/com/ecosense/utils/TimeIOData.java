package com.ecosense.utils;

public class TimeIOData {
    
    public static final String TIMEIO_BASE_URL = "https://tsm.ufz.de/sta/crnscosmicrayneutronsens_b1b36815413f48ea92ba3a0fbc795f7b/v1.1/";
    
    public static final String TIMEIO_LOCATION_SUFFIX = "Locations?" + 
                                                         "$select=@iot.id,name,location";

    public static final String TIMEIO_THING_SUFFIX = "Locations(locationID_Param)?" + 
                                                      "$select=@iot.id&" +
                                                      "$expand=Things($select=@iot.id,name,description)";

    public static final String TIMEIO_DATASTREAM_SUFFIX = "Things(thingID_Param)?" +
                                                           "$select=@iot.id&" +
                                                           "$expand=Datastreams($select=@iot.id,name,description,unitOfMeasurement)";

    public static final String TIMEIO_OBSERVATION_SUFFIX = "Datastreams(datastreamID_Param)/Observations?" +
                                                            "$select=@iot.id,phenomenonTime,result&" +
                                                            "$filter=phenomenonTime ge phenomenonTimeFrom_Param and phenomenonTime le phenomenonTimeTo_Param&" +
                                                            "$orderby=phenomenonTime asc&" +
                                                            "$top=100000";


}