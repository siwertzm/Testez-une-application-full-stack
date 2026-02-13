/// <reference types="cypress" />

describe('Session detail', () => {

  beforeEach(() => {

    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        admin: true,
        token: 'fake'
      }
    });

    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: [
        {
          id: 1,
          name: 'Morning yoga',
          date: '2024-01-01',
          teacher_id: 1,
          description: 'Relax'
        }
      ]
    });

    cy.intercept('GET', '**/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'Morning yoga',
        date: '2024-01-01',
        teacher_id: 1,
        description: 'Relax'
      }
    });

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('admin@test.com');
    cy.get('input[formControlName=password]').type('test1234');
    cy.get('button[type=submit]').click();
  });

  it('should display session detail', () => {

    cy.contains('Morning yoga').click();

    cy.contains('Relax').should('be.visible');
  });

});
