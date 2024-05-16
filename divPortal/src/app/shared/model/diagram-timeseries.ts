import { Station } from './station-db';
import { Phenomenon } from './phenomenon-db';

export class DiagramTimeseries {

    timeseriesId: number;
    station: Station;
    firstTime: Date;
    firstValue: number;
    lastTime: Date;
    lastValue: number;
    procedure: string;
    uom: string;
    phenomenon: Phenomenon;
    color: string;

    constructor() {
        this.phenomenon = new Phenomenon()
     }
}
