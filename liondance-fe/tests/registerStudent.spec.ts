import { test, expect } from "@playwright/test";

test("student can register form", async ({ browser }) => {
  const randomFirstName = Date.now().toString().slice(-4) + "fN";
  const randomLastName = Date.now().toString().slice(-4) + "lN";
  const randomEmail = Date.now().toString().slice(-4) + "bro@null.local";

  const studentContext = await browser.newContext({
    storageState: "playwright/.auth/user.json",
  });
  const studentPage = await studentContext.newPage();

  await studentPage.goto("/");
  await expect(studentPage.getByText("Welcome!")).toBeVisible();
  await studentPage
    .getByRole("link", { name: "Registration", exact: true })
    .click();
  await expect(
    studentPage.getByText(
      "Student Registration Form1Student Information2Address Information3Parent"
    )
  ).toBeVisible();
  await studentPage.getByPlaceholder("John", { exact: true }).click();
  await studentPage
    .getByPlaceholder("John", { exact: true })
    .fill(randomFirstName);
  await studentPage.getByPlaceholder("Doe", { exact: true }).click();
  await studentPage
    .getByPlaceholder("Doe", { exact: true })
    .fill(randomLastName);
  await studentPage.getByPlaceholder("January 1,").click();
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  const options: Intl.DateTimeFormatOptions = {
    day: "numeric",
    month: "long",
    year: "numeric",
  };
  const formattedDate = yesterday.toLocaleDateString("en-GB", options);
  await studentPage.getByLabel(formattedDate, { exact: true }).click();
  await studentPage.getByRole("button", { name: "Next" }).click();
  await studentPage.getByPlaceholder("john.doe@example.com").click();
  await studentPage.getByPlaceholder("john.doe@example.com").fill(randomEmail);
  await studentPage.getByRole("button", { name: "Next" }).click();
  await studentPage.getByPlaceholder("Main Street").click();
  await studentPage.getByPlaceholder("Main Street").fill("1");
  await studentPage.getByPlaceholder("Quebec").click();
  await studentPage.getByRole("option", { name: "Quebec" }).click();
  await studentPage.getByPlaceholder("Montreal").click();
  await studentPage.getByPlaceholder("Montreal").fill("Mont");
  await studentPage
    .getByRole("option", { name: "Montréal", exact: true })
    .click();
  await studentPage.getByPlaceholder("H1H 1H1").click();
  await studentPage.getByPlaceholder("H1H 1H1").fill("H1H1H1");
  await studentPage.getByRole("button", { name: "Next" }).click();
  await studentPage.getByRole("button", { name: "Next" }).click();
  await studentPage.getByRole("button", { name: "Submit" }).click();
  await expect(
    studentPage.getByText("Submission complete!Please")
  ).toBeVisible();
  await studentPage.waitForTimeout(1500);
  await studentPage.getByRole("link", { name: "Back to Home" }).click();

  const adminContext = await browser.newContext({
    storageState: "playwright/.auth/staff.json",
  });
  const adminPage = await adminContext.newPage();

  await adminPage.goto("/");
  await expect(adminPage.getByText("Welcome!")).toBeVisible();
  await expect(adminPage.getByText("Admin")).toBeVisible();
  await adminPage.getByText("Admin").click();
  await adminPage.getByRole("link", { name: "Pending Registrations" }).click();
  await expect(adminPage.url()).toMatch("/pending-registrations");
  await adminPage.evaluate(() =>
    window.scrollTo(0, document.body.scrollHeight)
  );
  await expect(
    adminPage.getByRole("cell", {
      name: `${randomFirstName} ${randomLastName}`,
    })
  ).toHaveCount(1);

  await studentPage.close();
  await adminPage.close();
});
