import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { of } from 'rxjs';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { Session } from '../../interfaces/session.interface';
import { TeacherService } from 'src/app/services/teacher.service';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  } 

  const routerMock = {
    navigate: jest.fn(),
    url: '/sessions/create',
  };

  const snackMock = {
    open: jest.fn(),
  };

  const apiMock = {
    detail: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
  };

  const teacherServiceMock = {
    all: jest.fn().mockReturnValue(of([])),
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: convertToParamMap({ id: '123' }),
    },
  };

  const adminUser = {
    token: 't',
    type: 'Bearer',
    id: 1,
    username: 'admin',
    firstName: 'A',
    lastName: 'B',
    admin: true,
  };

  const mockSession: Session = {
    id: 123,
    name: 'Yoga',
    date: new Date('2025-01-01'), // ✅ Date, pas string
    teacher_id: 1,
    description: 'Desc',
    users: []
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatSelectModule,
        MatSnackBarModule,
        BrowserAnimationsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackMock },
        { provide: SessionApiService, useValue: apiMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: SessionService, useValue: { sessionInformation: adminUser } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /sessions if user is not admin', () => {
    (TestBed.inject(SessionService) as any).sessionInformation.admin = false;
    (TestBed.inject(Router) as any).url = '/sessions/create';

    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should init create mode when url does not include update', () => {
    (TestBed.inject(Router) as any).url = '/sessions/create';

    component.ngOnInit();

    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeTruthy();
  });

  it('should submit create and exit page', () => {
    (TestBed.inject(Router) as any).url = '/sessions/create';
    apiMock.create.mockReturnValue(of(mockSession));

    component.ngOnInit();

    component.sessionForm!.setValue({
      name: 'Yoga',
      date: '2025-01-01',
      teacher_id: 1,
      description: 'Desc',
    });

    component.submit();

    expect(apiMock.create).toHaveBeenCalledWith(expect.any(Object));
    expect(snackMock.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should init update mode and call detail(id)', () => {
    (TestBed.inject(Router) as any).url = '/sessions/update/123';
    apiMock.detail.mockReturnValue(of(mockSession));

    component.ngOnInit();

    expect(component.onUpdate).toBe(true);
    expect(apiMock.detail).toHaveBeenCalledWith('123');
    expect(component.sessionForm!.value.name).toBe('Yoga');
  });

  it('should submit update and exit page', () => {
    (TestBed.inject(Router) as any).url = '/sessions/update/123';
    apiMock.detail.mockReturnValue(of(mockSession));
    apiMock.update.mockReturnValue(of(mockSession));

    component.ngOnInit();

    component.sessionForm!.setValue({
      name: 'Yoga updated',
      date: '2025-01-01',
      teacher_id: 1,
      description: 'Desc updated',
    });

    component.submit();

    expect(apiMock.update).toHaveBeenCalledWith('123', expect.any(Object));
    expect(snackMock.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
