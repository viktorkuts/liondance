import { test, expect } from '@playwright/test';

test('view pending registrations', async ({page}) => {
await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
await page.getByRole('link', { name: 'Pending Registrations' }).click();
await expect(page.getByRole('heading', { name: 'Pending Registrations' })).toBeVisible();
await expect(page.getByRole('cell', { name: 'Name' })).toBeVisible();
await page.getByRole('cell', { name: 'Email' }).click();
await page.getByRole('cell', { name: 'Gender' }).click();
await page.getByRole('cell', { name: 'Date of Birth' }).click();
} )