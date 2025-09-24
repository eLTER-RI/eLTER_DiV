import { TimeIOThing } from "./timeio-thing";

export class TimeIODatastream {

    id: number;
    idIot: number;
    name: string;
    description: string;
    unitMeasName: string;
    unitMeasSymbol: string;

    fkThing: TimeIOThing;

    color: string;

    checked: boolean = false;
    showed: boolean = false;

    constructor() { }

}