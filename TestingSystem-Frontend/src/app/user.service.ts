import {Injectable} from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {FullTest} from "./entity/FullTest";
import {Observable} from "rxjs";
import {UserRegistration} from "./entity/UserRegistration";
import {UserLogin} from "./entity/UserLogin";

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

  public loginUser(user: UserLogin): Observable<number> {
    return this.http.post<number>(`${this.apiServerUrl}/login`, user);
  }

  public submitAttempt(test: FullTest, studentId:number): Observable<void> {
    let params = new HttpParams().set('studentId', studentId);
    return this.http.post<void>(`${this.apiServerUrl}/submit`, test, {params: params});
  }
}
