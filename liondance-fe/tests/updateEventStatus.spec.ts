import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("can edit specific event status to CONFIRMED", async ({ page }) => {
  await page.goto("/events");
  await page.getByText("All Events").click();
  await expect(page.locator("tbody")).toContainText("PENDING");
  await page
    .getByRole("row", { name: "8 Maria Garcia maria.garcia@" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await page
    .getByRole("row", { name: "8 Maria Garcia maria.garcia@" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(
    await page.getByRole("button", { name: "Update Status" })
  ).toBeVisible();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
});

test("can edit specific event status to PENDING", async ({ page }) => {
  await page.goto("/events");
  await page.getByText("All Events").click();
  await expect(page.locator("tbody")).toContainText("PENDING");
  await page
    .getByRole("row", { name: "8 Maria Garcia maria.garcia@" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await page
    .getByRole("row", { name: "8 Maria Garcia maria.garcia@" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
});

test("can edit specific event status to CANCELLED", async ({ page }) => {
  await page.goto("/events");
  await page.getByText("All Events").click();
  await expect(page.locator("tbody")).toContainText("PENDING");
  await page
    .getByRole("row", { name: "8 Maria Garcia maria.garcia@" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CANCELLED" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await page
    .getByRole("row", { name: "8 Maria Garcia maria.garcia@" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
});
