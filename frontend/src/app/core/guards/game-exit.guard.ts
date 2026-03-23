import { CanDeactivateFn } from '@angular/router';
import { GameComponent } from '../../features/game/game.component';

export const gameExitGuard: CanDeactivateFn<GameComponent> = (component) => {
  return component.canLeavePage();
};
