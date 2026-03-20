import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RankingResponse } from '../models/game.model';

@Injectable({
  providedIn: 'root'
})
export class RankingService {

  private readonly API_URL = 'http://localhost:8080/api/ranking';

  constructor(private http: HttpClient) { }

  public getRanking(): Observable<RankingResponse[]> {
    return this.http.get<RankingResponse[]>(this.API_URL);
  }
}
