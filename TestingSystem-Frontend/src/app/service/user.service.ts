import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserRegistration} from "../entity/UserRegistration";
import {UserLogin} from "../entity/UserLogin";
import {JwtToken} from "../entity/JwtToken";
import {Answer} from "../entity/Answer";
import {LeaderboardPage} from "../entity/LeaderboardPage";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public createUser(user: UserRegistration): Observable<UserRegistration> {
    return this.http.post<UserRegistration>(`${this.apiServerUrl}/user`, user);
  }

  public loginUser(user: UserLogin): Observable<JwtToken> {
    return this.http.post<JwtToken>(`${this.apiServerUrl}/login`, user);
  }

  public refreshToken(refreshToken: string): Observable<JwtToken> {
    return this.http.post<JwtToken>(`${this.apiServerUrl}/token/refresh`, refreshToken);
  }

  public submitAttempt(answers: Answer[], userId: number, token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('userId', userId);
    return this.http.post<void>(`${this.apiServerUrl}/submit`, answers, {params: params, headers: headers});
  }

  public getLeaderboard(index: number, size: number): Observable<LeaderboardPage> {
    let params = new HttpParams().set('index', index).set('size', size);
    return this.http.get<LeaderboardPage>(`${this.apiServerUrl}/leaderboard`, {params: params});
  }
}
