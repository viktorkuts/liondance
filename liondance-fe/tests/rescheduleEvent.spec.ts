import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });

const currentDate = new Date();

const { currentMonth } = {
  currentMonth: currentDate.toLocaleString("default", {
    year: "numeric",
    month: "long",
  }),
};

test("can reschedule an event at their will", async ({ page }) => {
  await page.goto("/events");
  await page.getByText("All Events").click();
  await expect(page.locator("tbody")).toContainText(
    "2028-12-31, 07:00:00 p.m."
  );
  await expect(page.locator("tbody")).toContainText("Bob Lee");
  await page
    .getByRole("row", { name: "4 Bob Lee jhondoesnthack@" })
    .getByRole("button")
    .nth(1)
    .click();
  await page
    .locator("div")
    .filter({ hasText: /^New Date$/ })
    .click();
  await page.getByRole("button", { name: currentMonth, exact: true }).click();
  await page.getByRole("button", { name: "Dec" }).click();
  await page.getByLabel("31 December").click();
  await page
    .locator("div")
    .filter({ hasText: /^New Time$/ })
    .getByRole("textbox")
    .click();
  await page.getByRole("option", { name: "7:00 PM" }).click();
  await page
    .getByLabel("Reschedule Event")
    .getByRole("button", { name: "Reschedule" })
    .click();
  await expect(page.locator("tbody")).toContainText(
    "2025-12-31, 07:00:00 p.m."
  );
  await expect(page.locator("tbody")).toContainText("Bob Lee");
  await page
    .getByRole("row", { name: "4 Bob Lee jhondoesnthack@" })
    .getByRole("button")
    .nth(1)
    .click();
  await page
    .locator("div")
    .filter({ hasText: /^New Date$/ })
    .click();
  await page.getByRole("button", { name: currentMonth, exact: true }).click();
  await page
    .getByRole("button", { name: currentDate.getFullYear().toString() })
    .click();
  await page.getByRole("button", { name: "2028" }).click();
  await page.getByRole("button", { name: "Dec" }).click();
  await page.getByLabel("31 December").click();
  await page
    .locator("div")
    .filter({ hasText: /^New Time$/ })
    .getByRole("textbox")
    .click();
  await page.getByRole("option", { name: "7:00 PM" }).click();
  await page
    .getByLabel("Reschedule Event")
    .getByRole("button", { name: "Reschedule" })
    .click();
  await expect(page.locator("tbody")).toContainText(
    "2028-12-31, 07:00:00 p.m."
  );
  await expect(page.locator("tbody")).toContainText("Bob Lee");
});
