export interface Address {
  streetAddress: string;
  city: string;
  state: string;
  zip: string;
}

export enum EventType {
  WEDDING = "WEDDING",
  PARADE = "PARADE",
  FESTIVAL = "FESTIVAL",
  BIRTHDAY = "BIRTHDAY",
  OTHER = "OTHER",
}

export enum PaymentMethod {
  CASH = "CASH",
  PAYPAL = "PAYPAL",
  CREDIT = "CREDIT",
  DEBIT = "DEBIT",
  ETRANSFER = "ETRANSFER",
}

export enum EventStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  CANCELLED = "CANCELLED",
  COMPLETED = "COMPLETED",
}

export enum EventPrivacy {
  PUBLIC = "PUBLIC",
  PRIVATE = "PRIVATE",
}

export interface Event {
  eventId?: string;
  clientId?: string;
  venue: Address;
  eventDateTime: Date;
  eventType: EventType;
  paymentMethod: PaymentMethod;
  specialRequest?: string;
  eventStatus: EventStatus;
  eventPrivacy: EventPrivacy;
}
