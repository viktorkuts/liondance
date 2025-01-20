import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1800 },
});

test("view courses of a student", async ({ page }) => {
  await page.goto("/");
  await page.getByRole("link", { name: "Courses" }).click();
  await expect(page.getByText("Course Calendar«‹December")).toBeVisible();
  await page.getByText("My CoursesCourse").click();
});
