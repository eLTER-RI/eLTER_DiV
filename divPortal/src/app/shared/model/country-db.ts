import { Response } from 'src/app/shared/model/response-db';

export class CountryResponse extends Response {

    entity: CountryDTO[];
}

export class CountryDTO {

    name: string;
    id: number;

    constructor() { }

}
