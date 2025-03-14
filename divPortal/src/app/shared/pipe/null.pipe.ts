import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: 'na'
})
export class NullPipe implements PipeTransform {
    transform(value: any, defaultValue: string = 'N/A'): string {
      return value === null || value === undefined || value === '' ? defaultValue : value;
    }
}