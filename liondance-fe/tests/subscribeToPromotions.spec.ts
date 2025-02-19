import { test, expect } from "@playwright/test";

test.use({
    viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/client.json" });
test("subcribe to promotion emails", async ({ page }) => {
    await page.goto("/");
    await expect(page.locator('#root')).toContainText('Promotions!');
    await page.getByRole('link', { name: 'Promotions!' }).click();
    await expect(page.getByRole("checkbox")).toBeVisible();
    await page.getByLabel('I want to receive promotional emails').check();
    await page.getByRole('button', { name: 'Submit' }).click();
    //recieved email, show class or something lol
});