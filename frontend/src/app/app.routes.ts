import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component')
      .then(m => m.LoginComponent)
  },
   {
    path: 'register',
    loadComponent: () => import('./features/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component')
      .then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'about',
    loadComponent: () => import('./features/about/about.component')
      .then(m => m.AboutComponent)
  },
  {
    path: 'game',
    loadComponent: () => import('./features/game/game.component')
      .then(m => m.GameComponent),
    canActivate: [authGuard]
  },
  {
    path: 'ranking',
    loadComponent: () => import('./features/ranking/ranking.component')
      .then(m => m.RankingComponent),
    canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
