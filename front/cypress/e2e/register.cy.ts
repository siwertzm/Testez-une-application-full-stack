/// <reference types="cypress" />

describe('Register', () => {

  it('should register a user', () => {

    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 200
    }).as('register');

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john@doe.com');
    cy.get('input[formControlName=password]').type('test1234');

    cy.get('button[type=submit]').click();

    cy.wait('@register');

    cy.url().should('include', '/login');
  });

});
