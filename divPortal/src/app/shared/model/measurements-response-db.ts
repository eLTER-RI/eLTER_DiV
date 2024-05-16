import { Phenomenon } from './phenomenon-db';
import { Measurement } from './measurement';
import { Response } from 'src/app/shared/model/response-db';


export class MeasurementResponse extends Response {

    entity: MeasurementsODTO[];

}

export class MeasurementsODTO {

    measurements: Measurement[];
    phenomenon: Phenomenon;
    uom: string;
    station: string;
    procedure: string;
    timeseriesId: number;

    constructor() { }

}

