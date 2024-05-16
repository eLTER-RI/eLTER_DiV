export class EbvDetail {
    
    id: number;
    namingAuthority: string;
    title: string;
    dateCreated: string;
    summary: string;
    references: string [];
    source: string;
    coverageContentType: string;
    processingLevel: string;
    project: string;
    projectUrl: string[];
    creator: Creator;
    contributorName: string;
    license: string;
    publisher: Publisher;
    ebv: Ebv;
    ebvEntity: EbvEntity;
    ebvMetric: EbvMetric;
    ebvSpatial: EbvSpatial;
    geospatialLatUnits: string;
    geospatialLonUnits: string;
    timeCoverage: TimeCoverage;
    ebvDomain: string;
    comment: string;
    dataset: Dataset;
    file: File;

}

export class Creator {
    creatorName: string;
    creatorEmail: string;
    creatorInstitution: string;
    creatorCountry: string;
}

export class Publisher {
    publisherName: string;
    publisherEmail: string;
    publisherInstitution: string;
    publisherCountry: string;
}

export class EbvMetric {
    metric0: Metric;
    metric1: Metric;
}

export class Metric {
    name: string;
    description: string;
    unit: string;
}

export class EbvEntity {
    ebvEntityType: string;
    ebvEntityScope: string;
    ebvEntityClassificationName: string;
    ebvEntityClassificationUrl: string;
}

export class Ebv {
    ebvClass: string;
    ebvName: string;
}

export class EbvSpatial {
    ebvSpatialScope: string;
    ebvSpatialDescription: string;
}

export class TimeCoverage {
    timeCoverageResolution: string;
    timeCoverageStart: string;
    timeCoverageEnd: string;
}

export class Dataset {
    pathname: string;
    download: string;
    metadataJson: string;
    metadataXml: string;
}

export class File {
    download: string;
}