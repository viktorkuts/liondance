import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("send class feedback", async ({ page }) => {
  await page.goto("/classfeedback/2024-05-01");
  await page.getByLabel("Score (1-5) *").click();
  await page.getByLabel("Score (1-5) *").fill("4");
  await page.getByPlaceholder("Share your thoughts about the").click();
  await page
    .getByPlaceholder("Share your thoughts about the")
    .fill("pretty good");
  await page.getByRole("button", { name: "Submit Feedback" }).click();
  await expect(page.getByText("Feedback submitted")).toBeVisible();
  await page.goto("/");
});
