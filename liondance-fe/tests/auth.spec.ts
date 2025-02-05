import { test, expect } from "@playwright/test";

test.only("auth login", async ({ page }) => {
  const user = process.env.PLAYWRIGHT_STAFF_USERNAME;
  const pass = process.env.PLAYWRIGHT_STAFF_PASSWORD;
  expect(user).toBeTruthy();
  expect(pass).toBeTruthy();

  // This is to appease the TypeScript gods of codium
  if (!user || !pass) throw "..STAFF_USERNAME or ..STAFF_PASSWORD not set";

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
  expect(await page.getByText("Welcome!")).toBeVisible();
});
