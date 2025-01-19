import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("can view student information", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Students" }).click();
  await expect(page.locator("tbody")).toContainText(
    "Nikolaos Georgios Michaloliakos"
  );
  await page
    .getByRole("row", { name: "Nikolaos Georgios" })
    .getByRole("button")
    .click();
  await expect(page.locator("#root")).toContainText(
    "Name: Nikolaos Georgios Michaloliakos"
  );
});
