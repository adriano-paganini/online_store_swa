import { getErrorMessage } from '@/config/config';
import { clsx, type ClassValue } from 'clsx';
import { toast } from 'sonner';
import { twMerge } from 'tailwind-merge';

/**
 * Utility function for conditionally joining Tailwind CSS class names.
 *
 * Combines `clsx` for conditional logic and `tailwind-merge`
 * to intelligently merge conflicting Tailwind classes.
 *
 * @param inputs - A list of class values (strings, arrays, objects, etc.)
 * @returns A single merged className string
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * Takes the first character of up to the first two words
 * and returns them as uppercase initials.
 *
 * @example
 * getInitials("Jane Doe") // "JD"
 * getInitials("John") // "J"
 *
 * @param name - The full name to extract initials from
 * @returns The uppercase initials string
 */
export const getInitials = (name: string) =>
  name
    .split(' ')
    .slice(0, 2)
    .map((word) => word[0]?.toUpperCase())
    .join('');

/**
 * Displays a toast notification for API or unknown errors.
 *
 * @param err - The error thrown by an API call or unknown source
 */
export const toastApiError = (err: unknown): void => {
  toast.error(getErrorMessage(err));
};
