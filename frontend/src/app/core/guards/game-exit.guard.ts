import { CanDeactivateFn } from '@angular/router';
import { GameComponent } from '../../features/game/game.component';

export const gameExitGuard: CanDeactivateFn<GameComponent> = (component, _currentRoute, _currentState, nextState) => {
  return component.canLeavePage(nextState?.url ?? '/dashboard');
};
