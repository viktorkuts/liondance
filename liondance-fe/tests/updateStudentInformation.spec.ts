import { test, expect } from '@playwright/test';

test.use({
 viewport: { width: 1920, height: 1080 },
});

test('can edit specific student details', async ({ page }) => {
 await page.goto('http://localhost:5173/');
 await page.getByText('Admin').click();
 await page.getByRole('link', { name: 'Students' }).click();
 await page.getByRole('row', { name: 'Hawk Doesounow Tuah hawktuah@' }).getByRole('button').click();
 await expect(page.locator('#root')).toContainText('Name: Hawk Doesounow Tuah');
 await expect(page.locator('#root')).toContainText('Gender: FEMALE');
 await expect(page.locator('#root')).toContainText('Email: hawktuah@onthatthang.org');
 await expect(page.locator('#root')).toContainText('Phone: 514-637-8400');
 await expect(page.locator('#root')).toContainText('Address: 1234 Main St, Montreal, QC H1H 1H1');
 await page.getByRole('button', { name: 'Edit Student' }).click();
 await page.getByPlaceholder('John', { exact: true }).click();
 await page.getByPlaceholder('John', { exact: true }).click();
 await page.getByPlaceholder('John', { exact: true }).fill('Tuah');
 await page.getByPlaceholder('Pork').click();
 await page.getByPlaceholder('Pork').fill('Hawk');
 await page.getByRole('textbox', { name: 'Gender' }).click();
 await page.getByRole('option', { name: 'MALE', exact: true }).click();
 await page.getByPlaceholder('john.doe@example.com').click();
 await page.getByPlaceholder('john.doe@example.com').fill('hawktuah@onthatthang.com');
 await page.getByPlaceholder('-123-1234').click();
 await page.getByPlaceholder('-123-1234').fill('514-637-8401');
 await page.getByPlaceholder('Quebec').click();
 await page.getByRole('option', { name: 'Quebec' }).click();
 await page.getByRole('button', { name: 'Update Student' }).click();
 await expect(page.locator('#root')).toContainText('Name: Tuah Doesounow Hawk');
 await expect(page.locator('#root')).toContainText('Gender: MALE');
 await expect(page.locator('#root')).toContainText('Email: hawktuah@onthatthang.com');
 await expect(page.locator('#root')).toContainText('Phone: 514-637-8401');
 await page.getByRole('button', { name: 'Back to Student List' }).click();
 await expect(page.locator('tbody')).toContainText('Tuah Doesounow Hawk');
 await expect(page.locator('tbody')).toContainText('hawktuah@onthatthang.com');
 await expect(page.locator('tbody')).toContainText('MALE');
 await page.getByRole('row', { name: 'Tuah Doesounow Hawk hawktuah@' }).getByRole('button').click();
 await page.getByRole('button', { name: 'Edit Student' }).click();
 await page.getByPlaceholder('John', { exact: true }).click();
 await page.getByPlaceholder('John', { exact: true }).fill('Hawk');
 await page.getByPlaceholder('Pork').click();
 await page.getByPlaceholder('Pork').fill('Tuah');
 await page.getByRole('textbox', { name: 'Gender' }).click();
 await page.getByRole('option', { name: 'FEMALE' }).click();
 await page.getByPlaceholder('john.doe@example.com').click();
 await page.getByPlaceholder('john.doe@example.com').fill('hawktuah@onthatthang.org');
 await page.getByPlaceholder('-123-1234').click();
 await page.getByPlaceholder('-123-1234').fill('514-637-8400');
 await page.getByRole('button', { name: 'Update Student' }).click();
 await expect(page.locator('#root')).toContainText('Name: Hawk Doesounow Tuah');
 await expect(page.locator('#root')).toContainText('Gender: FEMALE');
 await expect(page.locator('#root')).toContainText('Email: hawktuah@onthatthang.org');
 await expect(page.locator('#root')).toContainText('Phone: 514-637-8400');
 await page.getByRole('button', { name: 'Back to Student List' }).click();
 await expect(page.locator('tbody')).toContainText('Hawk Doesounow Tuah');
 await expect(page.locator('tbody')).toContainText('hawktuah@onthatthang.org');
 await expect(page.locator('tbody')).toContainText('FEMALE');
 
});