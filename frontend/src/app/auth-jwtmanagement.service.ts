import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthJWTManagementService {

  AUTH_TOKEN_HEADER: string = 'Authorization';
  TOKEN_PREFIX: string = 'Bearer';
  loginUrl: string = 'http://127.0.0.1:8080/SpringBootRestApi/login';
  authenticate: boolean = false;

  user = {username: 'admin', password: 'password'};

  constructor(private http: HttpClient) {
    if (this.getToken() !== '') {
      this.authenticate = true;
    }
  }

  auth(credentials: any) {
    const promise = new Promise<void>((resolve, reject) => {
      this.http.post<any>(this.loginUrl, credentials, {
        headers: new HttpHeaders().set('Content-Type', 'text/plain'),
        observe: 'response'
      }).subscribe({
        next: (data) => {
          this.setToken(data.headers.get(this.AUTH_TOKEN_HEADER));

          if (this.getToken() !== '') {
              this.authenticate = true;
          } else {
              this.authenticate = false;
              console.error('NO ' + this.AUTH_TOKEN_HEADER + ' token found');
          }
          this.user = credentials;
          resolve();
        },
        error: (err: HttpErrorResponse) => {
          this.authenticate = false;
          console.log(err);
          reject(err);
        }
      });
    });
    return promise;
  }

  logout() {
    if (this.isAuthenticated()) {
      localStorage.clear();
      this.user = {username: 'User Api Application', password: ''};
      this.authenticate = false;
    }
  }

  isAuthenticated() {
      return this.authenticate && this.getToken() !== '';
  }

  setToken(token: string | null) {
      if (token) {
        localStorage.setItem(this.AUTH_TOKEN_HEADER, token);
      }
  }

  getToken() {
      const token = localStorage.getItem(this.AUTH_TOKEN_HEADER);
      return token !== null ? token : '';
  }

  getUser() {
    return this.user;
  }
}
