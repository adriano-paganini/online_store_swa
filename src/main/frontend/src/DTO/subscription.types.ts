import { NotificationType } from './notification.types';

export enum SubscriptionType {
  NAMEUPDATE = 'NAMEUPDATE',
  DESCRIPTIONUPDATE = 'DESCRIPTIONUPDATE',
  PRICEUPDATE = 'PRICEUPDATE',
  RESTOCK = 'RESTOCK',
  DISCOUNTUPDATE = 'DISCOUNTUPDATE',
}

export type TSubscriptionDTO = {
  id: number;
  userId: number;
  productId: number;
  types: SubscriptionType[];
  channels: NotificationType[];
};

export type TSubscriptionCreateDTO = {
  productId: number;
  types: SubscriptionType[];
  channels: NotificationType[];
};

export type TSubscriptionUpdateDTO = {
  types: SubscriptionType[];
  channels: NotificationType[];
};
