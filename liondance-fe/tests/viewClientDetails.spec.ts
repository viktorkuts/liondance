import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("admin can view client details", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Client List" }).click();
  await page.waitForTimeout(750);
  await expect(
    page.getByRole("heading", { name: "Client List" })
  ).toBeVisible();
  await page
    .getByRole("row", { name: "Alice Grace Johnson alice." })
    .getByRole("button")
    .click();
  await expect(page.locator("h1")).toContainText("Client Profile");
  await expect(page.locator("#root")).toContainText("Active Events");
  await expect(page.locator("#root")).toContainText("Past Events");
});
