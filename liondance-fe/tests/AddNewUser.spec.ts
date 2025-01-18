import { test, expect } from "@playwright/test";

test("register student", async ({
  page,
}) => {
await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
await page.getByText('Admin').click();
await page.getByRole('link', { name: 'Users' }).click();
await page.getByRole('button', { name: 'Add New User' }).click();
await expect(page.getByRole('heading', { name: 'Add New User' })).toBeVisible();await page.getByPlaceholder('John').click();
await page.getByPlaceholder('John').fill('Chie');
await page.getByPlaceholder('Doe').click();
await page.getByPlaceholder('Doe').fill('Satonaka');
await page.getByPlaceholder('Select gender').click();
await page.getByRole('option', { name: 'FEMALE' }).click();
await page.getByPlaceholder('YYYY-MM-DD').click();
await page.locator('div').filter({ hasText: /^December 2024$/ }).getByRole('button').first().click();
await page.getByRole('button', { name: 'November 2024', exact: true }).click();
await page.locator('div').filter({ hasText: /^2024$/ }).getByRole('button').first().click();
await page.locator('div').filter({ hasText: /^2023$/ }).getByRole('button').first().click();
await page.getByRole('button', { name: '2022' }).click();
await page.locator('.m_730a79ed > button').first().click();
await page.locator('.m_730a79ed > button').first().click();
await page.getByRole('button', { name: '2002' }).click();
await page.getByRole('button', { name: 'Mar' }).click();
await page.getByLabel('14 March').click();
await page.getByPlaceholder('example@example.com').click();
await page.getByPlaceholder('example@example.com').fill('ChieTheGoat@persona.com');
await page.getByPlaceholder('Main St').click();
await page.getByPlaceholder('Main St').fill('123 Main Street');
await page.getByPlaceholder('City').click();
await page.getByPlaceholder('City').fill('Inaba');
await page.getByPlaceholder('State/Province').click();
await page.getByPlaceholder('State/Province').fill('Quebec');
await page.getByPlaceholder('12345').click();
await page.getByPlaceholder('12345').fill('H');
await page.getByPlaceholder('Select role').click();
await page.getByRole('option', { name: 'STAFF' }).click();
await page.getByRole('button', { name: 'Submit' }).click();
await page.getByPlaceholder('12345').click();
await expect(page.getByText('Invalid postal code')).toBeVisible();
await page.getByPlaceholder('12345').click();
await page.getByPlaceholder('12345').fill('A1A 1A1');
await page.getByRole('button', { name: 'Submit' }).click();
await expect(page.getByText('Success', { exact: true })).toBeVisible();
});