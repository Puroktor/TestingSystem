import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserRegistration} from "../entity/UserRegistration";
import {UserLogin} from "../entity/UserLogin";
import {JwtToken} from "../entity/JwtToken";

@Injectable({
  providedIn: 'root'
})
export class UserAuthService {
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
}
