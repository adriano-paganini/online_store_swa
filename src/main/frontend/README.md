# Frontend (Vite + React + TypeScript)

---

## Tech Stack

- **TypeScript**
- **React 18**
- **Vite**
- **React Router**
- **Tailwind CSS** + `tailwind-merge`
- **ShadCn** using Radix UI primitives
- **Axios** for HTTP
- **ESLint** + **Prettier** + **Husky**

---

## Getting Started

### Prerequisites

- Node.js 18+
- npm

### Install dependencies

```bash
npm install
```

### Run the development server

```bash
npm run dev
```

The app will be available at:

```
http://localhost:3000
```

---

## Available Scripts

### Base

```
npm run dev        # Starts the Vite development server with hot reload.
npm run build      # Runs TypeScript type-checking and builds the production bundle into /dist
npm run preview    # Serves the production build locally for testing
```

---

### Type checking

```
npm run ts:check    # Runs a strict TypeScript type check without emitting files
```

---

### Linting

```
npm run lint          # Runs ESLint across the project.
npm run lint:fix      # Automatically fixes lint issues where possible.
```

---

### Formatting

```
npm run prettier:check      # Checks code formatting using Prettier
npm run prettier:write      # Formats all files using Prettier.
```

---

## Project file structure

```
src/
├── Contexts/          # React context providers
├── DTO/               # DTOs from the API spec
├── components/        # UI components
├── config/            # Axios config
├── lib/               # Utilities and helpers
├── mocks/             # Not used anymore, but kept just in case
├── utilities/         # API clients and helpers
├── views/             # Page components
├── routes.ts          # Page url to component connection
└── main.tsx           # App entry point
```

---

## Formatting & Commits

- Code formatting is enforced via **Prettier**
- Linting via **ESLint**
- Type safety via **ts**
- **Husky** + **lint-staged** run all 3 layers of checks on commit, aborts of anything fails
