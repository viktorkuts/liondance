import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("admin can get list of all clients", async ({ page }) => {
    await page.goto('http://localhost:5173/');
    await page.getByRole('link', { name: 'Client List' }).click();
    await expect(page.locator('h1')).toContainText('Client List');
    await expect(page.locator('tbody')).toContainText('Alice Grace Johnson');
  });