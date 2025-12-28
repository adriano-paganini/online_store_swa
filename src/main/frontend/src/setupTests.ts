import '@testing-library/jest-dom';
import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterEach, vi } from 'vitest';

// to make it works like Jest (auto-mocking);
vi.mock('react-dom');

// runs a cleanup after each test case (e.g. clearing jsdom)
afterEach(() => {
  vi.clearAllMocks();
  vi.resetAllMocks();
  cleanup();
});
