import { test, expect } from "@playwright/test";

test.use({ storageState: "playwright/.auth/client.json" });

test("view your events", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("link", { name: "Your Events" })).toBeVisible();
  await page.getByRole("link", { name: "Your Events" }).click();
  await expect(
    page.getByRole("heading", { name: "Your Events" })
  ).toBeVisible();
  await expect(page.getByRole("cell", { name: "Location" })).toBeVisible();
  await expect(
    page.getByRole("cell", { name: "Event Date & Time" })
  ).toBeVisible();
  await expect(page.getByRole("cell", { name: "Event Type" })).toBeVisible();
  await expect(
    page.getByRole("cell", { name: "Special Request", exact: true })
  ).toBeVisible();
  await expect(page.getByRole("cell", { name: "Event Status" })).toBeVisible();
});
