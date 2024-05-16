import { Response } from 'src/app/shared/model/response-db';

export class ActivityResponse extends Response {

    entity: ActivityDTO[];

}

export class ActivityDTO {

    name: string;
    id: number;

    constructor() { }

}
