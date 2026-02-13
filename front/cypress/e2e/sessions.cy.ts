describe('Sessions list', () => {

  beforeEach(() => {

    // fake login
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'user',
        firstName: 'first',
        lastName: 'last',
        admin: true,
        token: 'fake'
      }
    }).as('login');

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
    }).as('sessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('test1234');
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.wait('@sessions');
  });

  it('should display sessions list', () => {
    cy.contains('Morning yoga').should('be.visible');
  });

});
