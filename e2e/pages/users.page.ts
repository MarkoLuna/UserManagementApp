import { Page, Locator } from '@playwright/test';

export class UsersPage {
  readonly page: Page;
  readonly newUserButton: Locator;
  readonly userTable: Locator;
  readonly tableRows: Locator;
  readonly nameInput: Locator;
  readonly ageInput: Locator;
  readonly salaryInput: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;
  readonly alertMessage: Locator;

  constructor(page: Page) {
    this.page = page;
    this.newUserButton = page.getByRole('button', { name: 'New User' });
    this.userTable = page.locator('table[mat-table]');
    this.tableRows = page.locator('table[mat-table] tbody tr[mat-row], table[mat-table] tbody tr.mat-mdc-row');
    this.nameInput = page.locator('input[placeholder="Name"]');
    this.ageInput = page.locator('input[placeholder="Age"]');
    this.salaryInput = page.locator('input[placeholder="Salary"]');
    this.submitButton = page.getByRole('button', { name: 'Submit' });
    this.cancelButton = page.getByRole('button', { name: 'Cancel' });
    this.alertMessage = page.locator('mat-card-content.mat-body-1');
  }

  async goto() {
    await this.page.goto('/home');
    await this.waitForTableLoad();
  }

  async waitForTableLoad() {
    await this.page.waitForSelector('table[mat-table] tbody tr[mat-row], table[mat-table] tbody tr.mat-mdc-row');
  }

  async getUsers() {
    return this.tableRows;
  }

  async getUserCount() {
    return this.tableRows.count();
  }

  async getUserByName(name: string) {
    return this.userTable.locator('tbody tr[mat-row], tbody tr.mat-mdc-row').filter({ hasText: name });
  }

  async createUser(name: string, age: string | number, salary: string | number) {
    await this.newUserButton.click();
    await this.nameInput.fill(String(name));
    await this.ageInput.fill(String(age));
    await this.salaryInput.fill(String(salary));
    await this.submitButton.click();
  }

  async clickEditOnUser(name: string) {
    const row = await this.getUserByName(name);
    await row.locator('button[mat-icon-button] mat-icon:has-text("edit")').click();
  }

  async clickViewOnUser(name: string) {
    const row = await this.getUserByName(name);
    await row.locator('button[mat-icon-button] mat-icon:has-text("visibility")').click();
  }

  async clickDeleteOnUser(name: string) {
    const row = await this.getUserByName(name);
    await row.locator('button[mat-icon-button] mat-icon:has-text("delete")').click();
  }

  async updateForm(name: string, age: string | number, salary: string | number) {
    await this.nameInput.fill(String(name));
    await this.ageInput.fill(String(age));
    await this.salaryInput.fill(String(salary));
    await this.submitButton.click();
  }

  async getAlertMessage() {
    const alert = this.alertMessage;
    await alert.waitFor({ state: 'visible', timeout: 10000 });
    return (await alert.textContent())?.trim() ?? '';
  }

  async fillForm(name: string, age: string | number, salary: string | number) {
    await this.nameInput.fill(String(name));
    await this.ageInput.fill(String(age));
    await this.salaryInput.fill(String(salary));
  }
}
