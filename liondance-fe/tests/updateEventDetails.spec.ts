import { test, expect } from "@playwright/test";

test.use({
  viewport: { width: 1920, height: 1080 },
});

test("can edit the details of a specific event", async ({ page }) => {
    await page.goto("/events");
    await expect(page.locator('tbody')).toContainText('Joshua Block');
    await expect(page.locator('tbody')).toContainText('No special requests');
    await expect(page.locator('tbody')).toContainText('joshblock@billysio.org');
    await page.getByRole('row', { name: 'Joshua Block joshblock@' }).getByRole('button').nth(3).click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').fill('Block');
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').fill('Joshua');
    await page.locator('div').filter({ hasText: /^Event Type$/ }).getByRole('textbox').click();
    await page.getByRole('option', { name: 'FESTIVAL' }).click();
    await page.locator('div').filter({ hasText: /^Special Request$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^Special Request$/ }).getByRole('textbox').fill('nothing');
    await page.getByRole('button', { name: 'Update', exact: true }).click();
    await expect(page.locator('tbody')).toContainText('nothing');
    await expect(page.locator('tbody')).toContainText('FESTIVAL');
    await expect(page.locator('tbody')).toContainText('Block Joshua');
    await page.getByRole('row', { name: 'Block Joshua joshblock@' }).getByRole('button').nth(3).click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').fill('Joshua');
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').fill('Block');
    await page.locator('div').filter({ hasText: /^Event Type$/ }).getByRole('textbox').click();
    await page.getByRole('option', { name: 'WEDDING' }).click();
    await page.locator('div').filter({ hasText: /^Special Request$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^Special Request$/ }).getByRole('textbox').fill('');
    await page.getByRole('button', { name: 'Update', exact: true }).click();
    await expect(page.locator('tbody')).toContainText('No special requests');
    await expect(page.locator('tbody')).toContainText('Joshua Block');
    await expect(page.locator('tbody')).toContainText('WEDDING');
  });


test("cannot edit the details of a specific event due to blanc entry", async ({ page }) => {
    await page.goto("/events");
    await expect(page.locator('tbody')).toContainText('Joshua Block');
    await expect(page.locator('tbody')).toContainText('No special requests');
    await expect(page.locator('tbody')).toContainText('joshblock@billysio.org');
    await page.getByRole('row', { name: 'Joshua Block joshblock@' }).getByRole('button').nth(3).click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').fill('');
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').fill('');
    await page.locator('div').filter({ hasText: /^Event Type$/ }).getByRole('textbox').click();
    await page.getByRole('button', { name: 'Update', exact: true }).click();
    await expect(page.locator('div.error')).toContainText('Please fill in all fields.');
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^First Name$/ }).getByRole('textbox').fill('Joshua');
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^Last Name$/ }).getByRole('textbox').fill('Block');
    await page.getByRole('button', { name: 'Update', exact: true }).click();
    await expect(page.locator('tbody')).toContainText('Joshua Block');
  });