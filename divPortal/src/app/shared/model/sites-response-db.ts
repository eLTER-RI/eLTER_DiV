import { BoundingBox } from '../bounding-box-db';
import { Site } from '../site-db';
import { Response } from 'src/app/shared/model/response-db';

export class SitesResponse extends Response {

    entity: SitesDTO;

}

export class SitesDTO {

    boundingBox: BoundingBox;
    sites: Site[];

    constructor() { }
}
