import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });

test("can view student information", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Students" }).click();
  await expect(page.locator("tbody")).toContainText("Zippora Lemony Snickett");
  await page
    .getByRole("row", { name: "Zippora Lemony Snickett" })
    .getByRole("button")
    .click();
  await expect(page.locator("#root")).toContainText(
    "Name: Zippora Lemony Snickett"
  );
});
