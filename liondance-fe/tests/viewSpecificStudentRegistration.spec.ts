import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });

test("can view specific student registration", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByText("Welcome!")).toBeVisible();
  await expect(
    page.getByRole("link", { name: "Pending Registrations" })
  ).toBeVisible();
  await page.getByRole("link", { name: "Pending Registrations" }).click();
  await expect(page.getByText("Pending Registrations").nth(1)).toBeVisible();
  await expect(
    page.getByRole("heading", { name: "Pending Registrations" })
  ).toBeVisible();
  await expect(page.getByRole("cell", { name: "Name" })).toBeVisible();
  await expect(page.getByRole("cell", { name: "Email" })).toBeVisible();
  await expect(page.getByRole("cell", { name: "Date of Birth" })).toBeVisible();
  await expect(
    page.getByRole("cell", { name: "donald@trump.org" })
  ).toBeVisible();
  await page.getByRole("cell", { name: "donald@trump.org" }).click();
  await expect(page.getByText("Student DetailsFirst")).toBeVisible();
  await expect(
    page.getByRole("heading", { name: "Student Details" })
  ).toBeVisible();
  await expect(page.getByText("Email Addressdonald@trump.org")).toBeVisible();
  await expect(
    page.locator("div").filter({ hasText: /^donald@trump\.org$/ })
  ).toBeVisible();
  await expect(page.getByRole("button", { name: "Close" })).toBeVisible();
  await page.getByRole("button", { name: "Close" }).click();
});
