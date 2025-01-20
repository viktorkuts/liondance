import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("can edit specific user details", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Users" }).click();
  await expect(page.locator("tbody")).toContainText("Zippora Lemony Snickett");
  await expect(page.locator("tbody")).toContainText(
    "zippora.snickett@stories.org"
  );
  await page
    .getByRole("row", { name: "Zippora Lemony Snickett" })
    .getByRole("button")
    .click();
  await expect(page.locator("#root")).toContainText(
    "Name: Zippora Lemony Snickett"
  );
  await expect(page.locator("#root")).toContainText(
    "Email: zippora.snickett@stories.org"
  );
  await page.getByRole("button", { name: "Edit User" }).click();
  await page.getByPlaceholder("John", { exact: true }).click();
  await page.getByPlaceholder("John", { exact: true }).fill("Zipporas");
  await page.getByPlaceholder("Pork").click();
  await page.getByPlaceholder("Pork").fill("Snicketts");
  await page.getByPlaceholder("john.doe@example.com").click();
  await page
    .getByPlaceholder("john.doe@example.com")
    .fill("zippora.snickett@stories.com");
  await page.getByPlaceholder("Quebec").click();
  await page.getByRole("option", { name: "Quebec" }).click();
  await page.getByRole("button", { name: "Update User" }).click();
  await expect(page.locator("#root")).toContainText(
    "Name: Zipporas Lemony Snicketts"
  );
  await expect(page.locator("#root")).toContainText(
    "Email: zippora.snickett@stories.com"
  );
  await page.getByRole("button", { name: "Back to User List" }).click();
  await expect(page.locator("tbody")).toContainText(
    "Zipporas Lemony Snicketts"
  );
  await expect(page.locator("tbody")).toContainText(
    "zippora.snickett@stories.com"
  );
  await page
    .getByRole("row", { name: "Zipporas Lemony Snicketts" })
    .getByRole("button")
    .click();
  await page.getByRole("button", { name: "Edit User" }).click();
  await page.getByPlaceholder("John", { exact: true }).click();
  await page.getByPlaceholder("John", { exact: true }).fill("Zippora");
  await page.getByPlaceholder("Pork").click();
  await page.getByPlaceholder("Pork").fill("Snickett");
  await page.getByPlaceholder("john.doe@example.com").click();
  await page
    .getByPlaceholder("john.doe@example.com")
    .fill("zippora.snickett@stories.org");
  await page.getByRole("button", { name: "Update User" }).click();
  await expect(page.locator("#root")).toContainText(
    "Email: zippora.snickett@stories.org"
  );
  await page.getByRole("button", { name: "Back to User List" }).click();
  await expect(page.locator("tbody")).toContainText("Zippora Lemony Snickett");
  await expect(page.locator("tbody")).toContainText(
    "zippora.snickett@stories.org"
  );
});
