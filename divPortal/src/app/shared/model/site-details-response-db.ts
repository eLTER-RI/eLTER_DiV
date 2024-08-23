import { Response } from 'src/app/shared/model/response-db';

export class SiteDetailsResponse extends Response {

    entity: SiteDetails;

}

export class SiteDetails {

    id: string;
    description: string;
    activities: string;
    polygon: string;
    title: string;
    shortName: string;
    siteName: string;
    siteType: string;
    keywords: string;
    country: string;
    yearEstablished: string;

    experimentsDesign: string;
    experimentsScale: string;
    observationsDesign: string;
    observationsScale: string;

    changed: Date;

    geographic: GeographicDTO;
    status: UrlNameDTO;
    contact: ContactDTO;
    infrastructure: InfrastructureDTO;
    environmentalCharacteris: EnvironmentalCharacterisDTO;

    observingCapabilities: UrlNameDTO[];
    belongsTo: UrlNameDTO[];
    projects: UrlNameDTO[];
    relatedResources: UrlNameDTO[];
    protectionLevel: UrlNameDTO[];

    open: boolean = false;

    constructor() { }
}

export class GeographicDTO {

    elevationAvg: number;
	elevationMax: number;
	elevationMin: number;
	elevationUnit: string;
	
	sizeValue: number;
    sizeUnit: string;
    
    constructor() { }
}

export class ContactDTO {
    siteManagers: ContactResponsibleDTO;
    metadataProviders: ContactResponsibleDTO;

    operatingOrganisation: NameTypeDTO[];
    fundingAgency: NameTypeDTO[];
    siteUrl: UrlNameDTO[];

    constructor() { }
}

export class UrlNameDTO {

    name: string;
    url: string;

     constructor() { }
}


export class ContactResponsibleDTO {

    name: string;
    type: string;
    orcid: string;
    email: string;

     constructor() { }
}

export class InfrastructureDTO {

    accessibleAllYear: boolean;
    accessType: string;
    allPartsAccessible: boolean;
    maintenanceInterval: string;
    permanentPowerSupply: boolean;
    notes: string;
    collections: string;
    operationPermanent: boolean;
    operationNotes: string;
    operationSiteVisitInterval: string;

    policy: PolicyDTO;

    constructor() { }
}

export class PolicyDTO {

    url: string;
    notes: string;
    rights: string;

    constructor() { }

}

export class EnvironmentalCharacterisDTO {

    airTemperatureAvg: number;
    airTemperatureMin: number;
    airTemperatureMax: number;
    airTemperatureUnit: string;

    precipitationAnnual: number;
    precipitationMin: number;
    precipitationMax: number;
    precipitationUnit: string;

    biogeographicalRegion: string;
    biome: string;
    landforms: string;
    geoBonBiome: string;
    geology: string;
    hydrology: string;
    soils: string;
    vegetation: string;

    ecosystemAndLanduse: string;
    eunisHabitat: string;

    constructor() { }
}

export class NameTypeDTO {

    name: string;
    type: string;

     constructor() { }
}


