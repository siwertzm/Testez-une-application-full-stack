/// <reference types="cypress" />

describe('Session detail from list', () => {
  beforeEach(() => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: { id: 1, admin: true, token: 'fake' },
    }).as('login');

    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: [
        {
          id: 1,
          name: 'Morning yoga',
          date: '2024-01-01T00:00:00.000Z',
          teacher_id: 1,
          description: 'Relax',
          users: [2, 3],
          createdAt: '2024-01-01T00:00:00.000Z',
          updatedAt: '2024-01-02T00:00:00.000Z',
        },
      ],
    }).as('sessions');

    cy.intercept('GET', '**/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'Morning yoga',
        date: '2024-01-01T00:00:00.000Z',
        teacher_id: 1,
        description: 'Relax',
        users: [2, 3],
        createdAt: '2024-01-01T00:00:00.000Z',
        updatedAt: '2024-01-02T00:00:00.000Z',
      },
    }).as('detail');

    cy.intercept('GET', '**/api/teacher/1', {
      statusCode: 200,
      body: { id: 1, firstName: 'Alice', lastName: 'Smith' },
    }).as('teacher');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('admin@test.com');
    cy.get('input[formControlName=password]').type('test1234');
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.wait('@sessions');
  });

  it('should navigate to detail and load detail data', () => {
    // ✅ Clique le bouton qui a routerLink
    cy.contains('mat-card', 'Morning yoga')
      .contains('button', 'Detail')
      .click();

    cy.url().should('include', '/sessions/detail/1');

    cy.wait('@detail');
    cy.wait('@teacher');

    cy.contains('Relax').should('be.visible');
    cy.contains('Alice SMITH').should('be.visible');
  });
});
