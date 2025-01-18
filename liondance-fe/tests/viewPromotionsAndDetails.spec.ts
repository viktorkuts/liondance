import { test, expect } from "@playwright/test";

test("view promotions and details", async ({page,})=>{
  await page.goto(process.env.PR_NUMBER ? `${process.env.FRONTEND_URL.replace("://", `://${process.env.PR_NUMBER}`)}` : process.env.FRONTEND_URL);
  await page.getByText('Admin').click();
  await page.getByRole('link', { name: 'Promotions' }).click();
  await expect(page.getByText('PromotionsSummer SaleDiscount')).toBeVisible();
  await page.getByRole('heading', { name: 'Summer Sale' }).click();
  await expect(page.getByText('Summer SaleDiscount Rate: 20%')).toBeVisible();
  await page.getByRole('link', { name: 'Back' }).click();
  await expect(page.getByText('Summer SaleDiscount: 20%')).toBeVisible();  
})