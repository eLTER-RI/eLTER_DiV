import { SpatialScope } from './spatial-scope';
import { EntityType } from './entity-type';
import { EbvName } from './ebv-name';
import { EbvClass } from './ebv-class';


export class EbvFilter {
    
    creator: string;
    date: Date;
    ebvClass: EbvClass;
    ebvName: EbvName;
    entityType: EntityType;
    spatialScope: SpatialScope;

    constructor() { 
    }

}