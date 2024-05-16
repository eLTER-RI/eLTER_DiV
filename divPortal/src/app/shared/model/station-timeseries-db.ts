import { Timeseries } from './timeseries-db';

import { Point } from './point-db';

export class StationTimeseries {

    stationId: number;
    title: string;
    featureOfInterest: string;
    isProviderValid: boolean;

    point: Point;

    timeseriesPhenomenon: TimeseriesPhenomenon[];

    constructor() { 
        this.timeseriesPhenomenon = [];
    }
}

export class TimeseriesPhenomenon {

    timeseries: Timeseries[];
    phenomenonLabelEn: string;
    selected: boolean;

    constructor() { 
        this.timeseries = [];
    }

}
