/// <reference types="cypress" />

describe('Register', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('should have submit disabled when form is invalid, then enabled when valid', () => {
    // Au départ tout est vide => invalid
    cy.get('button[type=submit]').should('be.disabled');

    // Remplit partiellement => toujours invalid
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('button[type=submit]').should('be.disabled');

    // Remplit tout correctement => valid => enabled
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john@doe.com');
    cy.get('input[formControlName=password]').type('test1234');

    cy.get('button[type=submit]').should('not.be.disabled');
  });

  it('should register a user and redirect to /login (success path)', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 200,
      body: {},
    }).as('register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john@doe.com');
    cy.get('input[formControlName=password]').type('test1234');

    cy.get('button[type=submit]').click();

    cy.wait('@register').its('request.body').should((body) => {
      // bonus: vérifie que le payload correspond bien au form
      expect(body).to.include({
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@doe.com',
        password: 'test1234',
      });
    });

    cy.url().should('include', '/login');
    cy.contains('An error occurred').should('not.exist');
  });

  it('should display error message when register fails (error path)', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 400,
      body: { message: 'Bad request' },
    }).as('registerFail');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john@doe.com');
    cy.get('input[formControlName=password]').type('test1234');

    cy.get('button[type=submit]').click();
    cy.wait('@registerFail');

    // ✅ ça couvre: onError=true + *ngIf="onError"
    cy.contains('An error occurred').should('be.visible');

    // ✅ et on ne doit pas avoir été redirigé
    cy.url().should('include', '/register');
  });
});
