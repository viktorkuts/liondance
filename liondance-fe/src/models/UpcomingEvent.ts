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

export enum EventPrivacy {
    PUBLIC = "PUBLIC",
    PRIVATE = "PRIVATE",
}

export interface UpcomingEvent {
    eventId? : string;
    eventAddress: Address;
    eventDateTime: Date;
    eventType: EventType;
    eventPrivacy: EventPrivacy;
}