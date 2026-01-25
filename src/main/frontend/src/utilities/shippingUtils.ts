import { ShippingMethod } from '@/DTO/order.types';

type TShippingMethodConfig = {
  key: ShippingMethod;
  label: string;
  description: string;
};

export const SHIPPING_METHODS: TShippingMethodConfig[] = [
  {
    key: ShippingMethod.FAIRY_DUST_DISPATCH,
    label: 'Fairy Dust Dispatch',
    description: '✨ Probably enchanted.',
  },
  {
    key: ShippingMethod.CARRIER_PIGEON,
    label: 'Carrier Pigeon',
    description: 'May stop for snacks. Or get lost.',
  },
  {
    key: ShippingMethod.WELL_FIGURE_IT_OUT,
    label: "We'll Figure It Out",
    description: 'No further details.',
  },
];

export const getShippingMethodLabel = (method: ShippingMethod): string =>
  SHIPPING_METHODS.find((m) => m.key === method)?.label ?? method;
