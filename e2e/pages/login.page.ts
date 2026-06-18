import { Page, Locator } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly errorMessage: Locator;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = page.locator('input[name="user"]');
    this.passwordInput = page.locator('input[name="password"]');
    this.loginButton = page.locator('button.login-button');
    this.errorMessage = page.locator('mat-error');
  }

  async goto() {
    await this.page.goto('/login');
    await this.page.waitForSelector('button.login-button');
  }

  async login(username: string, password: string) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
  }

  async getErrorMessage() {
    return this.errorMessage.textContent();
  }
}
