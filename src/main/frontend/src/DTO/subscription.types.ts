import { NotificationType } from './notification.types';
import { TPaginationParams } from './pagination.types';
import { TProductDTO } from './product.types';

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

export type TPopulatedSubscriptionDTO = {
  id: number;
  userId: number;
  types: SubscriptionType[];
  channels: NotificationType[];
  product: TProductDTO;
};

export type TSubscriptionQueryParams = TPaginationParams & {
  types?: SubscriptionType[];
  channels?: NotificationType[];
};
