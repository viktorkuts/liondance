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

export enum PaymentMethod{
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
}

export interface Event {
    id? : string;
    firstName: string;
    middleName?: string;
    lastName: string;
    email: string;
    phone: string;
    address: Address;
    eventDateTime: string;
    eventType: EventType;
    paymentMethod: PaymentMethod;
    specialRequest?: string;
    eventStatus: EventStatus;
}