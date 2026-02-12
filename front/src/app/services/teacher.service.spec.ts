import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const baseUrl = 'api/teacher';

  const mockTeacher: Teacher = {
    id: 10,
    firstName: 'John',
    lastName: 'Doe',
  } as any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('all() should GET api/teacher', () => {
    service.all().subscribe((teachers) => {
      expect(teachers).toEqual([mockTeacher]);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');

    req.flush([mockTeacher]);
  });

  it('detail(id) should GET api/teacher/:id', () => {
    service.detail('10').subscribe((teacher) => {
      expect(teacher).toEqual(mockTeacher);
    });

    const req = httpMock.expectOne(`${baseUrl}/10`);
    expect(req.request.method).toBe('GET');

    req.flush(mockTeacher);
  });
});
