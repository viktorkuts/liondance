import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("can edit the details of a specific event", async ({ page }) => {
  await page.goto("/events");
  await page.getByText("All Events").click();
  await expect(page.locator("tbody")).toContainText("Alice Johnson");
  await expect(page.locator("tbody")).toContainText("No special requests");
  await expect(page.locator("tbody")).toContainText(
    "alice.johnson@webmail.com"
  );
  await page
    .getByRole("row", { name: "2 Alice Johnson alice.johnson" })
    .getByRole("button")
    .nth(3)
    .click();
  await page
    .locator("div")
    .filter({ hasText: /^Event Type$/ })
    .getByRole("textbox")
    .click();
  await page.getByRole("option", { name: "FESTIVAL" }).click();
  await page
    .locator("div")
    .filter({ hasText: /^Special Request$/ })
    .getByRole("textbox")
    .click();
  await page
    .locator("div")
    .filter({ hasText: /^Special Request$/ })
    .getByRole("textbox")
    .fill("nothing");
  await page.getByRole("button", { name: "Update", exact: true }).click();
  await expect(page.locator("tbody")).toContainText("nothing");
  await expect(page.locator("tbody")).toContainText("FESTIVAL");
  await expect(page.locator("tbody")).toContainText("Alice Johnson");
  await page
    .getByRole("row", { name: "2 Alice Johnson alice.johnson" })
    .getByRole("button")
    .nth(3)
    .click();
  await page
    .locator("div")
    .filter({ hasText: /^Event Type$/ })
    .getByRole("textbox")
    .click();
  await page.getByRole("option", { name: "WEDDING" }).click();
  await page
    .locator("div")
    .filter({ hasText: /^Special Request$/ })
    .getByRole("textbox")
    .click();
  await page
    .locator("div")
    .filter({ hasText: /^Special Request$/ })
    .getByRole("textbox")
    .fill("");
  await page.getByRole("button", { name: "Update", exact: true }).click();
  await expect(page.locator("tbody")).toContainText("No special requests");
  await expect(page.locator("tbody")).toContainText("Alice Johnson");
  await expect(page.locator("tbody")).toContainText("WEDDING");
});
