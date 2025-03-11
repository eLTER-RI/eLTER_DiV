import { Creator } from "./creator";
import { Contributor } from "./contributor";
import { Metadata } from "./metadata";
import { Site } from "./site-db";

export class Dataset {

    id: string;
    title: string;

    sites: Site[];

    metadata: Metadata;

    open: boolean = false;

    constructor() {}

}