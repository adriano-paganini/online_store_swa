# G4T1

## Project Context

PS Software Architecture (WS25/26), University of Innsbruck.

## Baseline

This project is based on the official **SWA Skeleton Project** provided by the course instructors:
<https://git.uibk.ac.at/informatik/qe/swa_swe/swa/swa-skeleton>

## Requirements

- Java 21
- Maven
- Node.js / npm
- Free ports: `8080`, `3000`
- internet connection (for SMPT)

## Running the application

### Startup

#### Backend

```bash
mvn spring-boot:run
```

#### Frontend

```bash
cd src/main/frontend
cp .env.example .env
npm run dev
```

The `.env` should include `VITE_BACKEND_SERVER_URL=http://localhost:8080`.

For more details on frontend see: `src/main/frontend/README.md`

### Usage

Frontend runs at: <http://localhost:3000/>

You can log in with:

- "admin" and "passwd"
  - roles: ADMIN, CUSTOMER
- "user1" and "passwd"
  - roles: MANAGER
- "user2" and "passwd"
  - role: CUSTOMER
- "elvis" and "passwd"
  - role: ADMIN
- "adriano" and "passwd"
  - roles: ADMIN, CUSTOMER

We recommend to use user `adriano` for exploring features,
since he has already predefined orders, subscriptions and notifications which can be inspected.

To receive notifications at your own email address, update the current user’s email to the desired address.

Email delivery depends on external SMTP service. Failures are handled gracefully.

## Documentation

Documentation, Gen-AI statement and other information can be found in `docs/` or the Gitlab Wiki.

---

If there are any problems, feel free to contact us via email.