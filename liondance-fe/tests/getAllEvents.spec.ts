import { test, expect } from '@playwright/test';

test.use({
    viewport: { width: 1800, height: 1080 },
  });

  test('test', async ({ page }) => {
    await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
    await expect(page.getByText('ReviewsContactCalendarBook')).toBeVisible();
    await expect(page.getByText('Admin')).toBeVisible();
    await page.getByText('Admin').click();
    await expect(page.getByRole('link', { name: 'Events' })).toBeVisible();
    await page.getByRole('link', { name: 'Events' }).click();
    await expect(page.getByText('All EventsNameEmailPhoneLocationEvent Date & TimeEvent TypeSpecial RequestEvent')).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Rio Da Yung OG' })).toBeVisible();
  });