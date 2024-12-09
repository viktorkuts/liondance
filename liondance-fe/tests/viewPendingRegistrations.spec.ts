import { test, expect } from '@playwright/test';

test('view pending registrations', async ({page}) => {
await page.goto('http://localhost:5173/');
await page.getByRole('link', { name: 'Pending Registrations' }).click();
await expect(page.getByRole('heading', { name: 'Pending Registrations' })).toBeVisible();
await expect(page.getByRole('cell', { name: 'Name' })).toBeVisible();
await page.getByRole('cell', { name: 'Email' }).click();
await page.getByRole('cell', { name: 'Gender' }).click();
await page.getByRole('cell', { name: 'Date of Birth' }).click();
} )