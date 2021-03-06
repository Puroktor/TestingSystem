import {Injectable} from '@angular/core';
import {Answer} from "../entity/Answer";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {LeaderboardPage} from "../entity/LeaderboardPage";
import {environment} from "../../environments/environment";
import {Attempt} from "../entity/Attempt";
import {AttemptResult} from "../entity/AttemptResult";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AttemptService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient, private userService: UserService) {
  }

  public submitAttempt(answers: Answer[]): Observable<AttemptResult> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.post<AttemptResult>(`${this.apiServerUrl}/attempt`, answers, {headers: headers});
  }

  public getAttemptsResults(userId: number): Observable<AttemptResult[]> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.get<AttemptResult[]>(`${this.apiServerUrl}/attempts/${userId}`, {headers: headers});
  }

  public getAttempt(attemptId: number): Observable<Attempt> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    return this.http.get<Attempt>(`${this.apiServerUrl}/attempt/${attemptId}`, {headers: headers});
  }

  public getLeaderboard(index: number, size: number): Observable<LeaderboardPage> {
    let headers = new HttpHeaders().set('Authorization', this.userService.accessToken.getValue());
    let params = new HttpParams().set('index', index).set('size', size);
    return this.http.get<LeaderboardPage>(`${this.apiServerUrl}/leaderboard`, {params: params, headers: headers});
  }
}
