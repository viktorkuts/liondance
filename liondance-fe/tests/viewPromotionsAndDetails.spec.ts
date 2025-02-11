import { test, expect } from "@playwright/test";

test.use({ storageState: "playwright/.auth/staff.json" });

test("view promotions and details", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Promotions" }).click();
  await expect(page.getByText("PromotionsSummer SaleDiscount")).toBeVisible();
  await page.getByText("Summer Sale").click();
  await expect(page.getByText("Summer SaleDiscount Rate: 20%")).toBeVisible();
  await page.getByRole("link", { name: "Back" }).click();
  await expect(page.getByText("Summer SaleDiscount: 20%")).toBeVisible();
});
