import { provideHttpClient } from '@angular/common/http';

import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';

describe('UserService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      UserService,
      provideHttpClient()
    ]
  }));

  it('should be created', () => {
    const service = TestBed.inject(UserService);
    expect(service).toBeTruthy();
  });
});
