import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 1,
    username: 'john',
    firstName: 'John',
    lastName: 'Doe',
    admin: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit false by default on $isLogged()', (done) => {
    service.$isLogged().subscribe((value) => {
      expect(value).toBe(false);
      done();
    });
  });

  it('should set sessionInformation + isLogged to true on logIn and emit true', () => {
    const emitted: boolean[] = [];
    const sub = service.$isLogged().subscribe((v) => emitted.push(v));

    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
    // BehaviorSubject emits initial value (false), then true after logIn
    expect(emitted).toEqual([false, true]);

    sub.unsubscribe();
  });

  it('should clear sessionInformation + set isLogged to false on logOut and emit false', () => {
    const emitted: boolean[] = [];
    const sub = service.$isLogged().subscribe((v) => emitted.push(v));

    service.logIn(mockUser);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    // false (initial), true (login), false (logout)
    expect(emitted).toEqual([false, true, false]);

    sub.unsubscribe();
  });
});
