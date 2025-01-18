import { test, expect } from "@playwright/test";

test("view your events", async ({page,})=>{
   await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
   await expect(page.getByRole('link', { name: 'Your Events' })).toBeVisible();
   await page.getByRole('link', { name: 'Your Events' }).click();
   await expect(page.getByText('Your EventsPhoneLocationEvent')).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Phone' })).toBeVisible();
   
    await expect(page.getByRole('cell', { name: 'Location' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Event Date & Time' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Event Type' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Special Request', exact: true })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Event Status' })).toBeVisible();
});