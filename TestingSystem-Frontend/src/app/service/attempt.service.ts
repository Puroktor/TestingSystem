import {Injectable} from '@angular/core';
import {Answer} from "../entity/Answer";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {LeaderboardPage} from "../entity/LeaderboardPage";
import {environment} from "../../environments/environment";
import {Attempt} from "../entity/Attempt";

@Injectable({
  providedIn: 'root'
})
export class AttemptService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public submitAttempt(answers: Answer[], userId: number, token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('userId', userId);
    return this.http.post<void>(`${this.apiServerUrl}/attempt`, answers, {params: params, headers: headers});
  }

  public getAttempt(userId: number, testId: number, token: string): Observable<Attempt>  {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('userId', userId).set('testId', testId)
    return this.http.get<Attempt>(`${this.apiServerUrl}/attempt`, {params: params, headers: headers});
  }

  public getLeaderboard(index: number, size: number, token: string): Observable<LeaderboardPage> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('index', index).set('size', size);
    return this.http.get<LeaderboardPage>(`${this.apiServerUrl}/leaderboard`, {params: params, headers: headers});
  }
}
