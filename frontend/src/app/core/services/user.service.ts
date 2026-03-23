import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatchHistory, UserProfile } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  public getMatchHistory(): Observable<MatchHistory[]> {
    return this.http.get<MatchHistory[]>(`${this.API_URL}/match/history`);
  }

  public getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.API_URL}/user/me`);
  }
}
