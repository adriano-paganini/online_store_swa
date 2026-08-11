# Frontend

React and TypeScript frontend for the event-driven e-commerce platform.

## Purpose

The frontend provides:

- public product browsing and product detail pages,
- authenticated customer flows such as profile, addresses, notifications, subscriptions, checkout, payment, and orders,
- manager-only product-management pages,
- admin-only user-management pages.

It is a client for the Spring Boot backend, not a standalone frontend mock.

## Local Development

```bash
npm install
npm start
```

Default local URL: `http://localhost:3000`

## Tooling

- React 18
- TypeScript
- Vite
- Tailwind CSS
- React Router
- Axios
- ESLint
- Prettier
- Husky / lint-staged
- Vitest setup

## Notes

- The frontend uses role-aware route guards for authenticated, manager, and admin sections.
- Axios configuration lives in `src/config/config.ts`.
- The checked-in frontend currently derives the backend base URL from `window.location.hostname`, so an environment file is not required for the default local setup.
- API wrappers live in `src/utilities/`.
