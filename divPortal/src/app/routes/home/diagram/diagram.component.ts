import { DiagramService } from './diagram.service';
import { SharedService } from './../../../shared/service/shared.service';
import { MeasurementsStationOnePhenom } from './../../../shared/model/measurements-station-one-phenomenon';
import { MeasurementsPhenomenon } from './../../../shared/model/measurements-phenom';
import { MeasurementsODTO } from './../../../shared/model/measurements-response-db';
import { MeasurementRequest, MeasurementRequestsPhenomenon } from './../../../shared/model/measurement-ib';
import { ChartDetails } from './../../../shared/model/chartDetails';
import { Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import * as moment from 'moment';
import { Subscription } from 'rxjs';
import { TimeIOService } from 'src/app/shared/service/time-io.service';
import { TimeIODatastream } from 'src/app/shared/model/timeio-datastream';
import { ToastrService } from 'ngx-toastr';
import { SettingsService } from 'src/app/core/settings/settings.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-diagram',
    templateUrl: './diagram.component.html',
    styleUrls: ['./diagram.component.scss'],
    standalone: false
})
export class DiagramComponent implements OnInit, OnDestroy {

  chartsDetails: ChartDetails[] = [];
  datastreams: TimeIODatastream[] = [];

  measurementsPhenom: MeasurementsPhenomenon[] = [];

  @ViewChild('graphScroll', { static: false }) graphScroll!: ElementRef;

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

  isLoading: boolean = false;

  diagramSubscription: Subscription;
  refreshSubscription: Subscription;
  
  private colors: string[] = ['#0879C0', '#96CE58', '#003972', '#badffd',
                              '#0879C0', '#7b00ff', '#42f5e3', '#917af5',
                              '#f7528c', '#f27900', '#eef200', '#f552f7'];

  constructor(private router: Router,
              private sharedService: SharedService,
              private zone: NgZone,
              private diagramService: DiagramService,
              private timeioService: TimeIOService,
              private toastr: ToastrService,
              private settings: SettingsService) { 
  }

  ngOnInit(): void {
    this.refreshDiagram();

    this.initSubscriptions();
  }

  ngOnDestroy() {
    if (this.diagramSubscription) {
      this.diagramSubscription.unsubscribe();
    }
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }

