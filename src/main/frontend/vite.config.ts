/// <reference types="vitest" />

import path from "path";
import { defineConfig } from "vite";

import react from "@vitejs/plugin-react";
import inspect from "vite-plugin-inspect";
import svgr from "vite-plugin-svgr";

// READ-MORE: https://vitejs.dev/config/
export default defineConfig(() => {
  // const env = loadEnv(mode, path.resolve(__dirname, 'env'));

  // const isDevMode = mode === 'development';
  // const isProdMode = mode === 'production';
  const plugins = [svgr(), react(), inspect()];

  return {
    server: {
      port: 3000,
      host: true,
      strictPort: true,
      open: false,
      esbuild: {
        target: "esnext",
        platform: "linux",
      },
    },
    preview: {
      port: 5001,
      strictPort: true,
      open: true,
    },
    base: "/",
    envDir: "./env",
    plugins,
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "./src"),
      },
    },
    build: {
      outDir: "dist",
      sourcemap: true,
      // READ-MORE:  https://vitejs.dev/config/build-options#build-target
      target: "esnext",
    },
    test: {
      globals: true, // Enables global test functions like `test`, `it`, etc.
      environment: "jsdom", // for DOM-related tests
      setupFiles: ["./src/setupTests.ts"],
    },
  };
});
