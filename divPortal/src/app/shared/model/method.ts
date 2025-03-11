import { Sampling } from "./sampling";
import { Step } from "./step";

export class Method {

    methodID: string;
    qualityControlDescription: string;
    instrumentationDescription: string;

    sampling: Sampling;

    steps: Step[];

    constructor() {}

}