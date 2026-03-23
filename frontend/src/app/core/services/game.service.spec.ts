import { provideHttpClient } from '@angular/common/http';

import { TestBed } from '@angular/core/testing';

import { GameService } from './game.service';

describe('GameService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      GameService,
      provideHttpClient()
    ]
  }));

  it('should be created', () => {
    const service = TestBed.inject(GameService);
    expect(service).toBeTruthy();
  });
});
