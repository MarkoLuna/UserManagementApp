import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/login.page';
import { VALID_CREDENTIALS, INVALID_CREDENTIALS } from '../fixtures/test-data';

test.describe('Authentication', () => {
  let loginPage: LoginPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
  });

  test('should login with valid credentials', async ({ page }) => {
    await loginPage.login(VALID_CREDENTIALS.username, VALID_CREDENTIALS.password);

    await expect(page).toHaveURL(/\/home/);
    await expect(page.locator('table[mat-table]')).toBeVisible();
  });

  test('should show error with invalid credentials', async ({ page }) => {
    await loginPage.login(INVALID_CREDENTIALS.username, INVALID_CREDENTIALS.password);

    await expect(page.locator('.error-message')).toBeVisible();
  });

  test('should redirect to login when accessing home without auth', async ({ page }) => {
    await page.goto('/home');
    await expect(page).toHaveURL(/\/login/);
  });

  test('should logout successfully', async ({ page }) => {
    await loginPage.login(VALID_CREDENTIALS.username, VALID_CREDENTIALS.password);
    await expect(page).toHaveURL(/\/home/);

    await page.goto('/login');
    await expect(page.locator('button.login-button')).toBeVisible();
  });
});
