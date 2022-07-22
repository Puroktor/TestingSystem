import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserRegistration} from "../entity/UserRegistration";
import {UserLogin} from "../entity/UserLogin";
import {JwtToken} from "../entity/JwtToken";
import {User} from "../entity/User";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  public createUser(user: UserRegistration): Observable<User> {
    return this.http.post<User>(`${this.apiServerUrl}/user`, user);
  }

  public getUser(userId: number): Observable<User> {
    let headers = new HttpHeaders().set('Authorization', localStorage.getItem('access-jwt') ?? '');
    return this.http.get<User>(`${this.apiServerUrl}/user/${userId}`, {headers: headers});
  }

  public loginUser(user: UserLogin): Observable<JwtToken> {
    return this.http.post<JwtToken>(`${this.apiServerUrl}/login`, user);
  }

  public refreshToken(refreshToken: string): Observable<JwtToken> {
    return this.http.post<JwtToken>(`${this.apiServerUrl}/token/refresh`, refreshToken);
  }
}
