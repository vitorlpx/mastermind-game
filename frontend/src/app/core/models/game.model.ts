import { MatchStatus } from '@core/enums';

export interface GuessRequest {
  colors: string[];
}

export interface GuessResponse {
  hits: number;
  score: number;
  status: MatchStatus;
}

export interface MatchResponse {
  matchId: string;
  status: MatchStatus;
}

export interface RankingResponse {
  name: string;
  email: string;
  score: number;
}
