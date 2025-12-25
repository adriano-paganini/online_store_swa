import { render, screen } from '@testing-library/react';
import App from './App';

test('renders welcome text', async () => {
  render(<App />);
  const linkElement = await screen.findByText(/Audio Management System/i);
  expect(linkElement).toBeInTheDocument();
});
