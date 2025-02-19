import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });

test("test", async ({ page }) => {
  await page.goto("/classfeedback/reports");
  await expect(page.getByText("Class Feedback Reports")).toBeVisible();
  if (await page.getByText("No reports available.").isVisible()) {
    return;
  } else {
    await expect(page.locator("td").first()).toBeVisible();
    await expect(page.locator("td:nth-child(2)").first()).toBeVisible();
    await expect(page.locator(".download-button").first()).toBeVisible();
  }
});
