import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {FullTest} from "./entity/FullTest";
import {environment} from "../environments/environment";
import {Page} from "./entity/Page";

@Injectable({
  providedIn: 'root'
})
export class TestService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public createTest(test: FullTest): Observable<FullTest> {
    return this.http.post<FullTest>(`${this.apiServerUrl}/test`, test);
  }

  public getTest(id: number): Observable<FullTest> {
    return this.http.get<FullTest>(`${this.apiServerUrl}/test/${id}`);
  }

  public fetchPage(programmingLang: string, index: number, size: number): Observable<Page> {
    let params = new HttpParams().set('programmingLang', programmingLang).set('index', index).set('size', size);
    return this.http.get<Page>(`${this.apiServerUrl}/test`, {params: params});
  }

  public updateTest(id: number, test: FullTest): Observable<void> {
    return this.http.put<void>(`${this.apiServerUrl}/test/${id}`, test);
  }

  public deleteTest(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/test/${id}`);
  }
}
