/// <reference types="cypress" />

describe('Create session', () => {

  beforeEach(() => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: { id: 1, admin: true, token: 'fake' }
    }).as('login');

    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: []
    }).as('sessions');

    cy.intercept('GET', '**/api/teacher', {
      statusCode: 200,
      body: [{ id: 1, firstName: 'Teacher', lastName: 'One' }]
    }).as('teachers');

    cy.intercept('POST', '**/api/session', {
      statusCode: 200
    }).as('create');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('admin@test.com');
    cy.get('input[formControlName=password]').type('test1234');
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.wait('@sessions');
  });

  it('should create a session', () => {
    cy.contains('Create').click();
    cy.url().should('include', '/sessions/create');

    cy.wait('@teachers');

    cy.get('input[formControlName=name]').type('New session');
    cy.get('textarea[formControlName=description]').type('desc');
    cy.get('input[formControlName=date]').type('2026-02-13');

    // 🔥 teacher select (adapte le formControlName si besoin)
    cy.get('[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Teacher One').click();

    cy.get('button[type=submit]').should('not.be.disabled').click();

    cy.wait('@create');
    cy.url().should('include', '/sessions');
  });

});
