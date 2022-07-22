import {Injectable} from '@angular/core';
import {Answer} from "../entity/Answer";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {LeaderboardPage} from "../entity/LeaderboardPage";
import {environment} from "../../environments/environment";
import {Attempt} from "../entity/Attempt";
import {AttemptResult} from "../entity/AttemptResult";

@Injectable({
  providedIn: 'root'
})
export class AttemptService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public submitAttempt(answers: Answer[], userId: number, token: string): Observable<AttemptResult> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('userId', userId);
    return this.http.post<AttemptResult>(`${this.apiServerUrl}/attempt`, answers,
      {params: params, headers: headers});
  }

  public getAttempt(attemptId: number, token: string): Observable<Attempt> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('attemptId', attemptId);
    return this.http.get<Attempt>(`${this.apiServerUrl}/attempt`, {params: params, headers: headers});
  }

  public getLeaderboard(index: number, size: number, token: string): Observable<LeaderboardPage> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('index', index).set('size', size);
    return this.http.get<LeaderboardPage>(`${this.apiServerUrl}/leaderboard`, {params: params, headers: headers});
  }
}
