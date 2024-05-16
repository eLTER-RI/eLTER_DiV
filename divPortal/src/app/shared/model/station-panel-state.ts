import { StationTimeseries } from './station-timeseries-db';

export class StationPanelState {

    stationTimeSeries: StationTimeseries;
    checkedTimeseries: boolean[][];
    selectAllTimeseries: boolean;


    constructor() { }

}
