import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("can reschedule an event at thier will", async ({ page }) => {
  await page.goto("/events");
  await expect(page.locator("tbody")).toContainText("31/12/2028, 19:00:00");
  await page
    .getByRole("row", { name: "Joshua Block joshblock@" })
    .getByRole("button")
    .nth(1)
    .click();
  await page.locator('input[data-dates-input="true"]').click();
  await page.getByRole("button", { name: "January 2025", exact: true }).click();
  await page
    .locator("div")
    .filter({ hasText: /^2025$/ })
    .getByRole("button")
    .nth(2)
    .click();
  await page.getByRole("button", { name: "2026" }).click();
  await page.getByRole("button", { name: "2029" }).click();
  await page.getByRole("button", { name: "Jan" }).click();
  await page.getByLabel("1 January 2029", { exact: true }).click();
  await page
    .locator("div")
    .filter({ hasText: /^New Time$/ })
    .getByRole("textbox")
    .click();
  await page.getByRole("option", { name: "7:30 PM" }).click();
  await page
    .getByLabel("Reschedule Event")
    .getByRole("button", { name: "Reschedule" })
    .click();
  await expect(page.locator("tbody")).toContainText("01/01/2029, 19:30:00");
  await page
    .getByRole("row", { name: "Joshua Block joshblock@" })
    .getByRole("button")
    .nth(1)
    .click();
  await page.locator('input[data-dates-input="true"]').click();
  await page.getByRole("button", { name: "January 2025", exact: true }).click();
  await page
    .locator("div")
    .filter({ hasText: /^2025$/ })
    .getByRole("button")
    .nth(2)
    .click();
  await page.getByRole("button", { name: "2026" }).click();
  await page.getByRole("button", { name: "2029" }).click();
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
  await page.locator("tr:nth-child(2) > td:nth-child(5)").click();
  await expect(page.locator("tbody")).toContainText("31/12/2028, 19:00:00");
});
