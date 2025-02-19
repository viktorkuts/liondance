import { test, expect } from "@playwright/test";

test.use({ storageState: "playwright/.auth/staff.json" });
test("approve event participation", async ({ page }) => {
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
  await page.getByRole("option", { name: "Tony Nearos" }).click();
  await page.getByText("Current Performers").click();
  await page.getByRole("heading", { name: "Current Performers" }).click();
  await page
    .getByLabel("Add Performers")
    .getByRole("button", { name: "Add Performers" })
    .click();
  await expect(page.getByRole("cell", { name: "Tony Nearos" })).toBeVisible();
  const eventId = await page
    .getByText("Event Id")
    .locator("..")
    .getByRole("cell")
    .last()
    .textContent();

  expect(eventId).toBeTruthy();

  await page.goto("/events/" + eventId + "/performers");

  await expect(
    page.getByRole("heading", { name: "Performance Participation" })
  ).toBeVisible();

  await page.getByPlaceholder("Pick an option").click();

  await page.getByRole("option", { name: "Accepted" }).click();
  await page.getByRole("button", { name: "Save" }).click();
  await expect(page.getByText("Status saved with success!")).toBeVisible();
});
