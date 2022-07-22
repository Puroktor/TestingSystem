import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {UserRegistration} from "../entity/UserRegistration";
import {UserLogin} from "../entity/UserLogin";
import {JwtToken} from "../entity/JwtToken";
import {User} from "../entity/User";
import jwt_decode from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  accessToken = new BehaviorSubject<string>('');
  username = new BehaviorSubject<string | null>(null);
  authorities = new BehaviorSubject<string[]>([]);
  private apiServerUrl = environment.apiBaseUrl;
  private timer?: ReturnType<typeof setTimeout>;
  private errors = new Subject<string | null>();

  constructor(private http: HttpClient) {
    this.updateSubjects();
  }

  public createUser(user: UserRegistration): Observable<User> {
    return this.http.post<User>(`${this.apiServerUrl}/user`, user);
  }

  public getUser(userId: number): Observable<User> {
    let headers = new HttpHeaders().set('Authorization', this.accessToken.getValue());
    return this.http.get<User>(`${this.apiServerUrl}/user/${userId}`, {headers: headers});
  }

  public loginUser(user: UserLogin): Observable<string | null> {
    let tokens = this.http.post<JwtToken>(`${this.apiServerUrl}/login`, user);
    tokens.subscribe({
      next: response => {
        localStorage.setItem('access-jwt', response.accessToken);
        localStorage.setItem('refresh-jwt', response.refreshToken);
        this.updateSubjects();
        this.errors.next(null);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status == 0) {
          setTimeout(() => this.loginUser(user), environment.retryDelay);
        } else {
          this.errors.next(err.error.message);
        }
      }
    });
    return this.errors;
  }

  public logout() {
    localStorage.removeItem('access-jwt');
    localStorage.removeItem('refresh-jwt');
    this.updateSubjects();
  }

  private updateSubjects() {
    let accessToken = localStorage.getItem('access-jwt');
    if (accessToken) {
      this.accessToken.next(accessToken);
      let decodedAccess: any = jwt_decode(accessToken);
      this.username.next(decodedAccess.sub);
      this.authorities.next(decodedAccess.authorities);
      this.timer = setTimeout(() => this.refresh(), Math.max(decodedAccess.exp * 1000 - Date.now(), 0));
    } else {
      this.accessToken.next('');
      this.username.next(null);
      this.authorities.next([]);
      clearTimeout(this.timer);
    }
  }

  private refresh() {
    let refreshToken = localStorage.getItem('refresh-jwt');
    if (refreshToken == null) {
      this.logout();
      return;
    }
    let decodedRefresh: any = jwt_decode(refreshToken);
    if (decodedRefresh.exp * 1000 > Date.now()) {
      this.refreshToken(refreshToken);
    } else {
      this.logout();
    }
  }

  private refreshToken(refreshToken: string) {
    this.http.post<JwtToken>(`${this.apiServerUrl}/token/refresh`, refreshToken).subscribe({
      next: (response) => {
        localStorage.setItem('access-jwt', response.accessToken);
        localStorage.setItem('refresh-jwt', response.refreshToken);
        this.updateSubjects();
      }, error: (err: HttpErrorResponse) => {
        if (err.status == 0) {
          setTimeout(() => this.refreshToken(refreshToken), environment.retryDelay);
        } else {
          this.logout();
        }
      }
    });
  }
}
