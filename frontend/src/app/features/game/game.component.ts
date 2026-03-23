import { CommonModule } from '@angular/common';
import { Component, HostListener, inject, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatchStatus } from '@core/enums';
import { BoardRow } from '@core/models/game.model';
import { GameService } from '@core/services/game.service';
import { getBackendErrorMessage } from '@core/utils/http-error.util';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './game.component.html',
  styleUrl: './game.component.scss'
})
export class GameComponent implements OnInit, OnDestroy {

  readonly COLOR_POOLS: Record<string, string[]> = {
    'EASY': ['RED', 'BLUE', 'GREEN', 'YELLOW', 'ORANGE', 'PURPLE'],
    'MEDIUM': ['RED', 'BLUE', 'GREEN', 'YELLOW', 'ORANGE', 'PURPLE', 'PINK', 'CYAN'],
    'HARD': ['RED', 'BLUE', 'GREEN', 'YELLOW', 'ORANGE', 'PURPLE', 'PINK', 'CYAN', 'WHITE', 'BROWN']
  };

  matchId = '';
  difficulty = '';
  board: BoardRow[] = Array.from({ length: 10 }, () => ({
    colors: ['', '', '', ''],
    feedback: [],
    score: 0,
    submitted: false
  }));
  currentRow = 0;
  selectedColor = '';
  gameOver = false;
  gameResult: 'WON' | 'LOST' | null = null;
  currentScore = 0;
  errorMessage = '';
  timer = 0;
  showExitModal = false;
  isAbandoningMatch = false;

  private timerInterval: any;
  private gameService = inject(GameService);
  private router = inject(Router);
  private pendingNavigationUrl = '/dashboard';

  ngOnInit() {
    this.matchId = localStorage.getItem('matchId') || '';
    this.difficulty = localStorage.getItem('difficulty') || 'EASY';
    this.startTimer();
  }

  ngOnDestroy() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  startTimer() {
    this.timerInterval = setInterval(() => {
      if (!this.gameOver) this.timer++;
    }, 1000);
  }

  selectColor(color: string) {
    this.selectedColor = color;

    if (this.gameOver) return;

    const row = this.board[this.currentRow];
    const emptyIndex = row.colors.indexOf('');

    if (emptyIndex !== -1) {
      row.colors[emptyIndex] = color;
    }
  }

  removeLastColor() {
    if (this.gameOver) return;

    const row = this.board[this.currentRow];

    for (let i = row.colors.length - 1; i >= 0; i--) {
      if (row.colors[i] !== '') {
        row.colors[i] = '';
        break;
      }
    }
  }

  placeColor(rowIndex: number, colorIndex: number) {
    if (rowIndex === this.currentRow && !this.board[rowIndex].submitted) {
      this.board[rowIndex].colors[colorIndex] = this.selectedColor;
    }
  }

  get COLOR_POOL(): string[] {
    return this.COLOR_POOLS[this.difficulty] ?? this.COLOR_POOLS['EASY'];
  }

  getColorHex(color: string): string {
    const map: Record<string, string> = {
      'RED': '#ef4444',
      'BLUE': '#3b82f6',
      'GREEN': '#22c55e',
      'YELLOW': '#eab308',
      'ORANGE': '#f97316',
      'PURPLE': '#a855f7',
      'PINK': '#ec4899',
      'CYAN': '#06b6d4',
      'WHITE': '#f1f5f9',
      'BROWN': '#92400e'
    };
    return map[color] || 'transparent';
  }

  getFeedbackDots(row: BoardRow): string[] {
    return row.feedback.length > 0 ? row.feedback : ['empty', 'empty', 'empty', 'empty'];
  }

  isSubmitDisabled(): boolean {
    return this.gameOver || this.board[this.currentRow]?.colors?.includes('');
  }

  isRemoveDisabled(): boolean {
    return this.gameOver || !this.board[this.currentRow]?.colors?.some(c => c !== '');
  }

  submitGuess() {
    const guessColors = this.board[this.currentRow].colors;

    if (guessColors.includes('')) {
      this.errorMessage = 'Preencha todas as posições antes de enviar.';
      return;
    }

    this.errorMessage = '';

    this.gameService.submitGuess(this.matchId, guessColors).subscribe({
      next: (response) => {
        this.board[this.currentRow].feedback = response.feedback;
        this.board[this.currentRow].submitted = true;
        this.currentScore = response.score;
        this.currentRow++;

        if (response.status === MatchStatus.WON) {
          this.gameOver = true;
          this.gameResult = 'WON';
          clearInterval(this.timerInterval);
          localStorage.removeItem('matchId');
        } else if (response.status === MatchStatus.LOST) {
          this.gameOver = true;
          this.gameResult = 'LOST';
          clearInterval(this.timerInterval);
          localStorage.removeItem('matchId');
        }
      },
      error: (error) => {
        this.errorMessage = getBackendErrorMessage(error);
      }
    });
  }

  get potentialScore(): number {
    const remaining = 10 - this.currentRow;
    return remaining * 100;
  }

  get formattedTime(): string {
    const minutes = Math.floor(this.timer / 60).toString().padStart(2, '0');
    const seconds = (this.timer % 60).toString().padStart(2, '0');
    return `${minutes}:${seconds}`;
  }

  @HostListener('window:beforeunload', ['$event'])
  onBeforeUnload(event: BeforeUnloadEvent): void {
    if (!this.hasInProgressMatch()) {
      return;
    }

    event.preventDefault();
    event.returnValue = '';
  }

  canLeavePage(nextUrl = '/dashboard'): boolean {
    if (!this.hasInProgressMatch()) {
      return true;
    }

    this.pendingNavigationUrl = nextUrl;
    this.showExitModal = true;
    return false;
  }

  cancelLeavePage(): void {
    this.showExitModal = false;
  }

  confirmLeavePage(): void {
    if (!this.matchId || this.isAbandoningMatch) {
      return;
    }

    this.isAbandoningMatch = true;

    this.gameService.abandonMatch(this.matchId).subscribe({
      next: () => {
        this.clearCurrentMatchSession();
        this.showExitModal = false;
        this.isAbandoningMatch = false;
        this.router.navigateByUrl(this.pendingNavigationUrl);
      },
      error: (error) => {
        this.errorMessage = getBackendErrorMessage(error);
        this.isAbandoningMatch = false;
      }
    });
  }

  private hasInProgressMatch(): boolean {
    return !!this.matchId && !this.gameOver;
  }

  private clearCurrentMatchSession(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }

    this.gameOver = true;
    this.matchId = '';
    localStorage.removeItem('matchId');
    localStorage.removeItem('difficulty');
  }

}