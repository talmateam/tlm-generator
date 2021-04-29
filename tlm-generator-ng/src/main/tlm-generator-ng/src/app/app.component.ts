import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {urlBase} from '../app/commons';

export interface State {
  id: string;
  name: string;
  icon: string;
  flagAdd:boolean
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private urlapi = urlBase + 'generadorapi/';
  public appsTemplates: any = null;
  constructor(private httpClient: HttpClient) {}

  ngOnInit() {
    this.getAppsTemplates();
  }

  private getAppsTemplates() {
    this.httpClient
      .get(this.urlapi)
      .subscribe(apiData => {this.appsTemplates = apiData;console.log('my data 1',this.appsTemplates);});
  }
  title = 'generatorapp';
  templateSel:any;
  selectTemplate(apli){
    const template = this.appsTemplates.templates.filter(app => app.name == apli)[0];
    console.log('my date 2',template);
    this.templateSel= this.formatData(template);
    console.log('my date 3',this.templateSel);
  }
  formatData(template){
    const data = template.groups;
    for (let i in data) {
      for (let key in data[i].properties) {
        if (data[i].properties[key].inputType=='checkbox') {
          if (data[i].properties[key].defaultValue=='false') {
            data[i].properties[key].defaultValue= false;
          }
          if (data[i].properties[key].defaultValue=='true') {
            data[i].properties[key].defaultValue= true;
          }
        }

      }
    }
    template.groups=data;
    return template;
  }
}
