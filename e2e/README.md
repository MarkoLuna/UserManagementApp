# E2E Tests

End-to-end tests for the User Management App using [Playwright](https://playwright.dev/).

## Prerequisites

- **Node.js** with **npm ≥ 11.10.0**
- The **backend** must be running (see root README for instructions)
- The **frontend** must be running on `http://localhost:4200`

## Quick Start

```bash
# Navigate to the e2e directory
cd e2e

# Install dependencies
npm install

# Install Playwright browsers (Chromium)
npx playwright install chromium

# Run tests headlessly
npm test

# Run tests with browser UI visible
npm run test:headed

# Run tests in debug mode
npm run test:debug

# Open Playwright UI mode (watch mode)
npm run test:ui

# Open the HTML test report
npm run report
```

## Test Structure

```
e2e/
├── pages/                # Page Object Models
│   ├── login.page.ts     # Login page interactions
│   └── users.page.ts     # User CRUD page interactions
├── fixtures/             # Test data
│   └── test-data.ts      # Seed data & credentials
├── tests/                # Test specs
│   ├── auth.spec.ts      # Authentication test scenarios
│   └── users-crud.spec.ts # User CRUD test scenarios
└── playwright.config.ts
```

## Test Files

### `tests/auth.spec.ts` — Authentication Scenarios

| Test | Description |
|------|-------------|
| Login with valid credentials | Authenticate and verify redirect to users page |
| Login with invalid credentials | Verify error message on bad credentials |
| Redirect without auth | Accessing /home without login redirects to /login |
| Logout | Verify login page shown after navigating away |

### `tests/users-crud.spec.ts` — User CRUD Scenarios

| Test | Description |
|------|-------------|
| List/Read users | Verify seed users are displayed in table |
| View user details | Click view icon and verify form populates |
| Create user | Fill form and submit, verify user appears |
| Create multiple users | Bulk create users |
| Update user | Edit existing user fields |
| Partial update | Update specific fields of a user |
| Delete user | Remove a user and verify deletion |
| Full CRUD workflow | Create → View → Update → Delete in sequence |

## Configuration

- **Base URL**: `http://localhost:4200` (configured in `playwright.config.ts`)
- **Browser**: Chromium (Desktop Chrome viewport)
- **Reporter**: HTML report + console list
- **Artifacts**: Screenshots on failure, video retained on failure
