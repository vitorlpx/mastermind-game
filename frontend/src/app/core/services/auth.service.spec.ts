import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store token in localStorage after login', () => {
    service.login('test@test.com', 'password123').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'fake-jwt-token' });

    expect(localStorage.getItem('token')).toBe('fake-jwt-token');
  });

  it('should remove token from localStorage on logout', () => {
    localStorage.setItem('token', 'fake-jwt-token');
    service.logout();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should return true when token exists', () => {
    localStorage.setItem('token', 'fake-jwt-token');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should return false when token does not exist', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });
});