import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("can reschedule an event at thier will", async ({ page }) => {
await page.goto('/events');
await expect(page.locator('tbody')).toContainText('2028-12-31, 07:00:00 p.m.');
await expect(page.locator('tbody')).toContainText('Joshua Block');
await page.getByRole('row', { name: 'Joshua Block joshblock@' }).getByRole('button').nth(1).click();
await page.locator('div').filter({ hasText: /^New Date$/ }).click();
await page.getByRole('button', { name: 'January 2025', exact: true }).click();
await page.getByRole('button', { name: 'Dec' }).click();
await page.getByLabel('31 December').click();
await page.locator('div').filter({ hasText: /^New Time$/ }).getByRole('textbox').click();
await page.getByRole('option', { name: '7:00 PM' }).click();
await page.getByLabel('Reschedule Event').getByRole('button', { name: 'Reschedule' }).click();
await expect(page.locator('tbody')).toContainText('2025-12-31, 07:00:00 p.m.');
await expect(page.locator('tbody')).toContainText('Joshua Block');
await page.getByRole('row', { name: 'Joshua Block joshblock@' }).getByRole('button').nth(1).click();
await page.locator('div').filter({ hasText: /^New Date$/ }).click();
await page.getByRole('button', { name: 'January 2025', exact: true }).click();
await page.getByRole('button', { name: '2025' }).click();
await page.getByRole('button', { name: '2028' }).click();
await page.getByRole('button', { name: 'Dec' }).click();
await page.getByLabel('31 December').click();
await page.locator('div').filter({ hasText: /^New Time$/ }).getByRole('textbox').click();
await page.getByRole('option', { name: '7:00 PM' }).click();
await page.getByLabel('Reschedule Event').getByRole('button', { name: 'Reschedule' }).click();
await expect(page.locator('tbody')).toContainText('2028-12-31, 07:00:00 p.m.');
await expect(page.locator('tbody')).toContainText('Joshua Block');
});
