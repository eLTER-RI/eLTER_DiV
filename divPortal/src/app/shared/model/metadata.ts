import { Creator } from "./creator"
import { Contributor } from "./contributor";
import { Description } from "./description";
import { Keyword } from "./keyword";
import { License } from "./license";
import { Project } from "./project";
import { TaxonomicCoverage } from "./taxonomic-coverage";
import { ResponsibleOrganization } from "./responsible-organization";
import { Method } from "./method";
import { ContactPoint } from "./contact-point";
import { HabitatReference } from "./habitat-reference";
import { AdditionalMetadata } from "./additional-metadata";

export class Metadata {

    dataLevel: number;
    language: string;

    projects: Project[];
    creators: Creator[];
    contributors: Contributor[];
    descriptions: Description[];
    keywords: Keyword[];
    licenses: License[];
    taxonomicCoverages: TaxonomicCoverage[];
    responsibleOrganizations: ResponsibleOrganization[];
    contactPoints: ContactPoint[];
    methods: Method[];
    habitatReferences: HabitatReference[];
    additionalMetadatas: AdditionalMetadata[];

    constructor() { }

}