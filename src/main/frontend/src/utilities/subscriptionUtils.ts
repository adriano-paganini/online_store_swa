import { SubscriptionType } from '@/DTO/subscription.types';

export const SubscriptionTypeLabels: Record<SubscriptionType, string> = {
  [SubscriptionType.NAMEUPDATE]: 'Name changed',
  [SubscriptionType.DESCRIPTIONUPDATE]: 'Description changed',
  [SubscriptionType.PRICEUPDATE]: 'Price changed',
  [SubscriptionType.RESTOCK]: 'Back in stock',
  [SubscriptionType.DISCOUNTUPDATE]: 'Discount available',
};
