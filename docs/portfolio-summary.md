# Event-Driven E-Commerce Platform Portfolio Summary

## Short Description

A full-stack e-commerce system built to explore backend architecture, transactional event-driven workflows, persistence design, notification dispatch, and broad automated testing rather than just basic webshop CRUD functionality.

## Medium Description

This project is a full-stack e-commerce platform built with Spring Boot and React/Vite. Customers can browse products, manage a persistent server-side cart, place orders, pay for them, review products, and subscribe to product changes. The most interesting part of the implementation is the backend architecture: product updates and successful payments publish Spring application events, listeners fan those events out to user notifications, and asynchronous delivery services update notification status after commit. The system also includes role-aware APIs, soft deletion for selected entities, pagination/filtering/sorting, Docker packaging, and a large backend test suite covering controllers, services, repositories, mappers, events, and listeners.

## My Role

- Implemented and refined the event-driven notification architecture around product updates, subscriptions, and order-confirmation email flow.
- Implemented or improved backend workflows for sorting, stock handling, order/payment behavior, and user-related management logic.
- Added and expanded automated tests for events, listeners, order email composition, and related backend components.
- Contributed architecture and project documentation, including UML and README updates.

## Technical Highlights

- Transactional Spring events published from domain services rather than directly from controllers.
- Asynchronous `@TransactionalEventListener` notification pipeline with persisted notification status.
- Server-side cart state and order creation based on authoritative backend data.
- Role-aware REST API and matching frontend route boundaries.
- Broad backend test coverage across services, controllers, repositories, mappers, listeners, and helper logic.
- Docker-based packaging that bundles the frontend build into the backend runtime image.

## Architecture Summary

The application is a single Spring Boot backend with a React frontend. The backend exposes REST endpoints for products, carts, orders, subscriptions, notifications, users, addresses, reviews, and authentication, persists domain data with JPA, and publishes Spring application events for product changes and successful order payment. Those events are consumed asynchronously after commit to create and deliver user notifications. The frontend consumes the REST API and separates public, authenticated, manager, and admin views.

## Event-Driven Architecture

Events are implemented with Spring’s in-process application event system. Product updates emit typed product events such as price, discount, restock, description, and name updates. A subscription listener matches subscribers, persists notifications, and republishes channel-specific email or SMS events. Successful payment emits an order-completion event that triggers asynchronous email confirmation. Notification entities record whether delivery is queued, sent, or failed.

## Testing Strategy

The backend uses layered tests: `MockMvc` controller tests for API behavior, service tests for business logic, repository tests for query behavior, mapper/helper tests for transformation logic, and dedicated listener/event tests for the event-driven notification pipeline. Async behavior is made deterministic in tests through a synchronous executor override. Frontend tooling for Vitest, ESLint, and Prettier exists, but the committed frontend test coverage is limited.

## Key Technologies

- Java 21
- Spring Boot
- Spring Security with JWT
- Spring Data JPA
- Spring application events
- H2
- React
- TypeScript
- Vite
- Docker

## Interesting Engineering Challenges

- Decoupling product and order state changes from notification side effects without introducing uncommitted-event problems.
- Designing a subscription model that maps product changes to per-user, per-channel notification fan-out.
- Keeping cart and order creation authoritative on the backend instead of trusting client-assembled state.
- Testing asynchronous event-driven behavior deterministically.
- Balancing soft deletion, order history retention, and cleanup of related entities such as subscriptions.

## Suggested Portfolio Case-Study Structure

1. Problem framing: why this project is more than a standard webshop.
2. High-level architecture: frontend, backend, persistence, event pipeline.
3. Event-driven workflows: product subscriptions and order-confirmation notifications.
4. Backend design decisions: server-side cart, soft deletion, filtering/sorting/pagination.
5. Testing strategy: how architectural behavior is verified.
6. My contribution: event architecture, backend workflows, tests, and documentation.
