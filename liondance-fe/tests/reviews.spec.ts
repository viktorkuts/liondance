import { test, expect } from "@playwright/test";

test.use({ storageState: "playwright/.auth/staff.json" });
test("change and see public reviews", async ({ page }) => {
  await page.goto("/");
  await page.getByText("Admin").click();
  await page.getByRole("link", { name: "Events", exact: true }).click();
  await page.getByText("▶").first().click();
  await page
    .getByRole("row", { name: "1 Alice Johnson alice.johnson" })
    .getByRole("button")
    .nth(5)
    .click();
  await expect(page.locator("#root")).toContainText("Feedback for Event");

  const comment =
    (await page.getByText("Comment: ").first().textContent()) || "false";

  expect(comment).toBeTruthy();

  await expect(
    page.getByText("Comment: ").first().locator("..").getByRole("button")
  ).toContainText("Make Public");

  page.getByText("Comment: ").first().locator("..").getByRole("button").click();

  await expect(
    page.getByText("Comment: ").first().locator("..").getByRole("button")
  ).toContainText("Make Private");

  await page.goto("/reviews");
  await expect(page.getByText("Comment: ").first()).toContainText(comment);
});
