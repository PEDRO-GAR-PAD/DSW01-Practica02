describe('Smoke E2E', () => {
  it('logs in as master and creates an employee', () => {
    const uniqueId = Date.now();
    const employeeName = `QA Cypress ${uniqueId}`;
    let createdEmployee = null;

    cy.intercept('GET', '**/api/v1/auth/empleado/me', (req) => {
      expect(req.headers.authorization).to.eq('Basic bWFzdGVyOmFkbWluMTIz');
      req.reply({
        statusCode: 200,
        body: {
          empleadoClave: 'E-0001',
          username: 'master',
          nombre: 'Master User',
          authStatus: 'AUTHENTICATED',
          roles: ['ROLE_ADMIN']
        }
      });
    }).as('authMe');

    cy.intercept('GET', '**/api/v1/departamentos*', {
      statusCode: 200,
      body: {
        content: [
          {
            clave: 'D-0001',
            nombre: 'Sistemas'
          }
        ],
        page: 0,
        size: 100,
        totalElements: 1,
        totalPages: 1
      }
    }).as('listDepartamentos');

    cy.intercept('GET', '**/api/v1/empleados*', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          content: createdEmployee ? [createdEmployee] : [],
          page: 0,
          size: 10,
          totalElements: createdEmployee ? 1 : 0,
          totalPages: 1
        }
      });
    }).as('listEmpleados');

    cy.intercept('POST', '**/api/v1/empleados', (req) => {
      const payload = req.body;

      expect(payload.nombre).to.eq(employeeName);
      expect(payload.direccion).to.eq('Calle Cypress 123');
      expect(payload.telefono).to.eq('5551234567');
      expect(payload.departamentoClave).to.match(/^D-[0-9]{4}$/);

      createdEmployee = {
        clave: 'E-9001',
        nombre: payload.nombre,
        direccion: payload.direccion,
        telefono: payload.telefono,
        departamentoClave: payload.departamentoClave,
        version: 0
      };

      req.reply({ statusCode: 201, body: createdEmployee });
    }).as('createEmpleado');

    cy.visit('/login');
    cy.contains('h1', 'Iniciar sesion').should('be.visible');

    cy.get('input[formcontrolname="email"]').type('master');
    cy.get('input[formcontrolname="password"]').type('admin123', { log: false });
    cy.contains('button', 'Entrar').click();

    cy.wait('@authMe').its('response.statusCode').should('eq', 200);
    cy.url({ timeout: 10000 }).should('include', '/app/dashboard');

    cy.contains('a', 'Empleados').click();
    cy.wait('@listEmpleados');
    cy.url().should('include', '/app/empleados');

    cy.contains('a', 'Nuevo empleado').click();
    cy.wait('@listDepartamentos');
    cy.url().should('include', '/app/empleados/nuevo');

    cy.get('input[formcontrolname="nombre"]').type(employeeName);
    cy.get('input[formcontrolname="direccion"]').type('Calle Cypress 123');
    cy.get('input[formcontrolname="telefono"]').type('5551234567');

    cy.get('body').then(($body) => {
      if ($body.find('select[formcontrolname="departamentoClave"]').length) {
        cy.get('select[formcontrolname="departamentoClave"] option')
          .then(($options) => {
            const firstValidOption = [...$options]
              .map((option) => option.value)
              .find((value) => value && value.trim() !== '');

            expect(firstValidOption).to.exist;
            cy.get('select[formcontrolname="departamentoClave"]').select(firstValidOption);
          });
      } else {
        cy.get('input[formcontrolname="departamentoClave"]').type('D-0001');
      }
    });

    cy.contains('button', 'Crear empleado').click();

    cy.wait('@createEmpleado').then((interception) => {
      expect(interception.response?.statusCode).to.be.oneOf([200, 201]);
      expect(interception.response?.body?.nombre).to.eq(employeeName);
    });

    cy.url().should('include', '/app/empleados');
    cy.wait('@listEmpleados');
    cy.contains('No se pudo cargar el listado de empleados.').should('not.exist');
  });
});
