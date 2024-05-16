import { Phenomenon } from './phenomenon-db';

export class Timeseries {

    label: string;
    lastValue: number;
    lastValueDate: Date;
    firstValue: number;
    firstValueDate: Date;
    uom: string;
    procedure: string;
    observedProperty: string;
    id: number;
    phenomenon: Phenomenon;

    constructor() { }

}
