import { MatchStatus } from '@core/enums';

export interface MatchHistory {
  score: number;
  status: MatchStatus;
  startedAt: Date;      
  finishedAt: Date;
}

export interface UserProfile {
  name: string;
  bestScore: number;
}