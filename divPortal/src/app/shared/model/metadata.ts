import { Author } from "./author"
import { Contributor } from "./contributor";
import { DatasetId } from "./dataset-id";
import { Description } from "./description";
import { File } from "./file";
import { Keyword } from "./keyword";
import { License } from "./license";
import { Project } from "./project";
import { PropertyRight } from "./property-right";
import { ShortName } from "./short-name";
import { SOReference } from "./so-reference";
import { TaxonomicCoverage } from "./taxonomic-coverage";

export class Metadata {

    soReference: SOReference;
    dataLevel: number;
    language: string;
    project: Project;

    authors: Author[];
    contributors: Contributor[];
    datasetIds: DatasetId[];
    descriptions: Description[];
    files: File[];
    keywords: Keyword[];
    licenses: License[];
    propertyRights: PropertyRight[];
    shortNames: ShortName[];
    taxonomicCoverages: TaxonomicCoverage[];

    constructor() { }

}