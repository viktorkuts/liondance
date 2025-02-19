import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("can add performers to a specific event", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Events", exact: true }).click();
  await page.getByText("▶").first().click();
  await page
    .getByRole("cell", { name: "Alice Johnson" })
    .first()
    .first()
    .click();
  await page.getByRole("button", { name: "Add Performers" }).click();
  await page.getByPlaceholder("Pick performers").click();
  await page.getByRole("option", { name: "Apollo Lightning" }).click();
  await page.getByRole("option", { name: "Zippora Snickett" }).click();
  await page.getByText("Select Performers to add").click();
  await page.getByLabel("Add Performers").locator("button").nth(3).click();
  await page.getByPlaceholder("Pick performers").click();
  await page.getByRole("option", { name: "Zippora Snickett" }).click();
  await page.getByText("Apollo Lightning").click();
  await page.getByText("Current Performers").click();
  await page.getByPlaceholder("Pick performers").press("Enter");
  await page.getByPlaceholder("Pick performers").press("Enter");
  await page.getByText("Current Performers").click();
  await page.getByPlaceholder("Pick performers").click();
  await page.getByText("Current Performers").click();
  await page
    .getByLabel("Add Performers")
    .getByRole("button", { name: "Add Performers" })
    .click();
  await expect(page.getByRole("cell", { name: "Sarah Smith" })).toBeVisible();
  await expect(
    page.getByRole("cell", { name: "Apollo Lightning" })
  ).toBeVisible();
  await page.getByRole("banner").getByRole("button").click();
});

test("can remove performers to a specific event", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Events", exact: true }).click();
  await page.getByText("▶").first().click();
  await page
    .getByRole("cell", { name: "Alice Johnson" })
    .first()
    .first()
    .click();
  await page.getByRole("button", { name: "Add Performers" }).click();
  await page.getByPlaceholder("Pick performers").click();
  await page.getByRole("option", { name: "Apollo Lightning" }).click();
  await page.getByRole("option", { name: "Zippora Snickett" }).click();
  await page.getByText("Select Performers to add").click();
  await page.getByLabel("Add Performers").locator("button").nth(3).click();
  await page.getByPlaceholder("Pick performers").click();
  await page.getByRole("option", { name: "Zippora Snickett" }).click();
  await page.getByText("Apollo Lightning").click();
  await page.getByText("Current Performers").click();
  await page.getByPlaceholder("Pick performers").press("Enter");
  await page.getByPlaceholder("Pick performers").press("Enter");
  await page.getByText("Current Performers").click();
  await page.getByPlaceholder("Pick performers").click();
  await page.getByText("Current Performers").click();
  await page
    .getByLabel("Add Performers")
    .getByRole("button", { name: "Add Performers" })
    .click();
  await expect(page.getByRole("cell", { name: "Sarah Smith" })).toBeVisible();
  await expect(
    page.getByRole("cell", { name: "Apollo Lightning" })
  ).toBeVisible();
  await page.getByRole("button", { name: "Remove Performers" }).click();
  await page
    .getByRole("textbox", { name: "Select Performers to remove" })
    .click();
  await page.getByRole("option", { name: "Sarah Smith" }).click();
  await page.getByRole("heading", { name: "Current Performers" }).click();
  await page
    .getByLabel("Remove Performers")
    .getByRole("button", { name: "Remove Performers" })
    .click();
  await expect(page.getByRole("cell", { name: "Sarah Smith" })).toBeHidden();
  await expect(
    page.getByRole("cell", { name: "Apollo Lightning" })
  ).toBeVisible();
});
