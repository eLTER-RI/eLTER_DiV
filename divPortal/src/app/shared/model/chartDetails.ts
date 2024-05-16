import * as am4charts from '@amcharts/amcharts4/charts';
import { Phenomenon } from './phenomenon-db';

export class ChartDetails {

    chart: am4charts.XYChart;
    phenomenon: Phenomenon;
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
