import { BoundingBox } from './bounding-box-db';
import { LayerGroup } from './layer-group';
export class Layer {

    id: number;
    idHash: string;
    uuid: string;
    name: string;
    version: string;
    code: string;
    layerName: string;
    layerType: string;
    layerGroup: LayerGroup;
    geoUrlWms: string;
    geoUrlWfs: string;
    geoUrlLegend: string;
    geoUrlLegendBiggerZoom: string;
    layerNameBiggerZoom: string;
    time: string;
    imgUrl: string;
    bbox: BoundingBox;

    skipForDelete: boolean;

    showFilter: boolean;
    showMap: boolean;
    selected: boolean;
 
    layerVector: any;
	layerTile: any;
	layerTileBiggerZoom: any;

    times: string[];

    authUsername: string;
	authPassword: string;

    searchFilterType: boolean;

    codeForSidebar: string;

    clickedForInfo: boolean = false;

    hasJsonDataset: boolean;

}