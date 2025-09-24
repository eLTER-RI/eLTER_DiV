import { Point } from "./point-db";
import { TimeIOThing } from "./timeio-thing";

export class TimeIOLocation {

    id: number

    idIot: number;
    name: string;
    
    coordinates: Point;

    things: TimeIOThing[];

    open: boolean = false;

    constructor() { }

}