import { Component, inject, OnInit } from '@angular/core';
import { RankingResponse } from '@core/models/game.model';
import { AuthService } from '@core/services/auth.service';
import { RankingService } from '@core/services/ranking.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

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

  ngOnInit() {
    this.userName = this.authService.getName() ?? '';

    this.rankingService.getRanking().subscribe({
      next: (response) => {
        this.ranking = response;
      },
      error: (err) => {
        console.error('Error fetching ranking:', err);
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