    this.chartsDetails.forEach(chartDetails => {
      if (chartDetails.chart) {
        chartDetails.chart.dispose();
        chartDetails.chart = undefined;
      }
    });

  }

  initSubscriptions() {
    this.refreshSubscription = this.diagramService.getRefreshEvent().subscribe(() => {
      this.refreshDiagram();
      this.openOffsidebar();
    });

    this.diagramSubscription = this.diagramService.getDiagramBehaviorEvent().subscribe( state => {
      if (this.datastreams.length !== 0) {
        if (state.action === 'selectOrDeselectDatastreams') {
            this.selectOrDeselectDatastreams(state.obj.datastreams, state.obj.index);

        } else if (state.action === 'removeOneDatastream') {
            this.removeOneDatastream(state.obj.dsToRemove);
        } 
      }
    });
  }

  async refreshDiagram() {
    this.isLoading = true;
    let datastreamsToBeAdded: TimeIODatastream[] = [];
    let selectedDatastreams: TimeIODatastream[] = this.timeioService.getSelectedDatastreams();

    selectedDatastreams.forEach( (ds) => {
      if (ds.checked === true) {
        ds.showed = true;
        if (!this.datastreams.includes(ds)) {
          datastreamsToBeAdded.push(ds);
        }
      } else {
        this.removeOneDatastream(ds);
      }
    });

    if ((!this.datastreams || this.datastreams.length === 0) && (!datastreamsToBeAdded || datastreamsToBeAdded.length === 0)) {
        this.router.navigate(['/home']);
    }

    const measurementsRequestsPhenomenon: MeasurementRequestsPhenomenon[] = this.prepareRequestsForMeasurements(datastreamsToBeAdded);
    try {
      await this.getMeasurements(measurementsRequestsPhenomenon);
    } finally {
        this.isLoading = false;
    }
  }

  removeOneDatastream(dsToRemove: TimeIODatastream, homeNavigation: boolean = true) {
    this.datastreams.map(ds => {
      if (ds.id === dsToRemove.id && homeNavigation) {
        ds.color = null;
        if (homeNavigation) {
          ds.showed = false;
        }
      }
    });
    this.datastreams = this.datastreams.filter(ds => ds.id !== dsToRemove.id);

    let indexChart = -1;
    let chartDetails;
    this.chartsDetails.forEach( (chartDetailsElem, chartDetailsIndex) => {
        if (chartDetailsElem.phenomenon.unitMeasName === dsToRemove.unitMeasName) {
            indexChart = chartDetailsIndex;
            chartDetails = chartDetailsElem;
            return;
        }
    });

    if (!this.datastreams.some( (ds) => ds.unitMeasName === dsToRemove.unitMeasName)) { // no more datastreams with this phenomenon --> removing chart
        this.chartsDetails.splice(indexChart, 1);
    } else { // removing one station series in chart
        const measurementPhenom = this.measurementsPhenom.find(measPhenom => measPhenom.phenomenon.label === chartDetails.phenomenon.unitMeasName);
        if (measurementPhenom != undefined) {
            measurementPhenom.measurementsStation.forEach( (measStation, indexStation) => {
                if (measStation.station === dsToRemove.id.toString()) {
                    this.chartsDetails[indexChart].chart.series.removeIndex(indexStation).dispose();
                    measurementPhenom.measurementsStation.splice(indexStation, 1);
                    return;
                }
            });
        }
    }

    if (this.datastreams.length == 0 && homeNavigation) {
        this.router.navigate(['/home']);
    }
}

  selectOrDeselectDatastreams(datastream: TimeIODatastream, i: number) {
    let chartIndex: number;
    this.chartsDetails.forEach( (chartDetailsElem, index) => {
        if (chartDetailsElem.phenomenon.unitMeasName === datastream.unitMeasName) {
            chartIndex = index;
        }
    });

    let indexStation = -1;
    const measPhenomenon = this.measurementsPhenom.find(elem =>
        elem.phenomenon.label === datastream.unitMeasName
    );
    measPhenomenon?.measurementsStation.forEach( (measStation, index) => {
        if (measStation.station === datastream.id.toString()) {
            indexStation = index;
        }
    });

    if (datastream.color == null) {
      datastream.color = this.colors[i % this.colors.length];
        this.scrollToElement(datastream.unitMeasName);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).fill = am4core.color(datastream.color);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).stroke = am4core.color(datastream.color);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).strokeWidth = 3;
    } else {
        //@ts-ignore
        datastream.color = null;

        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).fill = am4core.color(this.colors[indexStation % this.colors.length]);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).stroke = am4core.color(this.colors[indexStation % this.colors.length]);
        //@ts-ignore
        this.chartsDetails[chartIndex].chart.series.getIndex(indexStation).strokeWidth = 1;
    }
}

scrollToElement(elementId: string): void {
  const container = this.graphScroll.nativeElement;
  const targetElement = document.getElementById(elementId);
  if (targetElement) {
    const isInsideContainer = container.contains(targetElement);
    if (isInsideContainer) {
      targetElement.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest',
      });
    }
  }
}

