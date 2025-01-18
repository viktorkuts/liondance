import { test, expect } from '@playwright/test';

test.use({
  viewport: { width: 1800, height: 1080 },
});

test('view specific user details test', async ({ page }) => {
  await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
  await expect(page.getByText('ReviewsContactCalendarBook')).toBeVisible();
  await expect(page.getByText('Admin')).toBeVisible();
  await page.getByText('Admin').click();
  await expect(page.getByText('Users')).toBeVisible();
 await page.getByText('Users').click();
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
  await expect(page.getByText('Address: 1234 Main St,')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Back to User List' })).toBeVisible();
  await page.getByRole('button', { name: 'Back to User List' }).click();
});