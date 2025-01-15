import { test, expect } from '@playwright/test';

test.use({
 viewport: { width: 1920, height: 1080 },
});

test('admin can view client feedback on event', async ({ page }) => {
  await page.goto('http://localhost:5173/');
  await page.getByText('Admin').click();
  await page.getByRole('link', { name: 'Events' }).click();
  await page.getByRole('row', { name: 'Rio Da Yung OG rioodayungog@' }).getByRole('button').nth(2).click();
  await expect(page.locator('#root')).toContainText('Feedback for Event');
  await page.getByRole('button', { name: 'Back to Events' }).click();

});