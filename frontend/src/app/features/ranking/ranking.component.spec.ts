import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankingComponent } from './ranking.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

describe('RankingComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RankingComponent],
      providers: [
        provideHttpClient(),
        provideRouter([])
      ]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(RankingComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
