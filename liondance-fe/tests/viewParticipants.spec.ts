import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});
test.use({ storageState: "playwright/.auth/staff.json" });
test("admin can view the list of participants assigned to an event", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByText('Admin')).toBeVisible();
  await page.getByText('Admin').click();
  await expect(page.getByRole('link', { name: 'Events', exact: true })).toBeVisible();
  await page.getByRole('link', { name: 'Events', exact: true }).click();
  await expect(page.getByText('EventsAll Events (17)▶Pending')).toBeVisible();
  await expect(page.getByText('All Events (17)▶')).toBeVisible();
  await page.getByText('All Events (17)▶').click();
  await expect(page.getByRole('cell', { name: 'alice.johnson@webmail.com' }).first()).toBeVisible();
  await page.getByRole('cell', { name: 'alice.johnson@webmail.com' }).first().dblclick();
  await page.getByRole('cell', { name: '-567-8901' }).first().dblclick();
  await expect(page.getByRole('heading', { name: 'Event Details' })).toBeVisible();
  await expect(page.getByText('Performers (2)')).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Sarah Smith' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'ACCEPTED' }).first()).toBeVisible();
});
