import {Component, Inject, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {FileService} from '../../file.service';
import * as fileSaver from 'file-saver';
import {MAT_SNACK_BAR_DATA, MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'addapp',
  templateUrl: './addapp.component.html',
  styleUrls: ['./addapp.component.css']
})
export class AddappComponent {
  @Input()
  template:any;
  durationInSecondsNotifi = 5;
  notification = {status:'',message:''};
  constructor(private fileService: FileService,private _snackBar: MatSnackBar) {}
  indeterminate = false;
  spinerstate= false;
  depAdd=[];
  depCheckAdd=[];
  title = 'generatorapp';
  //urlReadme = "https://github.com/btford/angular-markdown-directive/blob/master/README.md";
  objvalidate:any;

  stateCtrl = new FormControl();
  //camporequerido = new FormControl('', [Validators.required]);

  ngOnInit() {

  }
  sendData(){
    this.spinerstate=true;
    const templ = this.getAppsTemplates();
    console.log("todos llenos:",templ);
    //this.validateValues(templ);
    if (this.validateValues(templ)) {
      this.fileService.downloadFile(templ).subscribe(
        response => {
          fileSaver.saveAs(response.body, this.template.name+'.zip');
          this.spinerstate= false;
          this.notification.status='success'
          this.notification.message='Proyecto generado'
          this.openSnackBar(this.notification);
        },
        err =>{
          this.spinerstate= false;
          this.notification.status='error'
          this.notification.message='Oops! Ocurrio un problema'
          this.openSnackBar(this.notification);
        },
        () => {
          console.log('yay');
        }
      )
    }else{
      this.spinerstate=false;
      this.notification.status='error'
      this.notification.message='Todos los campos son requeridos o no tiene formato adecuado'
      this.openSnackBar(this.notification);
    }
  }
  private getAppsTemplates() {
    let propTemplate = new Object();
    const data = this.template.groups;
    for (let i in data) {
      for (let key in data[i].properties) {
        let name = data[i].properties[key].name;
        let value = data[i].properties[key].defaultValue;
        propTemplate[name] = value
        if (data[i].properties[key].childs.length>0) {
          for (let j in data[i].properties[key].childs) {
            let name = data[i].properties[key].childs[j].name;
            let value = data[i].properties[key].childs[j].defaultValue;
            propTemplate[name] = value
          }
        }
      }
    }
    propTemplate["gitUrl"]=this.template.gitUrl;
    return propTemplate;
  }
  changeEvent(defVal,name,checkVal){
    if (checkVal==false) {
      return name
    }else{
      return defVal
    }
  }
  validateValues(tem) {
    let form = document.getElementById("conten-app-form") as HTMLFormElement;
    if (!form.checkValidity()) {
      form.reportValidity();
      console.log("checkValidity: false");
      return false;
    }
    for (let i in tem) {
      if (tem[i].length<1 || tem[i] == null) {
        console.log("one elment is null" + tem[i]);
        return false;
      }
    }
    return true;
  }
  openSnackBar(message) {
    this._snackBar.openFromComponent(NotificationComponent, {
      data: message,
      duration: this.durationInSecondsNotifi * 2000,
    });
  }
}
/*********************Notification Component********************** */
@Component({
  selector: 'notification',
  templateUrl: './notification.component.html',
  styles: [`
    .example-pizza-party {
      color: hotpink;
    }
  `],
})
export class NotificationComponent {
  constructor( @Inject(MAT_SNACK_BAR_DATA) public data: any) { }
}
