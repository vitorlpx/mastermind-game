import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'token';

  constructor(private http: HttpClient) {}

  public register(name: string, email: string, password: string) {
    return this.http.post(`${this.API_URL}/register`, { name, email, password });
  }

  public login(email: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.API_URL}/login`, { email, password })
      .pipe(
        tap(response => localStorage.setItem(this.TOKEN_KEY, response.token))
      );
  }

  public logout() {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  public getName(): string | null {
    const token = this.getToken();
    
    if (!token) return null;

    const decoded = jwtDecode<{ name: string }>(token);

    return decoded.name;  
  }

  public getEmail(): string | null {
    const token = this.getToken();
    
    if (!token) return null;

    const decoded = jwtDecode<{ email?: string; sub?: string; preferred_username?: string }>(token);

    return decoded.email ?? decoded.sub ?? decoded.preferred_username ?? null;
  }

  public isAuthenticated(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

}
