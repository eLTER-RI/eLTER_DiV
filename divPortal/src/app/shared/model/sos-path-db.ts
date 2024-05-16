import { Response } from 'src/app/shared/model/response-db';

export class SosPathResponse extends Response {

    entity: SosPath[];

}

export class SosPath {

    id: number;
    url: string;
    title: string;

    constructor() { }

}
