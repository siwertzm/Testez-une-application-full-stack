import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { Session } from '../../interfaces/session.interface';
import { of } from 'rxjs';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;

  const routerMock = {
    navigate: jest.fn(),
  };

  const snackMock = {
    open: jest.fn(),
  };

  const sessionApiMock = {
    detail: jest.fn(),
    delete: jest.fn(),
    participate: jest.fn(),
    unParticipate: jest.fn(),
  };

  const teacherServiceMock = {
    detail: jest.fn(),
  };

  const sessionServiceMock = {
    sessionInformation: {
      admin: true,
      id: 1,
      token: 't',
      type: 'Bearer',
      username: 'admin',
      firstName: 'A',
      lastName: 'B',
    },
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: convertToParamMap({ id: '123' }),
    },
  };

  const mockSessionParticipating: Session = {
    id: 123,
    name: 'Yoga',
    date: new Date('2025-01-01'),
    teacher_id: 10,
    description: 'Desc',
    users: [1], // user connecté participe
  } as any;

  const mockSessionNotParticipating: Session = {
    ...mockSessionParticipating,
    users: [2], // user connecté ne participe pas
  } as any;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  beforeEach(async () => {
    jest.clearAllMocks();

    // default mocks
    sessionApiMock.detail.mockReturnValue(of(mockSessionParticipating));
    teacherServiceMock.detail.mockReturnValue(of({ id: 10, firstName: 'T', lastName: 'Teacher' }));

    await TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackMock },
        { provide: SessionApiService, useValue: sessionApiMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.sessionId).toBe('123');
    expect(component.isAdmin).toBe(true);
    expect(component.userId).toBe('1');
  });

  it('ngOnInit should fetch session + teacher and set isParticipate true when user is in session.users', () => {
    component.ngOnInit();

    expect(sessionApiMock.detail).toHaveBeenCalledWith('123');
    expect(component.session).toEqual(mockSessionParticipating);
    expect(component.isParticipate).toBe(true);
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('10');
    expect(component.teacher).toBeTruthy();
  });

  it('should set isParticipate false when user is not in session.users', () => {
    sessionApiMock.detail.mockReturnValue(of(mockSessionNotParticipating));

    component.ngOnInit();

    expect(component.isParticipate).toBe(false);
  });

  it('delete should call api.delete and then show snackbar + navigate', () => {
    sessionApiMock.delete.mockReturnValue(of({}));

    component.delete();

    expect(sessionApiMock.delete).toHaveBeenCalledWith('123');
    expect(snackMock.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('participate should call api.participate then refetch session', () => {
    sessionApiMock.participate.mockReturnValue(of({}));
    component.ngOnInit();
    (sessionApiMock.detail as jest.Mock).mockClear();

    component.participate();

    expect(sessionApiMock.participate).toHaveBeenCalledWith('123', '1');
    expect(sessionApiMock.detail).toHaveBeenCalledWith('123'); // refetchSession
  });

  it('unParticipate should call api.unParticipate then refetch session', () => {
    sessionApiMock.unParticipate.mockReturnValue(of({}));
    component.ngOnInit();
    (sessionApiMock.detail as jest.Mock).mockClear();

    component.unParticipate();

    expect(sessionApiMock.unParticipate).toHaveBeenCalledWith('123', '1');
    expect(sessionApiMock.detail).toHaveBeenCalledWith('123'); // refetchSession
  });

  it('back should call window.history.back', () => {
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });
});

