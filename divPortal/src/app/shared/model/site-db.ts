import { Point } from './point-db';

export class Site {

    title: string;
    id: number;
    idSuffix: string;
    changed: Date;
    point: Point;
    markerIndex: number;

    constructor() { }
}
