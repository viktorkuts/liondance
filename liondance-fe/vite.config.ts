import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react-swc";
import { resolve } from "path";

// https://vite.dev/config/

const env = loadEnv("", "../", "");

export default defineConfig({
  envDir: "../",
  plugins: [react()],
  define: {
    "import.meta.env.OKTA_ISSUER": JSON.stringify(env.OKTA_ISSUER),
    "import.meta.env.OKTA_CLIENT_ID": JSON.stringify(env.OKTA_CLIENT_ID),
    "import.meta.env.OKTA_REDIRECT_URI": JSON.stringify(env.OKTA_REDIRECT_URI),
    "import.meta.env.OKTA_AUDIENCE": JSON.stringify(env.OKTA_AUDIENCE),
    "import.meta.env.BACKEND_URL":
      env.COOLIFY_BRANCH && /\d+/.test(JSON.stringify(env.COOLIFY_BRANCH))
        ? env.BACKEND_URL.replace(
            "://",
            `://${JSON.stringify(env.COOLIFY_BRANCH).match(/\d+/)[0]}.`
          )
        : JSON.stringify(env.BACKEND_URL),
  },
  server: {
    port: Number.parseInt(JSON.stringify(env.FRONTEND_PORT)),
  },
  resolve: {
    alias: [
      {
        find: "@",
        replacement: resolve(__dirname, "./src"),
      },
    ],
  },
});
