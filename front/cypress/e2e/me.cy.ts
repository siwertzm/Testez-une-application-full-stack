/// <reference types="cypress" />

describe('Account page (/me)', () => {
  const login = () => {
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('john@doe.com');
    cy.get('input[formControlName=password]').type('test1234');
    cy.get('button[type=submit]').click();
    cy.wait('@login');
    cy.wait('@sessions');
  };

  beforeEach(() => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: { id: 1, admin: false, token: 'fake' },
    }).as('login');

    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: [],
    }).as('sessions');
  });

  it('should display user info for non-admin and allow deleting account', () => {
    cy.intercept('GET', '**/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        admin: false,
        createdAt: '2024-01-01T00:00:00.000Z',
        updatedAt: '2024-02-01T00:00:00.000Z',
      },
    }).as('user');

    cy.intercept('DELETE', '**/api/user/1', {
      statusCode: 200,
      body: {},
    }).as('deleteUser');

    login();

    cy.contains('Account').click();
    cy.url().should('include', '/me');
    cy.wait('@user');

    // Header + infos (matching exact template)
    cy.contains('User information').should('be.visible');
    cy.contains('Name: John DOE').should('be.visible');
    cy.contains('Email: john@doe.com').should('be.visible');

    // Non-admin branch: delete section visible
    cy.contains('You are admin').should('not.exist');
    cy.contains('Delete my account:').should('be.visible');

    // Click delete button (span "Detail" inside button)
    cy.contains('button', 'Detail').click();
    cy.wait('@deleteUser');

    // Snackbar + navigation
    cy.contains('Your account has been deleted !').should('be.visible');
    cy.url().should('match', /\/($|login)/);
  });

  it('should display admin message and hide delete section when user is admin', () => {
    cy.intercept('GET', '**/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        admin: true,
        createdAt: '2024-01-01T00:00:00.000Z',
        updatedAt: '2024-02-01T00:00:00.000Z',
      },
    }).as('userAdmin');

    login();

    cy.contains('Account').click();
    cy.url().should('include', '/me');
    cy.wait('@userAdmin');

    cy.contains('User information').should('be.visible');
    cy.contains('Name: John DOE').should('be.visible');
    cy.contains('Email: john@doe.com').should('be.visible');

    // Admin branch visible
    cy.contains('You are admin').should('be.visible');

    // Delete branch hidden
    cy.contains('Delete my account:').should('not.exist');
    cy.contains('button', 'Detail').should('not.exist');
  });

  it('should not render user block when loading user fails (500)', () => {
    cy.intercept('GET', '**/api/user/1', { statusCode: 500 }).as('userFail');

    login();

    cy.contains('Account').click();
    cy.url().should('include', '/me');
    cy.wait('@userFail');

    // Card is there, but *ngIf="user" block should not be rendered
    cy.contains('User information').should('be.visible');
    cy.contains('Name:').should('not.exist');
    cy.contains('Email:').should('not.exist');
    cy.contains('Delete my account:').should('not.exist');
    cy.contains('You are admin').should('not.exist');
  });

  it('should go back when clicking the back arrow', () => {
    cy.intercept('GET', '**/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        admin: false,
        createdAt: '2024-01-01T00:00:00.000Z',
        updatedAt: '2024-02-01T00:00:00.000Z',
      },
    }).as('user');

    login();

    // create history: go somewhere then /me
    cy.contains('Sessions').click(); // adapte si ton menu s’appelle autrement
    cy.url().should('include', '/sessions');

    cy.contains('Account').click();
    cy.url().should('include', '/me');
    cy.wait('@user');

    // click back icon
    cy.get('mat-icon').contains('arrow_back').click();
    cy.url().should('include', '/sessions');
  });
});
