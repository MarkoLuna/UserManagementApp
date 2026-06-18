import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/login.page';
import { UsersPage } from '../pages/users.page';
import { SEED_USERS, VALID_CREDENTIALS } from '../fixtures/test-data';

test.describe('User CRUD Operations', () => {
  let usersPage: UsersPage;

  test.beforeEach(async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login(VALID_CREDENTIALS.username, VALID_CREDENTIALS.password);
    await expect(page).toHaveURL(/\/home/);

    usersPage = new UsersPage(page);
    await usersPage.waitForTableLoad();
  });

  test.describe('Read', () => {
    test('should display seed users in the table', async () => {
      const count = await usersPage.getUserCount();
      expect(count).toBeGreaterThanOrEqual(SEED_USERS.length);

      for (const seedUser of SEED_USERS) {
        const row = await usersPage.getUserByName(seedUser.name);
        await expect(row).toBeVisible();
        await expect(row).toContainText(String(seedUser.age));
      }
    });

    test('should view user details in the form', async () => {
      const targetUser = SEED_USERS[0];
      await usersPage.clickViewOnUser(targetUser.name);

      const nameInput = usersPage.page.locator('input[placeholder="Name"]');
      await expect(nameInput).toHaveValue(targetUser.name);
    });
  });

  test.describe('Create', () => {
    test('should create a new user successfully', async () => {
      const newUser = {
        name: `TestUser_${Date.now()}`,
        age: 25,
        salary: 50000,
      };

      await usersPage.goto();
      await usersPage.createUser(newUser.name, newUser.age, newUser.salary);

      const alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Created successfully');

      const row = await usersPage.getUserByName(newUser.name);
      await expect(row).toBeVisible();
    });

    test('should create multiple users', async () => {
      const users = [
        { name: `Alice_${Date.now()}`, age: 30, salary: 60000 },
        { name: `Bob_${Date.now()}`, age: 28, salary: 45000 },
      ];

      for (const user of users) {
        await usersPage.goto();
        await usersPage.createUser(user.name, user.age, user.salary);
        const alert = await usersPage.getAlertMessage();
        expect(alert).toContain('Created successfully');
      }

      for (const user of users) {
        const row = await usersPage.getUserByName(user.name);
        await expect(row).toBeVisible();
      }
    });
  });

  test.describe('Update', () => {
    test('should update an existing user', async () => {
      const uniqueId = Date.now();
      const tempUser = { name: `TempUpdate_${uniqueId}`, age: 25, salary: 30000 };
      const updatedName = `Updated_${uniqueId}`;

      await usersPage.goto();
      await usersPage.createUser(tempUser.name, tempUser.age, tempUser.salary);
      let alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Created successfully');

      await usersPage.goto();
      await usersPage.clickEditOnUser(tempUser.name);
      await usersPage.updateForm(updatedName, 99, 99999);

      alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Updated successfully');

      const updatedRow = await usersPage.getUserByName(updatedName);
      await expect(updatedRow).toBeVisible();
      await expect(updatedRow).toContainText('99');
      await expect(updatedRow).toContainText('$99,999.00');
    });

    test('should partially update user fields', async () => {
      const uniqueId = Date.now();
      const tempUser = { name: `TempPartial_${uniqueId}`, age: 30, salary: 40000 };

      await usersPage.goto();
      await usersPage.createUser(tempUser.name, tempUser.age, tempUser.salary);
      let alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Created successfully');

      await usersPage.goto();
      await usersPage.clickViewOnUser(tempUser.name);

      const nameInput = usersPage.page.locator('input[placeholder="Name"]');
      const ageInput = usersPage.page.locator('input[placeholder="Age"]');
      const salaryInput = usersPage.page.locator('input[placeholder="Salary"]');

      await nameInput.fill(tempUser.name);
      await ageInput.fill('50');
      await salaryInput.fill('75000');
      await usersPage.submitButton.click();

      alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Updated successfully');

      const row = await usersPage.getUserByName(tempUser.name);
      await expect(row).toBeVisible();
      await expect(row).toContainText('50');
      await expect(row).toContainText('$75,000.00');
    });
  });

  test.describe('Delete', () => {
    test('should delete an existing user', async () => {
      const userToDelete = {
        name: `DeleteMe_${Date.now()}`,
        age: 40,
        salary: 40000,
      };

      await usersPage.goto();
      await usersPage.createUser(userToDelete.name, userToDelete.age, userToDelete.salary);
      await usersPage.waitForTableLoad();

      await usersPage.clickDeleteOnUser(userToDelete.name);

      const alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Deleted successfully');

      const deletedRow = await usersPage.getUserByName(userToDelete.name);
      await expect(deletedRow).toHaveCount(0);
    });
  });

  test.describe('Full Workflow', () => {
    test('should complete full CRUD workflow on a single user', async () => {
      const uniqueId = Date.now();
      const user = {
        name: `CRUD_${uniqueId}`,
        age: 35,
        salary: 55000,
      };
      const updatedUser = {
        name: `CRUD_Updated_${uniqueId}`,
        age: 36,
        salary: 60000,
      };

      // Create
      await usersPage.goto();
      await usersPage.createUser(user.name, user.age, user.salary);

      let alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Created successfully');

      let row = await usersPage.getUserByName(user.name);
      await expect(row).toBeVisible();

      // Read / View
      await usersPage.goto();
      await usersPage.clickViewOnUser(user.name);
      const nameInput = usersPage.page.locator('input[placeholder="Name"]');
      await expect(nameInput).toHaveValue(user.name);

      // Update
      await usersPage.updateForm(updatedUser.name, updatedUser.age, updatedUser.salary);
      alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Updated successfully');

      row = await usersPage.getUserByName(updatedUser.name);
      await expect(row).toBeVisible();
      await expect(row).toContainText(String(updatedUser.age));
      await expect(row).toContainText(updatedUser.salary.toLocaleString('en-US'));

      // Delete
      await usersPage.clickDeleteOnUser(updatedUser.name);
      alert = await usersPage.getAlertMessage();
      expect(alert).toContain('Deleted successfully');

      const deletedRow = await usersPage.getUserByName(updatedUser.name);
      await expect(deletedRow).toHaveCount(0);
    });
  });
});
