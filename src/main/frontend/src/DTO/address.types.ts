export type TAddressDTO = {
  id: number;
  country: string;
  city: string;
  postalCode: string;
  street: string;
  number: string;
  extra?: string;
};

export type TAddressCreateDTO = {
  country: string;
  city: string;
  postalCode: string;
  street: string;
  number: string;
  extra?: string;
};

export type TAddressUpdateDTO = {
  country?: string;
  city?: string;
  postalCode?: string;
  street?: string;
  number?: string;
  extra?: string;
};
