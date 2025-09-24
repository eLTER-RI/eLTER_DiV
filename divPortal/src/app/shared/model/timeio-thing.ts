import { TimeIODatastream } from "./timeio-datastream";
import { TimeIOLocation } from "./timeio-location";

export class TimeIOThing {

    id: number;

    idIot: number;
    name: string;
    description: string;

    open: boolean;

    fkLocation: TimeIOLocation;

    fkDatastreams: TimeIODatastream[];

    constructor() { }

}