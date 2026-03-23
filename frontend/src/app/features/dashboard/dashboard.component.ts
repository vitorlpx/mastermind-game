import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { GameService } from '../../core/services/game.service';
import { RankingService } from '../../core/services/ranking.service';
import { Router, RouterLink } from '@angular/router';
import { RankingResponse } from '../../core/models/game.model';
import { MatchHistory } from '../../core/models/user.model';
import { UserService } from '../../core/services/user.service';
import { CommonModule } from '@angular/common';
import { MatchStatus } from '@core/enums';
import { getBackendErrorMessage } from '@core/utils/http-error.util';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {

  readonly MatchStatus = MatchStatus;

  private authService = inject(AuthService);
  private userService = inject(UserService);
  private gameService = inject(GameService);
  private rankingService = inject(RankingService);
  private router = inject(Router);

  userName: string = '';
  email: string = (this.authService.getEmail() ?? '').trim().toLowerCase();
  bestScore = 0;
  ranking: RankingResponse[] = [];
  matchHistory: MatchHistory[] = [];
  selectedDifficulty: string = 'EASY';

  showStartModal: boolean = false;
  isStartingGame: boolean = false;
  showLogoutModal: boolean = false;
  errorMessage = '';
  startGameErrorMessage = '';

  openStartModal() {
    this.startGameErrorMessage = '';
    this.showStartModal = true;
  }

  closeStartModal() {
    this.showStartModal = false;
    this.isStartingGame = false;
    this.startGameErrorMessage = '';
  }

  ngOnInit() {
    this.userName = this.authService.getName() ?? '';

    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.bestScore = profile.bestScore;
      },
      error: (error) => {
        this.errorMessage = getBackendErrorMessage(error);
      }
    });

    this.rankingService.getRanking().subscribe({
      next: (ranking) => {
        this.ranking = ranking;
      },
      error: (error) => {
        this.errorMessage = getBackendErrorMessage(error);
      }
    });

    this.userService.getMatchHistory().subscribe({
      next: (history) => {
        this.matchHistory = history;
      },
      error: (error) => {
        this.errorMessage = getBackendErrorMessage(error);
        this.matchHistory = [];
      }
    });
  }

  openLogoutModal() { this.showLogoutModal = true; }
  closeLogoutModal() { this.showLogoutModal = false; }

  confirmLogout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  confirmStartGame() {
    this.isStartingGame = true;

    this.gameService.startMatch(this.selectedDifficulty).subscribe({
      next: (response) => {
        localStorage.setItem('matchId', response.matchId);
        localStorage.setItem('difficulty', this.selectedDifficulty);
        this.router.navigate(['/game']);
      },
      error: (error) => {
        this.startGameErrorMessage = getBackendErrorMessage(error);
        this.isStartingGame = false;
      }
    });
  }

  getWinRate(): number {
    if (this.matchHistory.length === 0) return 0;
    const wins = this.matchHistory.filter(m => m.status === MatchStatus.WON).length;
    return Math.round((wins / this.matchHistory.length) * 100);
  }

  isCurrentUser(playerEmail: string): boolean {
    return !!this.email && playerEmail.trim().toLowerCase() === this.email;
  }
}
