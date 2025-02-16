import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("Create and delete promotion", async ({ page }) => {
    await page.goto("/promotions");
    await expect(page.getByRole('button', { name: 'Create Promotion' })).toBeVisible();
    await page.getByRole('button', { name: 'Create Promotion' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('test');
    await page.getByRole('spinbutton').click();
    await page.getByRole('spinbutton').fill('20');
    await page.locator('div').filter({ hasText: /^Start Date$/ }).getByRole('textbox').fill('2025-03-15');
    await page.locator('div').filter({ hasText: /^End Date$/ }).getByRole('textbox').fill('2025-05-15');
    await page.getByRole('button', { name: 'Create Promotion' }).click();
    await expect(page.getByText('test', { exact: true })).toBeVisible();
    await page.getByText('testDiscount: 20%Status:').click();
    await expect(page.getByRole('button', { name: 'Delete' })).toBeVisible();
    await page.getByRole('button', { name: 'Delete' }).click();
});
