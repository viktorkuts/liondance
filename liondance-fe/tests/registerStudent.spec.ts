import { test, expect } from "@playwright/test";

test("student can register form", async ({ page }) => {
  const randomFirstName = Date.now().toString().slice(-4) + "fN";
  const randomLastName = Date.now().toString().slice(-4) + "lN";
  const randomEmail = Date.now().toString().slice(-4) + "bro@null.local";
  await page.goto("/");
  await expect(page.getByText("Welcome!")).toBeVisible();
  if (
    await page.getByRole("link", { name: "Logout", exact: true }).isVisible()
  ) {
    await page.getByRole("link", { name: "Logout", exact: true }).click();
  }
  await page.getByRole("link", { name: "Registration", exact: true }).click();
  await expect(
    page.getByText(
      "Student Registration Form1Student Information2Address Information3Parent"
    )
  ).toBeVisible();
  await page.getByPlaceholder("John", { exact: true }).click();
  await page.getByPlaceholder("John", { exact: true }).fill(randomFirstName);
  await page.getByPlaceholder("Doe", { exact: true }).click();
  await page.getByPlaceholder("Doe", { exact: true }).fill(randomLastName);
  await page.getByPlaceholder("January 1,").click();
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  const options: Intl.DateTimeFormatOptions = {
    day: "numeric",
    month: "long",
    year: "numeric",
  };
  const formattedDate = yesterday.toLocaleDateString("en-GB", options);
  await page.getByLabel(formattedDate, { exact: true }).click();
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByPlaceholder("john.doe@example.com").click();
  await page.getByPlaceholder("john.doe@example.com").fill(randomEmail);
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByPlaceholder("Main Street").click();
  await page.getByPlaceholder("Main Street").fill("1");
  await page.getByPlaceholder("Quebec").click();
  await page.getByRole("option", { name: "Quebec" }).click();
  await page.getByPlaceholder("Montreal").click();
  await page.getByPlaceholder("Montreal").fill("Mont");
  await page.getByRole("option", { name: "Montréal", exact: true }).click();
  await page.getByPlaceholder("H1H 1H1").click();
  await page.getByPlaceholder("H1H 1H1").fill("H1H1H1");
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(page.getByText("Submission complete!Please")).toBeVisible();
  await page.getByRole("link", { name: "Back to Home" }).click();
  await page.waitForTimeout(1000);

  await page.getByRole("link", { name: "Pending Registrations" }).click();
  await expect(page.url()).toMatch("/pending-registrations");
  await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
  await expect(
    page.getByRole("cell", { name: `${randomFirstName} ${randomLastName}` })
  ).toBeVisible();
  await page
    .getByRole("cell", { name: `${randomFirstName} ${randomLastName}` })
    .click();
  await expect(page.locator("#root")).toContainText(randomFirstName);
  await expect(page.locator("#root")).toContainText(randomLastName);
});
