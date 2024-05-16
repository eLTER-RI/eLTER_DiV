import { DiagramService } from './diagram.service';
import { SharedService } from './../../../shared/service/shared.service';
import { MeasurementsStationOnePhenom } from './../../../shared/model/measurements-station-one-phenomenon';
import { MeasurementsPhenomenon } from './../../../shared/model/measurements-phenom';
import { MeasurementResponse, MeasurementsODTO } from './../../../shared/model/measurements-response-db';
import { MeasurementRequest, MeasurementRequestsPhenomenon } from './../../../shared/model/measurement-ib';
import { DiagramTimeseries } from './../../../shared/model/diagram-timeseries';
import { Router } from '@angular/router';
import { ChartDetails } from './../../../shared/model/chartDetails';
import { Component, NgZone, OnInit } from '@angular/core';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import * as moment from 'moment';
import { Subscription } from 'rxjs';
import { MalihuScrollbarService } from 'ngx-malihu-scrollbar';

@Component({
  selector: 'app-diagram',
  templateUrl: './diagram.component.html',
  styleUrls: ['./diagram.component.scss']
})
export class DiagramComponent implements OnInit {

  chartsDetails: ChartDetails[] = [];
  timeseries: DiagramTimeseries[];

  measurementsPhenom: MeasurementsPhenomenon[] = [];

  
  scrollbarOptionsGraph = {  theme: 'dark-thick', scrollButtons: { enable: true }, scrollInertia: 0,  setHeight: '85vh'};
  maxDate: any = moment();
  ranges: any = {
      Today: [moment().startOf('day'), moment()],
      Yesterday: [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
      'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment()],
      'Last 30 Days': [moment().subtract(29, 'days').startOf('day'), moment()],
      'This Month': [moment().startOf('month').startOf('day'), moment()],
      'Last Month': [moment().subtract(1, 'month').startOf('month').startOf('day'), moment().subtract(1, 'month').endOf('month').endOf('day')],
  };
  seriesStateBeforeHover: any = null;

  diagramSubscription: Subscription;

  
  private colors: string[] = ['#0879C0', '#96CE58', '#003972', '#BADFFD',
  '#0879C0', '#7b00ff', '#42f5e3', '#917af5',
  '#f7528c', '#f27900', '#eef200', '#f552f7'];

  constructor(private router: Router,
              private sharedService: SharedService,
              private zone: NgZone,
              private diagramService: DiagramService,
              private mScrollbarService: MalihuScrollbarService) { 
  }

  ngOnInit(): void {
    const timeseries = sessionStorage.getItem('diagramTimeseries');
    if (timeseries) {
        this.timeseries =  JSON.parse(timeseries);
    }
    if (!this.timeseries || this.timeseries.length === 0) {
      this.router.navigate(['/home']);
    }

    const measurementsRequestsPhenomenon: MeasurementRequestsPhenomenon[] = this.prepareRequestsForMeasurements();
    this.getMeasurements(measurementsRequestsPhenomenon);

    this.diagramSubscription = this.diagramService.currDiagram.subscribe( state => {
        if (state.action == 'selectOrDeselectTimeseries') {
            this.selectOrDeselectTimeseries(state.obj.timeseries, state.obj.index);
        } else if (state.action == 'removeOneTimeserie') {
            this.removeOneTimeserie(state.obj.tsToRemove, state.obj.index);
        } 
    })
    
  }

  removeOneTimeserie(tsToRemove: DiagramTimeseries, index) {
    // const indexTs = this.timeseries.indexOf(tsToRemove);
    this.timeseries.splice(index, 1);
    // this.stateService.updateTimeseries(this.timeseries);

    let indexChart = -1;
    let chartDetails;
    this.chartsDetails.forEach( (chartDetailsElem, chartDetailsIndex) => {
        if (chartDetailsElem.phenomenon.id === tsToRemove.phenomenon.id) {
            indexChart = chartDetailsIndex;
            chartDetails = chartDetailsElem;
            return;
        }
    });

    // removing chart

    if (!this.timeseries.some( (ts) => ts.phenomenon.id === tsToRemove.phenomenon.id)) {
            this.chartsDetails.splice(indexChart, 1);
    } else { // removing one station series in chart
        const measurementPhenom = this.measurementsPhenom.find(measPhenom => measPhenom.phenomenon.id === chartDetails.phenomenon.id);

        if (measurementPhenom != undefined) {
            measurementPhenom.measurementsStation.forEach( (measStation, indexStation) => {
                if (measStation.station === tsToRemove.station.name && measStation.procedure === tsToRemove.procedure) {
                    this.chartsDetails[indexChart].chart.series.removeIndex(indexStation).dispose();
                    measurementPhenom.measurementsStation.splice(indexStation, 1);
                    return;
                }
            });
        }
    }

    if (this.timeseries.length == 0) {
    //     const state = this.stateService.getStationPanelState();
    //     if (state.checkedTimeseries !== undefined) {
    //         // tslint:disable-next-line: prefer-for-of
    //         for (let i = 0; i < state.checkedTimeseries.length ; i++) {
    //             for (let j = 0; j < state.checkedTimeseries[i].length ; j++) {
    //                     state.checkedTimeseries[i][j] = false;
    //             }
    //         }
    //         state.selectAllTimeseries = false;
    //         this.stateService.updateStationPanelState(state);
    //     }
        this.router.navigate(['/home']);
    }
}

  selectOrDeselectTimeseries(timeseries: DiagramTimeseries, i: number) {
    let chartIndex: number;
    let chartDetails: ChartDetails;
    this.chartsDetails.forEach( (chartDetailsElem, index) => {
        if (chartDetailsElem.phenomenon.id === timeseries.phenomenon.id) {
            chartDetails = chartDetailsElem;
            chartIndex = index;
        }
    });

    let indexStation = -1;
    const measPhenomenon = this.measurementsPhenom.find(elem =>
        elem.phenomenon.id === timeseries.phenomenon.id
    );
    measPhenomenon?.measurementsStation.forEach( (measStation, index) => {
        if (measStation.station === timeseries.station.name) {
            indexStation = index;
        }
    });

    
    if (timeseries.color == null) {
        timeseries.color = this.colors[i % this.colors.length];
        this.mScrollbarService.scrollTo('#graphScroll', '#' + timeseries.phenomenon.id, this.scrollbarOptionsGraph);

        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).fill = am4core.color(timeseries.color);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).stroke = am4core.color(timeseries.color);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).strokeWidth = 3;
    } else {
        //@ts-ignore
        timeseries.color = null;

        // ako zamenimo dve liste boja, ovde ce biti potrebno da se menja (this.color)
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).fill = am4core.color(this.colors[indexStation % this.colors.length]);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).stroke = am4core.color(this.colors[indexStation % this.colors.length]);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).strokeWidth = 1;
    }
}

      // grupisem measurements requesteove sa istim phenomenom
  prepareRequestsForMeasurements(): MeasurementRequestsPhenomenon[] {
    const measurementsRequestsPhenomenon: MeasurementRequestsPhenomenon[] = [];

    this.timeseries?.forEach(ts => {

          const measReq: MeasurementRequest = new MeasurementRequest();
          measReq.timeseriesId = ts.timeseriesId;

          const existingMeasReqPhenom = measurementsRequestsPhenomenon.find(elem => elem.phenomenonId === ts.phenomenon.id);
          if (existingMeasReqPhenom != null) {
              existingMeasReqPhenom.measurementRequests.push(measReq);
          } else {
              const newMeasReqPhenom: MeasurementRequestsPhenomenon = new MeasurementRequestsPhenomenon();
              newMeasReqPhenom.phenomenonId = ts.phenomenon.id;
              newMeasReqPhenom.measurementRequests = [];
              newMeasReqPhenom.measurementRequests.push(measReq);

              measurementsRequestsPhenomenon.push(newMeasReqPhenom);

              const chartDetails: ChartDetails = new ChartDetails();
              chartDetails.phenomenon = ts.phenomenon;

              this.chartsDetails.push(chartDetails);
          }
    });

    return measurementsRequestsPhenomenon;
  }

  // element liste je lista meas-request-ova sa istim phenomenom, prolazim kroz nju i pozivam bek
  async getMeasurements(measurementsRequestPhenom: MeasurementRequestsPhenomenon[]) {
     measurementsRequestPhenom.forEach( async  elem => {
          const response = await this.sharedService.post('sos/getMeasurements', elem.measurementRequests);
            // this.diagramService.getMeasurements(elem.measurementRequests).subscribe((data: MeasurementResponse) => {
                if (response.status === 200) {
                    // lista gde je svaki element {lista merenja i ime stanice} (svuda je isti fenomen)
                    const measResponses: MeasurementsODTO[] = response.entity;

                    const measurementsWithSamePhenom: MeasurementsPhenomenon = new MeasurementsPhenomenon();
                    measurementsWithSamePhenom.measurementsStation = [];
                    measResponses.forEach((measResponse) => {
                        const measStationOnePhenom: MeasurementsStationOnePhenom = new MeasurementsStationOnePhenom();

                        measStationOnePhenom.measurements = measResponse.measurements;
                        measStationOnePhenom.station = measResponse.station;
                        measStationOnePhenom.procedure = measResponse.procedure;
                        measStationOnePhenom.timeSeriesId = measResponse.timeseriesId;
                        measStationOnePhenom.uom = measResponse.uom;

                        measurementsWithSamePhenom.measurementsStation.push(measStationOnePhenom);
                        measurementsWithSamePhenom.phenomenon = measResponse.phenomenon;
                    });
                    this.measurementsPhenom.push(measurementsWithSamePhenom);
                    this.initializeGraph(measurementsWithSamePhenom, elem.measurementRequests );
                }
            });
        // });
    }


  getMeasurementsForDate(i: number, phenomenonId: number) {
    const dateFrom: Date = this.chartsDetails[i].selectedDateInterval.startDate.toDate();
    const dateTo: Date = this.chartsDetails[i].selectedDateInterval.endDate.toDate();

    let chartDetails: any = null;
    this.chartsDetails.forEach(chartDetailsElem => {
        if (chartDetailsElem.phenomenon.id === phenomenonId) {
            chartDetails = chartDetailsElem;
        }
    });
    chartDetails.waitingIndicator.show();

    let measPhenom = this.measurementsPhenom.find(measPhenomEl => measPhenomEl.phenomenon.id === phenomenonId);
    const measReqList: MeasurementRequest[] = [];

    measPhenom?.measurementsStation.forEach(measStation => {
        const measReq: MeasurementRequest = new MeasurementRequest();
        measReq.dateFrom = dateFrom;
        measReq.dateTo = dateTo;
        measReq.timeseriesId = measStation.timeSeriesId;

        measReqList.push(measReq);
    });
    this.diagramService.getMeasurements(measReqList).subscribe((data: MeasurementResponse) => {
        if (data.status === 200) {
            const measResponse: MeasurementsODTO[] = data.entity;
            if (measResponse.length === 0) {
               // this.toastr.warning('No measurements for selected date interval.', 'Warning!');
                chartDetails.waitingIndicator.hide();
                chartDetails.selectedDateInterval = { startDate: moment(chartDetails.firstDate.getTime()),
                                                      endDate: moment(chartDetails.lastDate.getTime())};
                return;
            }

            const dataNew = this.getNewMeasurments(measResponse, chartDetails.chart.scrollbarX);

            chartDetails.firstDate = null;
            chartDetails.lastDate = null;
            measResponse.forEach(stationMeas => {
                if (chartDetails.firstDate == null || chartDetails.firstDate > stationMeas.measurements[0].date) {
                    chartDetails.firstDate = new Date(stationMeas.measurements[0].date);
                }
                if (chartDetails.lastDate == null || chartDetails.lastDate < stationMeas.measurements[stationMeas.measurements.length - 1].date) {
                    chartDetails.lastDate = new Date(stationMeas.measurements[stationMeas.measurements.length - 1].date);
                }
            });

            chartDetails.rangeDateMin = chartDetails.firstDate;
            chartDetails.rangeDateMax = chartDetails.lastDate;
            chartDetails.chart.data = dataNew;
            chartDetails.waitingIndicator.hide();
        }
    });

  }

  
  initializeGraph(measurementsWithSamePhenom: MeasurementsPhenomenon, measurementRequests: MeasurementRequest[]) {
    this.zone.runOutsideAngular(() => {
        const findChart =  this.chartsDetails.find(chartDetailsElem =>
            chartDetailsElem.phenomenon.id === measurementsWithSamePhenom.phenomenon.id
        );
        let chartDetails: ChartDetails;
        
        if (findChart != null) {
          chartDetails = findChart;
        

          const chart = am4core.create(measurementsWithSamePhenom.phenomenon.id + '', am4charts.XYChart);
          chartDetails.chart = chart;

        chart.paddingRight = 20;
        // chart.preloader.disabled = false;

        this.showIndicator(chartDetails);

        const scrollbarX = new am4charts.XYChartScrollbar();

        // da budu obojeni series i u scrollu (ne menja se kad se selektuje timeseries)
        // scrollbarX.scrollbarChart.plotContainer.filters.clear();

        const precipitation = measurementsWithSamePhenom.phenomenon.label.includes('precipitation');

        const dateAxis = chart.xAxes.push(new am4charts.DateAxis());


        dateAxis.periodChangeDateFormats.setKey('day', 'MMM dd');
        dateAxis.periodChangeDateFormats.setKey('month', 'MMM[/] [bold]yyyy');
        dateAxis.dateFormats.setKey('month', 'MMM yyyy');
        dateAxis.dateFormats.setKey('day', 'MMM dd');

        const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

        if (valueAxis.tooltip != null && valueAxis.tooltip != undefined) {
          valueAxis.tooltip.disabled = true;
          // da rezultati ne budu pribijeni za x osu
          if (!precipitation) {
              valueAxis.extraMin = 0.1;
              valueAxis.extraMax = 0.1;
          }
        }

        const dataForChart = [];
        let counter = 0;
        // let stations = []; // za neponavljanje istih stanica u Legendi
        measurementsWithSamePhenom.measurementsStation.forEach((elem, i) => {
            const tempFirstDate = new Date(elem.measurements[0].date)
            const tempLastDate = new Date(elem.measurements[elem.measurements.length - 1].date);

            if (chartDetails.firstDate == null || chartDetails.firstDate > tempFirstDate) {
                chartDetails.firstDate = tempFirstDate;
            }
            if (chartDetails.lastDate == null || chartDetails.lastDate < tempLastDate) {
                chartDetails.lastDate = tempLastDate;
            }
            chartDetails.rangeDateMin = chartDetails.firstDate;
            chartDetails.rangeDateMax = chartDetails.lastDate;

            chartDetails.selectedDateInterval = { startDate: moment(chartDetails.firstDate.getTime()),
                                               endDate: moment(chartDetails.lastDate.getTime())};

            const dateField = 'Date of measurment' + i;
            const valueField = elem.station + ' (value in ' + elem.uom + ')'+ i;
            elem.measurements.forEach(meas => {
                const dataObject = {};
                dataObject[dateField] = new Date(meas.date);
                dataObject[valueField] = meas.value;

                //@ts-ignore
                dataForChart.push(dataObject);
            });


            let series;
            if (precipitation) {
                series = chart.series.push(new am4charts.ColumnSeries());
            } else {
                series = chart.series.push(new am4charts.LineSeries());
            }
            series.dataFields.dateX =  dateField ;//+ '';
            series.dataFields.valueY = valueField ;//+ '';


            const currentTs = this.timeseries.find(ts => ts.timeseriesId === elem.timeSeriesId);
            if (currentTs != null && currentTs.color == null) {
                series.fill = am4core.color(this.colors[counter % this.colors.length]);
                series.stroke = am4core.color(this.colors[counter % this.colors.length]);
            } else if (currentTs != null) {
                series.fill = am4core.color(currentTs.color);
                series.stroke = am4core.color(currentTs.color);
                series.strokeWidth = 3;
            }

            series.tooltipText = "Value: {valueY.value} " + measurementsWithSamePhenom.measurementsStation[0].uom+"\nDate: {dateX.formatDate('dd MMM yyyy HH:mm')}";

            if (!precipitation) {
                const bullet = series.bullets.push(new am4charts.CircleBullet());
                bullet.circle.fill = am4core.color('white');
                bullet.circle.strokeWidth = 1.5;
                bullet.circle.radius = 4;
            }

            series.name = elem.station;
            series.connect = true;

            // series.tensionX = 0.8; // ne budu ravne linije vec zakrivljene

            // stations.push(elem.station); // za neponavljanje istih stanica u Legendi
            scrollbarX.series.push(series);
            counter++;

            // hover lineseries
            if (!precipitation) {
                const segment: am4charts.LineSeriesSegment = series.segments.template;
                segment.interactionsEnabled = true;

                const hoverState = segment.states.create('hover');
                hoverState.properties.strokeWidth = 3;

                const dimmed = segment.states.create('dimmed');
                dimmed.properties.stroke = am4core.color('#dadada');

                segment.events.on('over', (event) => {
                    this.processOver(event?.target?.parent?.parent?.parent, chart);
                });

                segment.events.on('out', (event) => {
                    this.processOut(event?.target?.parent?.parent?.parent, chart);
                });
            }
        });

        chart.legend = new am4charts.Legend();
        chart.legend.parent = chart.plotContainer;
        chart.legend.zIndex = 100;
        chart.legend.contentAlign = 'right'; // center is default
        chart.legend.contentValign = 'top';
        chart.zoomOutButton.align = 'left';
        chart.zoomOutButton.valign = 'bottom';
        chart.zoomOutButton.marginLeft = 10;
        chart.zoomOutButton.marginBottom = 10;

        chart.zoomOutButton.events.on('hit', (event) => {
            chartDetails.rangeDateMin = chartDetails.firstDate;
            chartDetails.rangeDateMax = chartDetails.lastDate;
        });

        if (!precipitation) {
            chart.legend.itemContainers.template.events.on('over', (event) => {
                this.processOver(event?.target?.dataItem?.dataContext, chart);
            });

            chart.legend.itemContainers.template.events.on('out', (event) => {
                this.processOut(event?.target?.dataItem?.dataContext, chart);
            });
        }

        scrollbarX.events.on('propertychanged', event => {
            dateAxis.zoomToDates(chartDetails.rangeDateMin, new Date(chartDetails.rangeDateMax.getTime() + (1000 * 60 * 60 * 24)));
            chartDetails.selectedDateInterval = { startDate: moment(chartDetails.firstDate.getTime()),
                                                  endDate: moment(chartDetails.lastDate.getTime())};
        });

        scrollbarX.events.on('swipeleft', async event => {
            chartDetails.waitingIndicator.show();
            let dataNew: any[] = [];

            const dateFrom = dateAxis.minZoomed;
            const dateTo = dateAxis.maxZoomed;
            chartDetails.rangeDateMin = new Date(dateFrom);
            chartDetails.rangeDateMax = new Date(dateTo);

            const rangeBetweenDates = (dateAxis.maxZoomed - dateAxis.minZoomed);

            const beforeDate = new Date(chartDetails.firstDate.getTime() - (rangeBetweenDates * 2));

            const newMeasurementRequests = this.prepareNewMeasurementsRequest(measurementRequests, beforeDate, chartDetails.firstDate);

            const response = await this.sharedService.post('sos/getMeasurements',newMeasurementRequests);
            
            if (response.status === 200) {
              const newMeasurmentsResponse = response.entity;
              chartDetails.firstDate = new Date(newMeasurmentsResponse[0].measurements[0].date);
              let dataFromDiagram:any[] = chart.data;
              dataNew = this.getNewMeasurments(newMeasurmentsResponse, scrollbarX);
              let dataChart: any = [];
              dataChart = dataNew.concat(dataFromDiagram)
              chart.data = dataChart;
          }
          chartDetails.waitingIndicator.hide();

        });

        scrollbarX.events.on('swiperight', async event => {
            chartDetails.waitingIndicator.show();
            let dataNew: any[] = [];

            const dateFrom = dateAxis.minZoomed;
            const dateTo = dateAxis.maxZoomed;

            chartDetails.rangeDateMin = new Date(dateFrom);
            chartDetails.rangeDateMax = new Date(dateTo);

            const rangeBetweenDates = (dateAxis.maxZoomed - dateAxis.minZoomed);

            const afterDate = new Date(chartDetails.lastDate.getTime() + (rangeBetweenDates * 2));
            const newMeasurementRequests = this.prepareNewMeasurementsRequest(measurementRequests, chartDetails.lastDate, afterDate);

            const response = await this.sharedService.post('sos/getMeasurements',newMeasurementRequests);

            if (response.status === 200) {
              const newMeasurmentsResponse = response.entity;

              if (newMeasurmentsResponse[0].measurements.length > 0) {
                  chartDetails.lastDate = new Date(newMeasurmentsResponse[0].measurements[newMeasurmentsResponse[0].measurements.length - 1].date);

                  dataNew = this.getNewMeasurments(newMeasurmentsResponse, scrollbarX);
                  const dataFromDiagram = chart.data;
                  let dataChart: any[] = [];
                  dataChart = dataFromDiagram.concat(dataNew);
                  chart.data = dataChart;
              }

          }
          chartDetails.waitingIndicator.hide();
        });

        chart.dateFormatter.dateFormat = 'yyyy-MM-dd';
        chart.data = dataForChart;
       
        chart.id = measurementsWithSamePhenom.phenomenon.id + '';
        chart.scrollbarX = scrollbarX;
        chart.cursor = new am4charts.XYCursor();
        chart.cursor.fullWidthLineX = true;
        chart.cursor.xAxis = dateAxis;
        chart.cursor.lineX.strokeOpacity = 0;
        chart.cursor.lineX.fill = am4core.color("#000");
        chart.cursor.lineX.fillOpacity = 0.1;
        chart.cursorOverStyle = am4core.MouseCursorStyle.pointer;


        chart.exporting.menu = new am4core.ExportMenu();
        chart.exporting.filePrefix = measurementsWithSamePhenom.phenomenon.label;
        // chart.exporting.title = 'mina bjelica'
        // chart.exporting.menu.items[0].label = "export";

        chart.events.on('ready', event => {
            chartDetails.waitingIndicator.hide();
        });
      }

    });
  }

  private getNewMeasurments(newMeasurmentsResponse: MeasurementsODTO[], scrollbarX) {
    const dataNew = [];

    const newMeasurementsWithSamePhenom: MeasurementsPhenomenon = new MeasurementsPhenomenon();
    newMeasurementsWithSamePhenom.measurementsStation = [];
    newMeasurmentsResponse.forEach((measResponse) => {
        const newMeasStationOnePhenom: MeasurementsStationOnePhenom = new MeasurementsStationOnePhenom();

        newMeasStationOnePhenom.measurements = measResponse.measurements;
        newMeasStationOnePhenom.station = measResponse.station;
        newMeasStationOnePhenom.uom = measResponse.uom;

        newMeasurementsWithSamePhenom.measurementsStation.push(newMeasStationOnePhenom);
        newMeasurementsWithSamePhenom.phenomenon = measResponse.phenomenon;
    });


    newMeasurementsWithSamePhenom.measurementsStation.forEach((measStation, i) => {
        const dateField1 = 'Date of measurment';
        const valueField1 = measStation.station + ' (value in ' + measStation.uom + ')';
        measStation.measurements.forEach(meas => {
            const dataObject = {};
            dataObject[dateField1] = new Date(meas.date);
            dataObject[valueField1] = meas.value;

            //@ts-ignore
            dataNew.push(dataObject);
        });

        const series2 = scrollbarX.series.getIndex(i);

        series2.dataFields.dateX = dateField1 + '';
        series2.dataFields.valueY = valueField1 + '';
        scrollbarX.series.push(series2);
    });

    return dataNew;
  }

  showIndicator(chartDetails: ChartDetails) {
    //@ts-ignore
    chartDetails.waitingIndicator = chartDetails.chart.tooltipContainer.createChild(am4core.Container);

    chartDetails.waitingIndicator.background.fill = am4core.color('white');
    chartDetails.waitingIndicator.background.fillOpacity = 0.8;
    chartDetails.waitingIndicator.width = am4core.percent(100);
    chartDetails.waitingIndicator.height = am4core.percent(100);

    const indicatorLabel = chartDetails.waitingIndicator.createChild(am4core.Label);
    indicatorLabel.text = 'Loading data...';
    indicatorLabel.align = 'center';
    indicatorLabel.valign = 'middle';
    indicatorLabel.fontSize = 20;
    chartDetails.waitingIndicator.show();
  }

  processOut(hoveredSeries, chart) {
    chart.series.each(series => {
        if (series !== hoveredSeries) {
            series.segments.each(segment => {
                segment.setState('default');
            });
            series.bulletsContainer.setState('default');
        }
    });
    hoveredSeries.stroke = this.seriesStateBeforeHover.stroke;
    hoveredSeries.strokeWidth = this.seriesStateBeforeHover.strokeWidth;
  }

  processOver(hoveredSeries, chart) {
    hoveredSeries.toFront();

    // saving old series state beacuse it might be already selected on timeseries sidebar and it has to stay selected after processOut
    this.seriesStateBeforeHover = {stroke: hoveredSeries.stroke,
                                   strokeWidth: hoveredSeries.strokeWidth};

    hoveredSeries.segments.each(segment => {
        segment.setState('hover');
    });

    chart.series.each(series => {
        if (series !== hoveredSeries) {
            series.segments.each(segment => {
                segment.setState('dimmed');
            });
            series.bulletsContainer.setState('dimmed');
        }
    });
  }

  private prepareNewMeasurementsRequest(measurementRequests, dateFrom, dateTo) {
    const newMeasurementRequests: MeasurementRequest[] = [];

    measurementRequests.forEach(measurementRequest => {
        const newMeasurementRequest = new MeasurementRequest();
        newMeasurementRequest.dateFrom = dateFrom;
        newMeasurementRequest.dateTo = dateTo;
        newMeasurementRequest.timeseriesId = measurementRequest.timeseriesId;

        newMeasurementRequests.push(newMeasurementRequest);
    });

    return newMeasurementRequests;
}

    btn_navigateToHome() {
        this.router.navigate(['home']);
    }

}
