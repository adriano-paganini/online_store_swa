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

## Running the application

### Backend

```bash
mvn spring-boot:run
```

### frontend

Ensure backend URL is `localhost:8080`:

```bash
cd src/main/frontend
cp .env.example .env
npm run dev
```

Frontend runs at: <http://localhost:3000/>

You can log in with:

- "admin" and "passwd"
- "user1" and "passwd"
- "user2" and "passwd"
- "elvis" and "passwd"

See also: `src/main/frontend/README.md`

## Documentation

Documentation for the Software Requirements can be found in `docs/` and the Gitlab Wiki.
