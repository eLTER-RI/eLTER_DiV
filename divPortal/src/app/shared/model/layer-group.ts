import { Layer } from 'src/app/shared/model/layer';
export class LayerGroup {
    id: number;
    name: string;
    iconClass: string;
    layerGroupParent: LayerGroup;

    layers: Layer[];

    opened: boolean;

}