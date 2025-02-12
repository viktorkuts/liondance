import { test as teardown } from "@playwright/test";

teardown("teardown db", async () => {
  await fetch(process.env.BACKEND_URL + "/reset-db");
});
