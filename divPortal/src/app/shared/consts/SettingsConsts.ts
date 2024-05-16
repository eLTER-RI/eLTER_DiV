//Default values for map view
import { Circle as CircleStyle, Fill, Stroke, Style } from 'ol/style';
import WFS from 'ol/format/WFS';
import GML from 'ol/format/GML';
import RegularShape from 'ol/style/RegularShape';
import { View } from 'ol';


export const DEFAULT_START_LATITUDE = 18.6955;
export const DEFAULT_START_LONGITUDE = 45.5550;
export const DEFAULT_STARTING_ZOOM = 11;

//Default values for layers on init
export const DEFAULT_OPACITY_VALUE = 1;



// stil selektovanja
export const highlightStyle = new Style({
	fill: new Fill({
		color: 'rgba(255, 255, 255, 0.6)',
	}),
	stroke: new Stroke({
		color: 'rgba(40, 209, 58, 1)',
		width: 5,
	}),
});


// stil sateliti
export const satelliteStyle = new Style({
	fill: new Fill({
		color: 'rgba(255, 255, 255, 0.1)'
	}),
	stroke: new Stroke({
		color: 'rgba(23, 252, 3, 1)',
		width: 5,
		lineDash: [4, 8],
	}),
});


// stil crtanja
export const drawingStyle = new Style({
	fill: new Fill({
		color: 'rgba(255, 255, 255, 0.2)',
	}),
	stroke: new Stroke({
		color: 'rgba(40, 209, 58, 1)',
		width: 2,
		lineDash: [4, 8],
	}),

	image: new RegularShape({
		fill: new Fill({
			color: 'black'
		}),
		points: 4,
		radius1: 15,
		radius2: 1
	}),
})


// stil nakon crtanja
export const afterDrawingStyle = new Style({
	fill: new Fill({
		color: 'rgba(255, 255, 255, 0.6)',
	}),
	stroke: new Stroke({
		color: 'rgba(40, 209, 58, 1)',
		width: 3,
	}),
	image: new CircleStyle({
		radius: 5,
		fill: new Fill({
			color: 'orange',
		}),
	}),


})


// stil merenja
export const measureStyle = new Style({
	fill: new Fill({
		color: 'rgba(40, 209, 58, 0.2)',
	}),
	stroke: new Stroke({
		color: 'rgba(40, 209, 58, 1)',
		width: 2,
	}),

})

export const drawingOptions = {
	POINT: 'Point',
	LINE_STRING: 'LineString',
	POLYGON: 'Polygon',
	CIRCLE: 'Circle',
};


export const formatParcel = new WFS({
	featureNS: "zito",
	featureType: 'parcel',
	schemaLocation: "http://www.opengis.net/wfs/2.0"
});

export const formatGMLParcel = new GML({
	srsName: "urn:ogc:def:crs:EPSG::4326",
	featureNS: 'zito',
	featureType: 'parcel',
});



export const formatParcelAdministrative = new WFS({
	featureNS: "zito",
	featureType: 'parcel',
	schemaLocation: "http://www.opengis.net/wfs/2.0"
});

export const formatGMLParcelAdministrative = new GML({
	srsName: "urn:ogc:def:crs:EPSG::4326",
	featureNS: 'zito',
	featureType: 'parcel_administrative',
});


export const view4326 = new View({
	projection: 'EPSG:4326',
})

