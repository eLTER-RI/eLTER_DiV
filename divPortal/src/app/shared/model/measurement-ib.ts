export class MeasurementRequestsPhenomenon {
    phenomenonId: number;
    measurementRequests: MeasurementRequest[];

    constructor() { }
}

export class MeasurementRequest {

    timeseriesId: number;
    dateFrom: Date;
    dateTo: Date;

    constructor() { }
}
