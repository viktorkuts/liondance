import { test, expect, Page } from "@playwright/test";
import { randomBytes } from "crypto";

const currentDate = new Date();
const prevDate = new Date(currentDate);
prevDate.setMonth(currentDate.getMonth() - 1);
const nextDate = new Date(currentDate);
nextDate.setMonth(currentDate.getMonth() + 1);

const { prevMonth, currentMonth, nextMonth } = {
  prevMonth: prevDate.toLocaleString("default", {
    year: "numeric",
    month: "long",
  }),
  currentMonth: currentDate.toLocaleString("default", {
    year: "numeric",
    month: "long",
  }),
  nextMonth: nextDate.toLocaleString("default", {
    year: "numeric",
    month: "long",
  }),
};

const eventBooking = async (page: Page) => {
  const randomSeed = randomBytes(8).toString();
  const msg = "hello world" + randomSeed;
  const address = {
    street: randomSeed + " grove street",
    state: "Newfoundland and Labrador",
    city: randomSeed + "shlag",
    zip: "H1H1H1",
  };
  await expect(
    page.getByRole("heading", { name: "Event Registration Form" })
  ).toBeVisible();
  await page.getByPlaceholder("Pick a date").click();
  await page.getByRole("button").filter({ hasText: /^$/ }).nth(1).click();
  await page
    .getByRole("button", { name: /^6[^0-9].*\d{4}$/, exact: true })
    .last()
    .click();
  await page.getByPlaceholder("Select a time").click();
  await page.getByRole("option", { name: "1:30 PM" }).click();
  await page.getByPlaceholder("Wedding").click();
  await page.getByRole("option", { name: "WEDDING" }).click();
  await page.getByPlaceholder("Cash").click();
  await page.getByRole("option", { name: "CASH" }).click();
  await page.getByPlaceholder("Request...").fill(msg);
  await page.getByPlaceholder("PRIVATE").click();
  await page.getByRole("option", { name: "PUBLIC" }).click();
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByPlaceholder("Main Street").fill(address.street);
  await page.getByPlaceholder("Quebec").click();
  await page.getByText(address.state).click();
  await page.getByPlaceholder("Montreal").fill(address.city);
  await page.getByPlaceholder("H1H 1H1").fill(address.zip);
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByRole("button", { name: "Looks Good!" }).click();
  await expect(
    page.getByRole("heading", { name: "Request Submitted!" })
  ).toBeVisible();
  await page.waitForTimeout(1000);
  await page.getByRole("link", { name: "Upcoming Events" }).click();
  await expect(
    page.getByText(
      `Address: ${address.street}, ${address.city}, ${address.state} ${address.zip}`
    )
  ).toHaveCount(1);
};
const clientRegistration = async (page: Page) => {
  await expect(
    page.getByRole("heading", { name: "Client Registration Form" })
  ).toBeVisible();
  await page.getByPlaceholder("John", { exact: true }).click();
  await page.getByPlaceholder("John", { exact: true }).fill("H");
  await page.getByPlaceholder("John", { exact: true }).press("Tab");
  await page.getByPlaceholder("Z.").fill("E");
  await page.getByPlaceholder("Z.").press("Tab");
  await page.getByPlaceholder("Doe", { exact: true }).fill("E");
  await page.getByPlaceholder("Doe", { exact: true }).press("Tab");
  await page.getByPlaceholder("john.doe@example.com").fill("e@example.com");
  await page.getByPlaceholder("john.doe@example.com").press("Tab");
  await page.getByPlaceholder("-123-1234").fill("(514) 709-7180");
  await page.getByPlaceholder("-123-1234").press("Tab");
  await page.getByPlaceholder("January 1, 2000").click();
  await page
    .locator("div")
    .filter({ hasText: currentMonth })
    .getByRole("button")
    .first()
    .click();
  await page.getByLabel(`1 ${prevMonth}`, { exact: true }).click();
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByPlaceholder("Main Street").click();
  await page.getByPlaceholder("Main Street").fill("170 grove street");
  await page.getByPlaceholder("Main Street").press("Tab");
  await page.getByPlaceholder("Quebec").click();
  await page.getByRole("option", { name: "Newfoundland and Labrador" }).click();
  await page.getByPlaceholder("Montreal").click();
  await page.getByPlaceholder("Montreal").fill("g");
  await page.getByRole("option", { name: "Gaultois" }).click();
  await page.getByPlaceholder("H1H 1H1").click();
  await page.getByPlaceholder("H1H 1H1").fill("H1H1H1");
  await page.getByRole("button", { name: "Next" }).click();
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByRole("heading", { name: "Submission complete!" })
  ).toBeVisible();
  await page.waitForTimeout(1000);
};

test.describe("new user booking", () => {
  test.use({ storageState: "playwright/.auth/user.json" });
  test.describe.configure({ mode: "serial" });

  test("book event as new user", async ({ page }) => {
    await page.goto("/");
    expect(await page.getByText("Welcome!")).toBeVisible();
    await page.getByRole("link", { name: "Book Event" }).click();

    await clientRegistration(page);

    await page.getByRole("link", { name: "Back to Event Form" }).click();

    await eventBooking(page);
  });

  test("book event as newly created user", async ({ page }) => {
    await page.goto("/");
    expect(await page.getByText("Welcome!")).toBeVisible();
    await page.getByRole("link", { name: "Book Event" }).click();

    await eventBooking(page);
  });
});

test.describe("student booking", () => {
  test.use({ storageState: "playwright/.auth/student.json" });

  test("book event as student", async ({ page }) => {
    await page.goto("/");
    expect(await page.getByText("Welcome!")).toBeVisible();
    await page.getByRole("link", { name: "Book Event" }).click();

    await eventBooking(page);
  });
});

test.describe("client booking", () => {
  test.use({ storageState: "playwright/.auth/client.json" });

  test("book event as client", async ({ page }) => {
    await page.goto("/");
    expect(await page.getByText("Welcome!")).toBeVisible();
    await page.getByRole("link", { name: "Book Event" }).click();

    await eventBooking(page);
  });
});
