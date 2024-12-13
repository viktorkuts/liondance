import { test, expect } from '@playwright/test';

test.use({
 viewport: { width: 1920, height: 1080 },
});

test('can edit specific student details', async ({ page }) => {
 await page.goto('http://localhost:5173/');
 await page.getByText('Admin').click();
 await page.getByRole('link', { name: 'Students' }).click();
 await expect(page.locator('tbody')).toContainText('Hawk Doesounow Tuah');
 await expect(page.locator('tbody')).toContainText('hawktuah@onthatthang.org');
 await expect(page.locator('tbody')).toContainText('FEMALE');
 await page.getByRole('row', { name: 'Hawk Doesounow Tuah hawktuah@' }).getByRole('button').click();
 await expect(page.locator('h1')).toContainText('Hawk Doesounow Tuah');
 await expect(page.locator('#root')).toContainText('Gender: FEMALE');
 await expect(page.locator('#root')).toContainText('Email: hawktuah@onthatthang.org');
 await page.getByRole('button', { name: 'Edit Student' }).click();
 await page.getByPlaceholder('First Name').click();
 await page.getByPlaceholder('First Name').fill('Tuah');
 await page.getByPlaceholder('Last Name').click();
 await page.getByPlaceholder('Last Name').fill('Hawk');
 await page.getByPlaceholder('Email').click();
 await page.getByPlaceholder('Email').fill('hawktuah@onthatthang.com');
 await page.getByPlaceholder('Gender').click();
 await page.getByPlaceholder('Gender').fill('MALE');
 await page.getByRole('button', { name: 'Confirm' }).click();
 await expect(page.locator('h1')).toContainText('Tuah Doesounow Hawk');
 await expect(page.locator('#root')).toContainText('Gender: MALE');
 await expect(page.locator('#root')).toContainText('Email: hawktuah@onthatthang.com');
 await page.getByRole('button', { name: 'Back to Student List' }).click();
 await expect(page.locator('tbody')).toContainText('Tuah Doesounow Hawk');
 await expect(page.locator('tbody')).toContainText('hawktuah@onthatthang.com');
 await expect(page.locator('tbody')).toContainText('MALE');
 await page.getByRole('row', { name: 'Tuah Doesounow Hawk hawktuah@' }).getByRole('button').click();
 await page.getByRole('button', { name: 'Edit Student' }).click();
 await page.getByPlaceholder('First Name').click();
 await page.getByPlaceholder('First Name').fill('Hawk');
 await page.getByPlaceholder('Last Name').click();
 await page.getByPlaceholder('Last Name').fill('Tuah');
 await page.getByPlaceholder('Email').click();
 await page.getByPlaceholder('Email').fill('hawktuah@onthatthang.org');
 await page.getByPlaceholder('Gender').click();
 await page.getByPlaceholder('Gender').fill('FEMALE');
 await page.getByRole('button', { name: 'Confirm' }).click();
 await expect(page.locator('h1')).toContainText('Hawk Doesounow Tuah');
 await expect(page.locator('#root')).toContainText('Gender: FEMALE');
 await expect(page.locator('#root')).toContainText('Email: hawktuah@onthatthang.org');
 await page.getByRole('button', { name: 'Back to Student List' }).click();
 await expect(page.locator('tbody')).toContainText('Hawk Doesounow Tuah');
 await expect(page.locator('tbody')).toContainText('hawktuah@onthatthang.org');
 await expect(page.locator('tbody')).toContainText('FEMALE');
});