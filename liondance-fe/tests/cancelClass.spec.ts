import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("can cancel a class", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Cancel Course" }).click();
  await expect(page.locator("tbody")).toContainText("Martial Arts");
  await expect(page.locator("tbody")).toContainText("SUNDAY");
  await expect(page.locator("tbody")).toContainText("10:00 AM - 12:00 AM");
  await expect(page.locator("tbody")).toContainText("Sarah Jane Smith");
  await page.locator('button:has(abbr[aria-label="February 9, 2025"])').click();
  await page.getByRole("button", { name: "Confirm" }).click();
  await expect(
    page.getByText("Failed to cancel course. Please try again later.")
  ).toBeHidden();
});
