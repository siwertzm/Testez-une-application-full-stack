import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const baseUrl = 'api/session';

  const mockSession: Session = {
    id: 1,
    name: 'Yoga',
    date: new Date('2025-01-01'),
    teacher_id: 10,
    description: 'Desc',
    users: []
  } as any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('all() should GET api/session', () => {
    service.all().subscribe((sessions) => {
      expect(sessions).toEqual([mockSession]);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');

    req.flush([mockSession]);
  });

  it('detail(id) should GET api/session/:id', () => {
    service.detail('123').subscribe((session) => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`${baseUrl}/123`);
    expect(req.request.method).toBe('GET');

    req.flush(mockSession);
  });

  it('delete(id) should DELETE api/session/:id', () => {
    service.delete('123').subscribe((res) => {
      expect(res).toEqual({ ok: true });
    });

    const req = httpMock.expectOne(`${baseUrl}/123`);
    expect(req.request.method).toBe('DELETE');

    req.flush({ ok: true });
  });

  it('create(session) should POST api/session with body', () => {
    service.create(mockSession).subscribe((created) => {
      expect(created).toEqual(mockSession);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockSession);

    req.flush(mockSession);
  });

  it('update(id, session) should PUT api/session/:id with body', () => {
    service.update('123', mockSession).subscribe((updated) => {
      expect(updated).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`${baseUrl}/123`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockSession);

    req.flush(mockSession);
  });

  it('participate(id, userId) should POST api/session/:id/participate/:userId with null body', () => {
    service.participate('123', '7').subscribe((res) => {
      expect(res).toBeUndefined(); // void
    });

    const req = httpMock.expectOne(`${baseUrl}/123/participate/7`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();

    req.flush(null);
  });

  it('unParticipate(id, userId) should DELETE api/session/:id/participate/:userId', () => {
    service.unParticipate('123', '7').subscribe((res) => {
      expect(res).toBeUndefined(); // void
    });

    const req = httpMock.expectOne(`${baseUrl}/123/participate/7`);
    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });
});
