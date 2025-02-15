// import { test, expect } from "@playwright/test";

// test.use({
//     viewport: { width: 1920, height: 1080 },
// });
// test.use({ storageState: "playwright/.auth/client.json" });
// test("subcribe to promotion emails", async ({ page }) => {
//   await page.goto("/");
//   await page.getByRole('button', { name: 'Continue', exact: true }).click();
//   await expect(page.locator('#root')).toContainText('Promotions!');
//   await page.getByRole('link', { name: 'Promotions!' }).click();
//   await page.getByLabel('I want to receive promotional').check();
//   await page.getByRole('button', { name: 'Submit' }).click();
//   await page.getByRole('link', { name: 'Promotions!' }).click();
//   await page.getByRole('button', { name: 'Submit' }).click();
//   //recieved email, show class or something lol
// });
