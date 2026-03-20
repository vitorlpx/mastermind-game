import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GuessResponse, MatchResponse } from '../models/game.model';

@Injectable({ providedIn: 'root' })
export class GameService {

  private readonly API_URL = 'http://localhost:8080/api/game';

  constructor(private http: HttpClient) { }

  public startMatch(): Observable<MatchResponse> {
    return this.http.post<MatchResponse>(`${this.API_URL}/start`, {})
  }

  public submitGuess(matchId: string, colors: string[]): Observable<GuessResponse> {
    return this.http.post<GuessResponse>(`${this.API_URL}/guess/${matchId}`, { colors });
  }

}