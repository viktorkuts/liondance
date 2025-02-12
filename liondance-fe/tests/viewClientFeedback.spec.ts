import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("admin can view client feedback on event", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Events", exact: true }).click();
  await page.getByText("All Events").click();
  await page
    .getByRole("row", { name: "1 Alice Johnson alice.johnson" })
    .getByRole("button")
    .nth(2)
    .click();
  await expect(page.locator("#root")).toContainText("Feedback for Event");
  await page.getByRole("button", { name: "Back to Events" }).click();
});
