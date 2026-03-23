import { Component, inject, OnInit } from '@angular/core';
import { RankingResponse } from '@core/models/game.model';
import { AuthService } from '@core/services/auth.service';
import { RankingService } from '@core/services/ranking.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { getBackendErrorMessage } from '@core/utils/http-error.util';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.scss'
})
export class RankingComponent implements OnInit {

  private rankingService = inject(RankingService);
  private authService = inject(AuthService);
  private router = inject(Router);

  showLogoutModal: boolean = false;
  userName: string = '';
  email: string = (this.authService.getEmail() ?? '').trim().toLowerCase();
  ranking: RankingResponse[] = [];
  errorMessage = '';

  ngOnInit() {
    this.userName = this.authService.getName() ?? '';

    this.rankingService.getRanking().subscribe({
      next: (response) => {
        this.ranking = response;
        this.errorMessage = '';
      },
      error: (error) => {
        this.errorMessage = getBackendErrorMessage(error);
      }
    });
  }

  openLogoutModal() { this.showLogoutModal = true; }
  closeLogoutModal() { this.showLogoutModal = false; }

  confirmLogout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isCurrentUser(playerEmail: string): boolean {
    return !!this.email && playerEmail.trim().toLowerCase() === this.email;
  }

}
