import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("can edit specific event status to CONFIRMED", async ({ page }) => {
  await page.goto("/events");
  await expect(page.locator("tbody")).toContainText("PENDING");
  await page
    .getByRole("row", { name: "Rio Da Yung OG rioodayungog@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("CONFIRMED");
  await page
    .getByRole("row", { name: "Rio Da Yung OG rioodayungog@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.getByLabel("Update Event Status")).toContainText(
    "Failed to update event status, please provide a valid choice."
  );
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
});

test("can edit specific event status to PENDING", async ({ page }) => {
  await page.goto("/events");
  await expect(page.locator("tbody")).toContainText("CANCELLED");
  await page
    .getByRole("row", { name: "Chief Keef lovesosa@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
  await page
    .getByRole("row", { name: "Chief Keef lovesosa@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("CONFIRMED");
  await page
    .getByRole("row", { name: "Chief Keef lovesosa@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CONFIRMED" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.getByLabel("Update Event Status")).toContainText(
    "Failed to update event status, please provide a valid choice."
  );
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
});

test("can edit specific event status to CANCELLED", async ({ page }) => {
  await page.goto("/events");
  await expect(page.locator("tbody")).toContainText("PENDING");
  await page
    .getByRole("row", { name: "Joshua Block joshblock@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CANCELLED" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("CANCELLED");
  await page
    .getByRole("row", { name: "Joshua Block joshblock@" })
    .getByRole("button")
    .click();
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "CANCELLED" }).locator("span").click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.getByLabel("Update Event Status")).toContainText(
    "Failed to update event status, please provide a valid choice."
  );
  await page.getByRole("textbox", { name: "Event Status" }).click();
  await page.getByRole("option", { name: "PENDING" }).click();
  await page.getByRole("button", { name: "Update Status" }).click();
  await expect(page.locator("tbody")).toContainText("PENDING");
});
