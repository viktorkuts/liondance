import { test, expect } from "@playwright/test";
test.use({ storageState: "playwright/.auth/staff.json" });
test("upon approve remove from pending and add to users list", async ({
  page,
}) => {
  await page.goto("/");
  await expect(page.getByText("Welcome!")).toBeVisible();
  await expect(page.getByText("Admin")).toBeVisible();
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Pending Registrations" }).click();
  await page.goto("/pending-registrations");
  await page.waitForTimeout(1000);
  await expect(page.getByRole("cell", { name: "John Pork" })).toBeVisible();
  await page.getByRole("cell", { name: "John Pork" }).click();
  await expect(page.getByText("Student DetailsFirst")).toBeVisible();
  await page.getByRole("button", { name: "Approve" }).click();
  await expect(page.getByRole("cell", { name: "John Pork" })).toHaveCount(0);
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Students" }).click();
  await page.getByPlaceholder("Select statuses").click();
  await page.getByRole("option", { name: "ACTIVE", exact: true }).click();
  await page.goto("/users");
  await page.waitForTimeout(1000);
  await expect(
    page.getByRole("cell", { name: "John Doesounow Pork" })
  ).toBeVisible();
});

test("upon deny delete student", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByText("Welcome!")).toBeVisible();
  await expect(page.getByText("Admin")).toBeVisible();
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Pending Registrations" }).click();
  await page.waitForTimeout(1000);
  await expect(page.getByRole("cell", { name: "Tim Robinson" })).toBeVisible();
  await page.getByRole("cell", { name: "Tim Robinson" }).click();
  await expect(page.getByText("Student DetailsFirst")).toBeVisible();
  await page.getByRole("button", { name: "Deny" }).click();
  await expect(page.getByRole("cell", { name: "Tim Robinson" })).toHaveCount(0);
  await page.waitForTimeout(1000);
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Students" }).click();
  await page.getByPlaceholder("Select statuses").click();
  await page.getByRole("option", { name: "INACTIVE", exact: true }).click();
  await page.waitForTimeout(250);
  await expect(page.getByRole("cell", { name: "Tim Robinson" })).toHaveCount(0);
});
