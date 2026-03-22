import { TestBed } from '@angular/core/testing';
import { GameComponent } from './game.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

describe('LoginComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameComponent],
      providers: [
        provideHttpClient(),
        provideRouter([])
      ]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(GameComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
