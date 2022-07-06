import {Injectable} from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserRegistration} from "./entity/UserRegistration";
import {UserLogin} from "./entity/UserLogin";
import {Answer} from "./entity/Answer";
import {Leaderboard} from "./entity/Leaderboard";

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

  public loginUser(user: UserLogin): Observable<UserLogin> {
    return this.http.post<UserLogin>(`${this.apiServerUrl}/login`, user);
  }

  public submitAttempt(answers: Answer[], userId: number, token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    let params = new HttpParams().set('userId', userId);
    return this.http.post<void>(`${this.apiServerUrl}/submit`, answers, {params: params, headers: headers});
  }

  public getLeaderboard(): Observable<Leaderboard> {
    return this.http.get<Leaderboard>(`${this.apiServerUrl}/leaderboard`);
  }

  public logout(token: string): Observable<void> {
    let headers = new HttpHeaders().set('Authorization', token);
    return this.http.post<void>(`${this.apiServerUrl}/logout`, {headers: headers});
  }
}
