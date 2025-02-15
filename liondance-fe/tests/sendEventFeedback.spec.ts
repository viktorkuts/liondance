import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/client.json" });
test("client can submit feedback for COMPLETED event", async ({ page }) => {
    await page.goto("/");
    await page.getByRole('link', { name: 'Your Events' }).click();
    await expect(page.locator('tbody')).toContainText('COMPLETED');
    await page.getByRole('button', { name: 'Give Feedback' }).click();
    await page.getByLabel('Rating (1-5):').dblclick();
    await page.getByLabel('Rating (1-5):').fill('01');
    await page.getByLabel('Rating (1-5):').press('ArrowLeft');
    await page.getByLabel('Rating (1-5):').fill('1');
    await page.getByLabel('Comment:').click();
    await page.getByLabel('Comment:').fill('good');
    await page.getByRole('button', { name: 'Submit Feedback' }).click();
    await expect(page.getByRole('paragraph')).toContainText('Feedback submitted successfully!');
});
