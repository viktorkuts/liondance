import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});

test("can cancel a class", async ({ page }) => {
    await page.goto('/');
    await page.getByRole('link', { name: 'Cancel a Course' }).click();
    await expect(page.locator('tbody')).toContainText('Martial Arts');
    await expect(page.locator('tbody')).toContainText('SUNDAY');
    await expect(page.locator('tbody')).toContainText('10:00 AM - 12:00 AM');
    await expect(page.locator('tbody')).toContainText('Sarah Jane Smith');
    await page.locator('button:has(abbr[aria-label="February 9, 2025"])').click();
    await page.getByRole('button', { name: 'Confirm' }).click();
});