import * as am4charts from '@amcharts/amcharts4/charts';
import { Phenomenon } from './phenomenon-db';
import { TimeIODatastream } from './timeio-datastream';

export class ChartDetails {

    chart: am4charts.XYChart;
    phenomenon: TimeIODatastream;
    waitingIndicator: any;
    selectedDateInterval: any;
    firstDate: Date;
    lastDate: Date;
    rangeDateMin: Date;
    rangeDateMax: Date;

    constructor() {
        // this.selectedDateInterval = null;
    }

}