// group measurements requests with same phenomenom
  prepareRequestsForMeasurements(datastreams: TimeIODatastream[], selectedDateInterval: any = null): MeasurementRequestsPhenomenon[] {
    const measurementsRequestsPhenomenon: MeasurementRequestsPhenomenon[] = [];

    // go through new datastreams and check if exists in this.datastreams (global list)
    // if exists in global list - delete that chart and add new chart with both (or more) datastreams with same phenomenon

    const globallyExistingSamePhenomDsList = this.datastreams.filter(existingDs => 
      datastreams.some(newDs => (newDs.unitMeasName === existingDs.unitMeasName && newDs.id !== existingDs.id))
    );

    this.datastreams.push(...datastreams);

    if (globallyExistingSamePhenomDsList && globallyExistingSamePhenomDsList.length > 0) {

      globallyExistingSamePhenomDsList.forEach(existingDs => {
        existingDs.color = null;
      });

      // add ds with same phenomenon (with new ones) to new datastreams for further processing
      datastreams.push(...globallyExistingSamePhenomDsList);
      
      // delete charts for globallyExistingPhenomDsList
      this.chartsDetails = this.chartsDetails.filter(chartDetails => 
        !globallyExistingSamePhenomDsList.some(existingDs => existingDs.unitMeasName === chartDetails.phenomenon.unitMeasName)
      );

      // remove measurementsPhenom with that phenomenons
      this.measurementsPhenom = this.measurementsPhenom.filter(measPhenom => 
        !globallyExistingSamePhenomDsList.some(existingDs => existingDs.unitMeasName === measPhenom.phenomenon.label)
      );
    }

    datastreams?.forEach(ds => {
          const measReq: MeasurementRequest = new MeasurementRequest();
          measReq.timeseriesId = ds.id;

          if (!selectedDateInterval) {
            selectedDateInterval = { startDate: moment().subtract(3, 'months').hours(0).minutes(0).seconds(0), endDate: moment() };
          }
          
          measReq.dateFrom = selectedDateInterval.startDate.toDate();
          measReq.dateTo = selectedDateInterval.endDate.toDate();

          const existingMeasReqPhenom = measurementsRequestsPhenomenon.find(elem => elem.phenomenonId === ds.unitMeasName);

          if (existingMeasReqPhenom != null) {
              existingMeasReqPhenom.measurementRequests.push(measReq);
          } else {
              const chartDetails: ChartDetails = new ChartDetails();
              chartDetails.phenomenon = ds;

              chartDetails.selectedDateInterval = selectedDateInterval;
              
              chartDetails.firstDate = selectedDateInterval.startDate.toDate();
              chartDetails.lastDate = selectedDateInterval.endDate.toDate();

              const newMeasReqPhenom: MeasurementRequestsPhenomenon = new MeasurementRequestsPhenomenon();
              newMeasReqPhenom.phenomenonId = ds.unitMeasName;
              newMeasReqPhenom.measurementRequests = [];
              newMeasReqPhenom.measurementRequests.push(measReq);

              measurementsRequestsPhenomenon.push(newMeasReqPhenom);

              this.chartsDetails.push(chartDetails);
          }
    });

    return measurementsRequestsPhenomenon;
  }

  // list element is measRequest list with same phenomenon
  // go through list and call backend
  async getMeasurements(measurementsRequestPhenom: MeasurementRequestsPhenomenon[]) {
    for (const elem of measurementsRequestPhenom) {
        const response = await this.sharedService.post('timeio/observation/getObservations', elem);
              if (response.status === 200) {
                  // list where element is {measurement list and station name} - same phenomenon
                  if (response.entity && response.entity.length > 0) {
                    const measResponses: MeasurementsODTO[] = response.entity;
                    const measurementsWithSamePhenom: MeasurementsPhenomenon = new MeasurementsPhenomenon();
                    measurementsWithSamePhenom.measurementsStation = [];
                    measResponses.forEach((measResponse) => {
                        const measStationOnePhenom: MeasurementsStationOnePhenom = new MeasurementsStationOnePhenom();

                        measResponse.measurements.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
                        measResponse.measurements = measResponse.measurements.filter((item, index, self) => 
                          index === self.findIndex((t) => t.date === item.date && t.value === item.value)
                        ); // remove duplicates

                        measStationOnePhenom.measurements = measResponse.measurements; 
                        measStationOnePhenom.station = measResponse.station; // datastream not station
                        measStationOnePhenom.timeSeriesId = measResponse.timeseriesId;
                        measStationOnePhenom.uom = measResponse.uom;

                        measurementsWithSamePhenom.measurementsStation.push(measStationOnePhenom);

                        measurementsWithSamePhenom.phenomenon = measResponse.phenomenon;
                    });
                    this.measurementsPhenom.push(measurementsWithSamePhenom);
                    this.initializeGraph(measurementsWithSamePhenom, elem.measurementRequests);
                  } else { // no data
                    // TODO timeio - show diagram but write that there are no data and to chose another date interval
                    let datastreamToRemove = new TimeIODatastream();
                    datastreamToRemove.id = elem.measurementRequests[0].timeseriesId;
                    datastreamToRemove.unitMeasName = elem.phenomenonId
                    
                    this.datastreams.forEach(ds => { 
                      if (ds.id == datastreamToRemove.id) {
                        ds.showed = false; 
                        ds.checked = false;
                      }
                    });
                    this.removeOneDatastream(datastreamToRemove);
                    this.toastr.warning('No measurements for ' + datastreamToRemove.name + ' ( ' + datastreamToRemove.unitMeasName + ')' + 'for selected date interval.', 'Warning!');
                  }
              }
          }
    }


  getMeasurementsForDate(i: number, phenomenon: string) {
    let chartDetails: any = null;
    this.chartsDetails.forEach(chartDetailsElem => {
        if (chartDetailsElem.phenomenon.unitMeasName === phenomenon) {
            chartDetails = chartDetailsElem;
        }
    });
    chartDetails.waitingIndicator.show();
    const datastreamsForDateRefresh = this.datastreams.filter(ds =>
      ds.unitMeasName === phenomenon && ds.showed === true
    );

    const dateInterval = chartDetails.selectedDateInterval;

    datastreamsForDateRefresh.forEach(ds => {
      this.removeOneDatastream(ds, false);
    });

    const measurementsRequestsPhenomenon: MeasurementRequestsPhenomenon[] = this.prepareRequestsForMeasurements(datastreamsForDateRefresh, dateInterval);

    this.getMeasurements(measurementsRequestsPhenomenon);
    chartDetails.waitingIndicator.hide();

  }

  
  initializeGraph(measurementsWithSamePhenom: MeasurementsPhenomenon, measurementRequests: MeasurementRequest[]) {
    this.zone.runOutsideAngular(() => {
        const findChart =  this.chartsDetails.find(chartDetailsElem =>
            chartDetailsElem.phenomenon.unitMeasName === measurementsWithSamePhenom.phenomenon.label
        );
        let chartDetails: ChartDetails;

        if (findChart != null) {
          chartDetails = findChart;
        

          const chart = am4core.create(measurementsWithSamePhenom.phenomenon.label + '', am4charts.XYChart);
          if (chartDetails.chart) {
            chartDetails.chart.dispose();
          }
          chartDetails.chart = chart;

          chart.paddingRight = 20;
          chart.preloader.disabled = false;

          this.showIndicator(chartDetails);

          const scrollbarX = new am4charts.XYChartScrollbar();

          // colored series in scrollbar
          scrollbarX.scrollbarChart.plotContainer.filters.clear();

  //         const precipitation = measurementsWithSamePhenom.phenomenon.label.includes('precipitation');

          const dateAxis = chart.xAxes.push(new am4charts.DateAxis());


          dateAxis.periodChangeDateFormats.setKey('day', 'MMM dd');
          dateAxis.periodChangeDateFormats.setKey('month', 'MMM[/] [bold]yyyy');
          dateAxis.dateFormats.setKey('month', 'MMM yyyy');
          dateAxis.dateFormats.setKey('day', 'MMM dd');

          const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

          if (valueAxis.tooltip != null && valueAxis.tooltip != undefined) {
            valueAxis.tooltip.disabled = true;
          }

          let counter = 0;
          let stations = []; // za neponavljanje istih stanica u Legendi
          measurementsWithSamePhenom.measurementsStation.forEach((elem, i) => {
              const seriesData = [];
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

              const dateField = "date";
              const valueField = "value";

              elem.measurements.forEach(meas => {
                  const dataObject = {};
                  dataObject[dateField] = new Date(meas.date);
                  dataObject[valueField] = meas.value;

                  seriesData.push(dataObject);
              });

              let series;
  //             if (precipitation) {
  //                 series = chart.series.push(new am4charts.ColumnSeries());
  //             } else {
                  series = chart.series.push(new am4charts.LineSeries());
  //             }
              series.dataFields.dateX =  dateField;
              series.dataFields.valueY = valueField;


              const currentTs = this.datastreams.find(ts => ts.id === elem.timeSeriesId);
              if (currentTs != null && currentTs.color == null) {
                  series.fill = am4core.color(this.colors[counter % this.colors.length]);
                  series.stroke = am4core.color(this.colors[counter % this.colors.length]);
              } else if (currentTs != null) {
                  series.fill = am4core.color(currentTs.color);
                  series.stroke = am4core.color(currentTs.color);
                  series.strokeWidth = 3;
              }

              series.tooltipText = "Value: {valueY.value} " + measurementsWithSamePhenom.measurementsStation[0].uom + "\nDate: {dateX.formatDate('dd MMM yyyy HH:mm')}";

  //             if (!precipitation) {
                  // const bullet = series.bullets.push(new am4charts.CircleBullet());
                  // bullet.circle.fill = am4core.color('white');
                  // bullet.circle.strokeWidth = 1.5;
                  // bullet.circle.radius = 4;
  //             }

              series.name = elem.station;
              series.connect = false;


              series.baseInterval = { timeUnit: "minute", count: 1 };
              series.autoGapCount = true;
              series.autoGapCount = 60;

              series.tensionX = 0.8; // rounded lines (series)

              if (!stations.includes(elem.station)) {
                stations.push(elem.station);
              }

              series.data = seriesData;

              scrollbarX.series.push(series);
              counter++;
        });

        chart.legend = new am4charts.Legend();
        chart.legend.parent = chart.chartContainer;
        chart.legend.zIndex = 100;
        chart.legend.contentAlign = 'right';
        chart.legend.contentValign = 'top';
        chart.zoomOutButton.align = 'left';
        chart.zoomOutButton.valign = 'bottom';
        chart.zoomOutButton.marginLeft = 10;
        chart.zoomOutButton.marginBottom = 10;

        chart.zoomOutButton.events.on('hit', (event) => {
            chartDetails.rangeDateMin = chartDetails.firstDate;
            chartDetails.rangeDateMax = chartDetails.lastDate;
        });

        scrollbarX.events.on('propertychanged', event => {
            dateAxis.zoomToDates(chartDetails.rangeDateMin, new Date(chartDetails.rangeDateMax.getTime() + (1000 * 60 * 60 * 24)));
            chartDetails.selectedDateInterval = { startDate: moment(chartDetails.firstDate.getTime()),
                                                  endDate: moment(chartDetails.lastDate.getTime())};
        });

        // TODO timeio
        // scrollbarX.events.on('swipeleft', async event => {
        //     chartDetails.waitingIndicator.show();
        //     let dataNew: any[] = [];

        //     const dateFrom = dateAxis.minZoomed;
        //     const dateTo = dateAxis.maxZoomed;
        //     chartDetails.rangeDateMin = new Date(dateFrom);
        //     chartDetails.rangeDateMax = new Date(dateTo);

        //     const rangeBetweenDates = (dateAxis.maxZoomed - dateAxis.minZoomed);

        //     const beforeDate = new Date(chartDetails.firstDate.getTime() - (rangeBetweenDates * 2));

        //     const newMeasurementRequests = this.prepareNewMeasurementsRequest(measurementRequests, beforeDate, chartDetails.firstDate);

        //     const response = await this.sharedService.post('sos/getMeasurements',newMeasurementRequests);
            
        //     if (response.status === 200) {
        //       const newMeasurmentsResponse = response.entity;
        //       chartDetails.firstDate = new Date(newMeasurmentsResponse[0].measurements[0].date);
        //       let dataFromDiagram:any[] = chart.data;
        //       dataNew = this.getNewMeasurments(newMeasurmentsResponse, scrollbarX);
        //       let dataChart: any = [];
        //       dataChart = dataNew.concat(dataFromDiagram)
        //       chart.data = dataChart;
        //   }
        //   chartDetails.waitingIndicator.hide();

        // });

        // TODO timeio
        // scrollbarX.events.on('swiperight', async event => {
        //     chartDetails.waitingIndicator.show();
        //     let dataNew: any[] = [];

        //     const dateFrom = dateAxis.minZoomed;
        //     const dateTo = dateAxis.maxZoomed;

        //     chartDetails.rangeDateMin = new Date(dateFrom);
        //     chartDetails.rangeDateMax = new Date(dateTo);

        //     const rangeBetweenDates = (dateAxis.maxZoomed - dateAxis.minZoomed);

        //     const afterDate = new Date(chartDetails.lastDate.getTime() + (rangeBetweenDates * 2));
        //     const newMeasurementRequests = this.prepareNewMeasurementsRequest(measurementRequests, chartDetails.lastDate, afterDate);

        //     const response = await this.sharedService.post('sos/getMeasurements',newMeasurementRequests);

        //     if (response.status === 200) {
        //       const newMeasurmentsResponse = response.entity;

        //       if (newMeasurmentsResponse[0].measurements.length > 0) {
        //           chartDetails.lastDate = new Date(newMeasurmentsResponse[0].measurements[newMeasurmentsResponse[0].measurements.length - 1].date);

        //           dataNew = this.getNewMeasurments(newMeasurmentsResponse, scrollbarX);
        //           const dataFromDiagram = chart.data;
        //           let dataChart: any[] = [];
        //           dataChart = dataFromDiagram.concat(dataNew);
        //           chart.data = dataChart;
        //       }

        //   }
        //   chartDetails.waitingIndicator.hide();
        // });

        chart.dateFormatter.dateFormat = 'yyyy-MM-dd';

        chart.id = measurementsWithSamePhenom.phenomenon.label + '';  
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

        chart.events.on('ready', event => {
            chartDetails.waitingIndicator.hide();
        });
      }
    });
  }

