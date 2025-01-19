import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1800, height: 1080 },
});

test("can book event", async ({ page }) => {
  await page.goto("/");
  await expect(
    page.getByText(
      "Welcome!ReviewsContactCalendarBook EventRegistrationPending"
    )
  ).toBeVisible();
  await expect(page.getByText("ReviewsContactCalendarBook")).toBeVisible();
  await expect(page.getByRole("link", { name: "Book Event" })).toBeVisible();
  await page.getByRole("link", { name: "Book Event" }).click();
  await expect(page.getByText("Event Registration Form1Event")).toBeVisible();
  await expect(
    page.getByRole("heading", { name: "Event Registration Form" })
  ).toBeVisible();
  await expect(page.getByText("First Name *Middle NameLast")).toBeVisible();
  await expect(page.getByPlaceholder("John", { exact: true })).toBeVisible();
  await page.getByPlaceholder("John", { exact: true }).click();
  await page.getByPlaceholder("John", { exact: true }).fill("Jameson");
  await expect(page.getByPlaceholder("Z.")).toBeVisible();
  await page.getByPlaceholder("Z.").click();
  await page.getByPlaceholder("Z.").fill("Statham");
  await expect(page.getByPlaceholder("Doe", { exact: true })).toBeVisible();
  await page.getByPlaceholder("Doe", { exact: true }).click();
  await page.getByPlaceholder("Doe", { exact: true }).fill("Bourne");
  await expect(page.getByPlaceholder("john.doe@example.com")).toBeVisible();
  await page.getByPlaceholder("john.doe@example.com").click();
  await page
    .getByPlaceholder("john.doe@example.com")
    .fill("liondance@yopmail.com");
  await expect(page.getByPlaceholder("+1 (123) 456-")).toBeVisible();
  await page.getByPlaceholder("+1 (123) 456-").click();
  await page.getByPlaceholder("+1 (123) 456-").fill("5149999999");
  await expect(page.getByPlaceholder("Pick a date")).toBeVisible();
  await page.getByPlaceholder("Pick a date").click();
  await expect(page.locator("div:nth-child(4) > .m_38a85659")).toBeVisible();
  await expect(
    page.locator("div").filter({ hasText: /^December 2024$/ })
  ).toBeVisible();
  await page
    .locator("div")
    .filter({ hasText: /^December 2024$/ })
    .getByRole("button")
    .nth(2)
    .click();
  await expect(page.getByLabel("10 January")).toBeVisible();
  await page.getByLabel("10 January").click();
  await expect(page.getByPlaceholder("Select a time")).toBeVisible();
  await page.getByPlaceholder("Select a time").click();
  await expect(page.getByRole("option", { name: "12:00 PM" })).toBeVisible();
  await page.getByRole("option", { name: "12:00 PM" }).click();
  await expect(page.getByPlaceholder("Wedding")).toBeVisible();
  await page.getByPlaceholder("Wedding").click();
  await expect(page.getByRole("option", { name: "WEDDING" })).toBeVisible();
  await page.getByRole("option", { name: "WEDDING" }).click();
  await expect(page.getByPlaceholder("Cash")).toBeVisible();
  await page.getByPlaceholder("Cash").click();
  await expect(page.getByRole("option", { name: "PAYPAL" })).toBeVisible();
  await page.getByRole("option", { name: "PAYPAL" }).click();
  await expect(page.getByPlaceholder("Request...")).toBeVisible();
  await page.getByPlaceholder("Request...").click();
  await page.getByPlaceholder("Request...").fill("Diddy Party");
  await expect(page.getByRole("button", { name: "Next" })).toBeVisible();
  await page.getByRole("button", { name: "Next" }).click();
  await expect(page.getByText("Address Line *Province *City")).toBeVisible();
  await expect(page.getByPlaceholder("Main Street")).toBeVisible();
  await page.getByPlaceholder("Main Street").click();
  await page.getByPlaceholder("Main Street").fill("123 main st");
  await expect(page.getByPlaceholder("Quebec")).toBeVisible();
  await page.getByPlaceholder("Quebec").click();
  await expect(page.getByRole("option", { name: "Quebec" })).toBeVisible();
  await page.getByRole("option", { name: "Quebec" }).click();

  await expect(page.getByPlaceholder("Montreal")).toBeVisible();
  await page.getByPlaceholder("Montreal").click();
  await page.getByPlaceholder("Montreal").fill("Montr");
  await expect(
    page.getByRole("option", { name: "Montréal", exact: true })
  ).toBeVisible();
  await page.getByRole("option", { name: "Montréal", exact: true }).click();
  await expect(page.getByPlaceholder("H1H 1H1")).toBeVisible();
  await page.getByPlaceholder("H1H 1H1").click();
  await page.getByPlaceholder("H1H 1H1").fill("j3x 1g1");
  await expect(page.getByRole("button", { name: "Next" })).toBeVisible();
  await page.getByRole("button", { name: "Next" }).click();
  await expect(
    page.getByRole("button", { name: "I need to correct something!" })
  ).toBeVisible();
  await page
    .getByRole("button", { name: "I need to correct something!" })
    .click();
  await expect(page.getByPlaceholder("H1H 1H1")).toBeVisible();
  await expect(page.getByRole("button", { name: "Next" })).toBeVisible();
  await page.getByRole("button", { name: "Next" }).click();
  await expect(
    page.getByRole("heading", { name: "Does this information look" })
  ).toBeVisible();
  await expect(page.getByText("Name: Jameson Bourne")).toBeVisible();
  await expect(page.getByText("Email: liondance@yopmail.com")).toBeVisible();
  await expect(page.getByText("Phone: +1 (514) 999-9999")).toBeVisible();
  await expect(
    page.getByText("Location: 123 main st, Montréal, Quebec, j3x 1g1")
  ).toBeVisible();
  await expect(page.getByText("Event Date: January 10, 2025")).toBeVisible();
  await expect(page.getByText("Event Time: 12:00 AM")).toBeVisible();
  await expect(
    page.getByRole("button", { name: "I need to correct something!" })
  ).toBeVisible();
  await expect(page.getByRole("button", { name: "Looks Good!" })).toBeVisible();
  await page
    .getByRole("button", { name: "I need to correct something!" })
    .click();
  await expect(page.getByText("Address Line *Province *City")).toBeVisible();
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByRole("button", { name: "Looks Good!" }).click();
  await expect(
    page.getByRole("heading", { name: "Request Submitted!" })
  ).toBeVisible();
  await expect(page.getByText("Request Submitted!Thank you")).toBeVisible();
  await expect(page.getByRole("link", { name: "Back to Home" })).toBeVisible();
  await page.getByRole("link", { name: "Back to Home" }).click();
});
