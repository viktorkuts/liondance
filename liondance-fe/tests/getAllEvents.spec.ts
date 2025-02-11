import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });

test("test", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByText("Welcome!")).toBeVisible();
  await expect(page.getByText("Admin")).toBeVisible();
  await page.getByText("Admin").click();
  await expect(
    page.getByRole("link", { name: "Events", exact: true })
  ).toBeVisible();
  await page.getByRole("link", { name: "Events", exact: true }).click();
  await expect(page.getByPlaceholder("Search events...")).toBeVisible();
  await page.getByText("All Events").click();
  await expect(
    page.getByRole("cell", { name: "Alice Johnson" }).first()
  ).toBeVisible();
});
