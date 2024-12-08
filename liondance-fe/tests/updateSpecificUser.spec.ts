import { test, expect } from '@playwright/test';

test.use({
 viewport: { width: 1920, height: 1080 },
});

test('can edit specific user details', async ({ page }) => {
 await page.goto('http://localhost:5173/');
 await expect(page.getByText('ReviewsContactCalendarBookingRegistrationPending RegistrationsLoginAdmin')).toBeVisible();
 await expect(page.getByRole('link', { name: 'Admin' })).toBeVisible();
 await page.getByRole('link', { name: 'Admin' }).click();
 await expect(page.getByText('User ListNameEmailGenderDate')).toBeVisible();
 await expect(page.getByRole('heading', { name: 'User List' })).toBeVisible();
 await expect(page.getByRole('cell', { name: 'Name' })).toBeVisible();
 await expect(page.getByRole('cell', { name: 'Email', exact: true })).toBeVisible();
 await expect(page.getByRole('cell', { name: 'Gender' })).toBeVisible();
 await expect(page.getByRole('cell', { name: 'Date of Birth' })).toBeVisible();
 await expect(page.getByRole('cell', { name: 'Actions' })).toBeVisible();
 await expect(page.getByRole('cell', { name: 'Nikolaos Georgios' })).toBeVisible();
 await expect(page.getByRole('row', { name: 'Nikolaos Georgios' }).getByRole('cell').nth(4)).toBeVisible();
 await expect(page.getByRole('row', { name: 'Nikolaos Georgios' }).getByRole('button')).toBeVisible();
 await page.getByRole('row', { name: 'Nikolaos Georgios' }).getByRole('button').click();
 await expect(page.getByText('Nikolaos Georgios MichaloliakosGender: MALEDate of Birth: 2000-01-01Email:')).toBeVisible();
 await expect(page.getByRole('heading', { name: 'Nikolaos Georgios' })).toBeVisible();
 await expect(page.getByText('Gender: MALE')).toBeVisible();
 await expect(page.getByText('Date of Birth: 2000-01-')).toBeVisible();
 await expect(page.getByText('Email: nikolaos.michaloliakos')).toBeVisible();
 await expect(page.getByText('Phone:')).toBeVisible();
 await expect(page.getByText('Address: 1234 Main St, Montreal')).toBeVisible();
 await page.getByRole('button', { name: 'Edit Profile' }).click();
 await page.getByPlaceholder('City').click();
 await page.getByPlaceholder('City').fill('Quebec');
 await page.getByRole('button', { name: 'Confirm' }).click();
 await expect(page.locator('#root')).toContainText('Address: 1234 Main St, Quebec, QC, H1H 1H1');
 await page.getByRole('button', { name: 'Edit Profile' }).click();
 await page.getByPlaceholder('City').click();
 await page.getByPlaceholder('City').fill('Montreal');
 await page.getByRole('button', { name: 'Confirm' }).click();
 await expect(page.locator('#root')).toContainText('Address: 1234 Main St, Montreal, QC, H1H 1H1');

});