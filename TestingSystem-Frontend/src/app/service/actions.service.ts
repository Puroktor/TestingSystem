import {Injectable} from '@angular/core';
import {Answer} from "../entity/Answer";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Leaderboard} from "../entity/Leaderboard";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ActionsService {

  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public submitAttempt(answers: Answer[], userId: number, token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('userId', userId);
    return this.http.post<void>(`${this.apiServerUrl}/submit`, answers, {params: params, headers: headers});
  }

  public getLeaderboard(): Observable<Leaderboard> {
    return this.http.get<Leaderboard>(`${this.apiServerUrl}/leaderboard`);
  }
}
