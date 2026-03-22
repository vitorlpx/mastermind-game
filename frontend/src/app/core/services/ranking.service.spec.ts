import { provideHttpClient } from '@angular/common/http';

import { TestBed } from '@angular/core/testing';

import { RankingService } from './ranking.service';

describe('RankingService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      RankingService,
      provideHttpClient()
    ]
  }));

  it('should be created', () => {
    const service = TestBed.inject(RankingService);
    expect(service).toBeTruthy();
  });
});