//   private getNewMeasurments(newMeasurmentsResponse: MeasurementsODTO[], scrollbarX) {
//     const dataNew = [];

//     const newMeasurementsWithSamePhenom: MeasurementsPhenomenon = new MeasurementsPhenomenon();
//     newMeasurementsWithSamePhenom.measurementsStation = [];
//     newMeasurmentsResponse.forEach((measResponse) => {
//         const newMeasStationOnePhenom: MeasurementsStationOnePhenom = new MeasurementsStationOnePhenom();

//         newMeasStationOnePhenom.measurements = measResponse.measurements;
//         newMeasStationOnePhenom.station = measResponse.station;
//         newMeasStationOnePhenom.uom = measResponse.uom;

//         newMeasurementsWithSamePhenom.measurementsStation.push(newMeasStationOnePhenom);
//         newMeasurementsWithSamePhenom.phenomenon = measResponse.phenomenon;
//     });


//     newMeasurementsWithSamePhenom.measurementsStation.forEach((measStation, i) => {
//         const dateField1 = 'Date of measurment';
//         const valueField1 = measStation.station + ' (value in ' + measStation.uom + ')';
//         measStation.measurements.forEach(meas => {
//             const dataObject = {};
//             dataObject[dateField1] = new Date(meas.date);
//             dataObject[valueField1] = meas.value;

//             //@ts-ignore
//             dataNew.push(dataObject);
//         });

//         const series2 = scrollbarX.series.getIndex(i);

//         series2.dataFields.dateX = dateField1 + '';
//         series2.dataFields.valueY = valueField1 + '';
//         scrollbarX.series.push(series2);
//     });

//     return dataNew;
//   }

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

//   private prepareNewMeasurementsRequest(measurementRequests, dateFrom, dateTo) {
//     const newMeasurementRequests: MeasurementRequest[] = [];

//     measurementRequests.forEach(measurementRequest => {
//         const newMeasurementRequest = new MeasurementRequest();
//         newMeasurementRequest.dateFrom = dateFrom;
//         newMeasurementRequest.dateTo = dateTo;
//         newMeasurementRequest.timeseriesId = measurementRequest.timeseriesId;

//         newMeasurementRequests.push(newMeasurementRequest);
//     });

//     return newMeasurementRequests;
//   }

  btn_navigateToHome() {
      this.datastreams.forEach(ds => {
        ds.color = null;
      });
      this.router.navigate(['home']);
  }

  openOffsidebar(){
    if (this.settings.getLayoutSetting('offsidebarOpen')) {
        this.settings.toggleLayoutSetting('offsidebarOpen');
    }
  }

}
