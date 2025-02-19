import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("admin can contact client", async ({ page }) => {
  await page.goto("/");
    await expect(page.getByText('Admin')).toBeVisible();
    await page.getByText('Admin').click();
    await expect(page.getByRole('link', { name: 'Events', exact: true })).toBeVisible();
    await page.getByRole('link', { name: 'Events', exact: true }).click();
    await page.getByText('Promotions!ReviewsUpcoming').click();
    await page.getByText('▶').first().click();
    await expect(page.locator('.button_contact_client').first()).toBeVisible();
    await page.locator('.button_contact_client').first().click();
    await expect(page.getByText('Contact Alice Johnson Best')).toBeVisible();
    await expect(page.getByPlaceholder('Enter your message here...')).toBeVisible();
    await expect(page.getByText('Include contact information')).toBeVisible();
    await page.getByText('Include contact information').click();
    await page.getByText('Include contact information').dblclick();
    await expect(page.getByRole('button', { name: 'Cancel', exact: true })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Send' })).toBeVisible();
    await page.getByRole('button', { name: 'Send' }).click();
});