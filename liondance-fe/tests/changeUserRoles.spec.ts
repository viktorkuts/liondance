import { test, expect } from "@playwright/test";

test("register student", async ({
  page,
}) => {
await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
await page.getByText('Admin').click();
await page.getByRole('link', { name: 'Users' }).click();
await page.getByRole('row', { name: 'Sarah Jane Smith Sarah.Smith@' }).getByRole('button').click();
await page.getByRole('button', { name: 'Change Roles' }).click();
await page.locator('div').filter({ hasText: /^STAFF$/ }).nth(2).click();
await page.getByRole('option', { name: 'CLIENT' }).click();
await page.locator('div').filter({ hasText: /^STAFFCLIENT$/ }).nth(1).click();
await page.getByRole('button', { name: 'Save Roles' }).click();
await expect(page.getByText('Roles updated successfully.')).toBeVisible();
await page.getByRole('button', { name: 'Change Roles' }).click();
await page.getByLabel('Change Roles').locator('button').nth(2).click();
await page.getByRole('button', { name: 'Save Roles' }).click();
await expect(page.getByText('Roles updated successfully.')).toBeVisible();
});