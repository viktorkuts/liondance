import { test, expect } from '@playwright/test';

test.use({
    viewport: { width: 1800, height: 1080 },
  });
  
test('can view specific student registration', async ({ page }) => {
    await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
    await expect(page.getByText('ReviewsContactCalendarBook')).toBeVisible();
    await expect(page.getByRole('link', { name: 'Pending Registrations' })).toBeVisible();
    await page.getByRole('link', { name: 'Pending Registrations' }).click();
    await expect(page.getByText('ReviewsContactCalendarBook')).toBeVisible();
    await expect(page.getByText('Pending RegistrationsNameEmailGenderDate of BirthNikolaos Michaloliakosnikolaos')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Pending Registrations' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Name' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Email' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Gender' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Date of Birth' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'nikolaos.michaloliakos@' })).toBeVisible();
    await page.getByRole('cell', { name: 'nikolaos.michaloliakos@' }).click();
    await expect(page.getByText('Student DetailsFirst')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Student Details' })).toBeVisible();
    await expect(page.getByText('Email Addressnikolaos.')).toBeVisible();
    await expect(page.getByText('Email Address')).toBeVisible();
    await expect(page.locator('div').filter({ hasText: /^nikolaos\.michaloliakos@goldendawn\.org$/ })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Close' })).toBeVisible();
    await page.getByRole('button', { name: 'Close' }).click();
});

