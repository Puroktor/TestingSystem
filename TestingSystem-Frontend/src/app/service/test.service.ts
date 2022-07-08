import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {FullTest} from "../entity/FullTest";
import {environment} from "../../environments/environment";
import {Page} from "../entity/Page";

@Injectable({
  providedIn: 'root'
})
export class TestService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public createTest(test: FullTest, token: string): Observable<FullTest> {
    let headers = new HttpHeaders().set('Authorization', token);
    return this.http.post<FullTest>(`${this.apiServerUrl}/test`, test, {headers:headers});
  }

  public getShuffledTest(id: number, token: string): Observable<FullTest> {
    let headers = new HttpHeaders().set('Authorization', token);
    return this.http.get<FullTest>(`${this.apiServerUrl}/test/shuffled/${id}`, {headers:headers});
  }

  public getTest(id: number, token: string): Observable<FullTest> {
    let headers = new HttpHeaders().set('Authorization', token);
    return this.http.get<FullTest>(`${this.apiServerUrl}/test/${id}`, {headers:headers});
  }

  public fetchPage(programmingLang: string, index: number, size: number): Observable<Page> {
    let params = new HttpParams().set('programmingLang', programmingLang).set('index', index).set('size', size);
    return this.http.get<Page>(`${this.apiServerUrl}/test`, {params: params});
  }

  public updateTest(id: number, test: FullTest, token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    return this.http.put<void>(`${this.apiServerUrl}/test/${id}`, test, {headers:headers});
  }

  public deleteTest(id: number, token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    return this.http.delete<void>(`${this.apiServerUrl}/test/${id}`, {headers:headers});
  }
}
