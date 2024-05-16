export class ResourceIdentifier {
    code: string;
  }
  
  export class Keyword {
    value: string;
  }
  
  export class ResponsibleParty {
    individualName: string;
    organisationName: string;
    role: string;
    email: string;
  }
  
  export class OnlineResource {
    url: string;
    name: string;
    description: string;
    function: string;
    type: string;
  }
  
  export class SupplementalResource {
    name: string;
    description: string;
    url: string;
  }
  
  export class DatasetReferenceDate {
    creationDate: string;
  }
  
  export class ResourceType {
    value: string;
  }
  
  export class AccessLimitation {
    value: string;
    code: string;
    uri: string;
  }
  
  export class DeimsSite {
    title: string;
    id: string;
    url: string;
  }
  
  export class DarDB {
    id: string;
    uri: string;
    type: string;
    title: string;
    description: string;
    metadataDate: string;
    resourceIdentifiers: ResourceIdentifier[];
    keywords: Keyword[];
    dataLevel: string;
    responsibleParties: ResponsibleParty[];
    onlineResources: OnlineResource[];
    supplemental: SupplementalResource[];
    datasetReferenceDate: DatasetReferenceDate;
    resourceType: ResourceType;
    accessLimitation: AccessLimitation;
    deimsSites: DeimsSite[];
    linkedDocument: boolean;
    importId: string;
    importLastModified: string;
    mapViewable: boolean;
    incomingCitationCount: number;
    resourceStatus: string;
    authors: ResponsibleParty[];

  }
  