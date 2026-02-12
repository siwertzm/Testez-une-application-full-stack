import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const routerMock = {
    navigate: jest.fn(),
  };

  const sessionServiceMock = {
    logIn: jest.fn(),
  };

  const authServiceMock = {
    login: jest.fn(),
  };

  const mockResponse = {
    token: 't',
    type: 'Bearer',
    id: 1,
    username: 'john',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [ReactiveFormsModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        BrowserAnimationsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.onError).toBe(false);
  });

  it('submit should login, call sessionService.logIn and navigate on success', () => {
    authServiceMock.login.mockReturnValue(of(mockResponse));

    component.form.setValue({ email: 'a@b.com', password: '123' });
    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith({ email: 'a@b.com', password: '123' });
    expect(sessionServiceMock.logIn).toHaveBeenCalledWith(mockResponse);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('submit should set onError to true on error', () => {
    authServiceMock.login.mockReturnValue(throwError(() => new Error('bad credentials')));

    component.form.setValue({ email: 'a@b.com', password: '123' });
    component.submit();

    expect(authServiceMock.login).toHaveBeenCalled();
    expect(component.onError).toBe(true);
    expect(sessionServiceMock.logIn).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
