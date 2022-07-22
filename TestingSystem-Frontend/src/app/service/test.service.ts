import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {FullTest} from "../entity/FullTest";
import {environment} from "../../environments/environment";
import {Page} from "../entity/Page";
import {TestCard} from "../entity/TestCard";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class TestService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient, private userService: UserService) {
  }

  public createTest(test: FullTest): Observable<FullTest> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.post<FullTest>(`${this.apiServerUrl}/test`, test, {headers: headers});
  }

  public getShuffledTest(id: number): Observable<FullTest> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.get<FullTest>(`${this.apiServerUrl}/test/shuffled/${id}`, {headers: headers});
  }

  public getTest(id: number): Observable<FullTest> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.get<FullTest>(`${this.apiServerUrl}/test/${id}`, {headers: headers});
  }

  public fetchPage(programmingLang: string, index: number, size: number): Observable<Page<TestCard>> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    let params = new HttpParams().set('programmingLang', programmingLang).set('index', index).set('size', size);
    return this.http.get<Page<TestCard>>(`${this.apiServerUrl}/test`, {params: params, headers: headers});
  }

  public updateTest(id: number, test: FullTest): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.put<void>(`${this.apiServerUrl}/test/${id}`, test, {headers: headers});
  }

  public deleteTest(id: number): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.delete<void>(`${this.apiServerUrl}/test/${id}`, {headers: headers});
  }
}
