import { test as setup, expect, Page } from "@playwright/test";

const staffFile = "playwright/.auth/staff.json";
const studentFile = "playwright/.auth/student.json";
const clientFile = "playwright/.auth/client.json";
const userFile = "playwright/.auth/user.json";

const pass = process.env.PLAYWRIGHT_PASS;

const login = async (page: Page, user?: string) => {
  expect(user).toBeTruthy();
  expect(pass).toBeTruthy();

  // This is to appease the TypeScript gods of codium
  if (!user || !pass) throw "Missing variables";

  await page.goto("/");
  await expect(page.getByText("Welcome!")).toBeVisible();
  const logout = page.getByRole("link", { name: "Logout", exact: true });
  if (await logout.isVisible()) {
    await logout.click();
  }
  await page.waitForTimeout(1000);
  const login = page.getByText("Login");
  expect(await login.isVisible());
  await login.click();
  await page.waitForTimeout(1500);
  expect(await page.getByText("Continue with Google").isVisible());
  await page.getByLabel("Username").fill(user);
  await page.getByLabel("Password").fill(pass);
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await page.waitForTimeout(250);
  expect(await page.getByText("Incorrect username or password")).toBeHidden();
  if (await page.getByText("Authorize App").isVisible()) {
    await page.getByRole("button", { name: "Accept" }).click();
  }
  await page.waitForTimeout(1000);
};

setup("authenticate as staff", async ({ page }) => {
  await login(page, process.env.PLAYWRIGHT_STAFF);

  expect(await page.getByText("Welcome!")).toBeVisible();
  expect(await page.getByText("Admin")).toBeVisible();

  await page.context().storageState({ path: staffFile });
});

setup("authenticate as student", async ({ page }) => {
  await login(page, process.env.PLAYWRIGHT_STUDENT);

  expect(await page.getByText("Welcome!")).toBeVisible();

  await page.context().storageState({ path: studentFile });
});

setup("authenticate as client", async ({ page }) => {
  await login(page, process.env.PLAYWRIGHT_CLIENT);

  expect(await page.getByText("Welcome!")).toBeVisible();

  await page.context().storageState({ path: clientFile });
});

setup("authenticate as user", async ({ page }) => {
  await login(page, process.env.PLAYWRIGHT_ANYUSER);

  expect(await page.getByText("Welcome!")).toBeVisible();

  await page.context().storageState({ path: userFile });
});
