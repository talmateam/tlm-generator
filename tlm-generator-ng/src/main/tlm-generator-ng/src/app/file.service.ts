import {Injectable} from '@angular/core';
import {HttpResponse,HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import { urlBase } from '../app/commons';

@Injectable({ providedIn: 'root' })
export class FileService {

  constructor(private http: HttpClient) {}

  downloadFile(data): Observable<HttpResponse<Blob>>{
    return this.http.post(urlBase+'generadorapi/generator', data,{observe: 'response', responseType: 'blob'});
   }
}
