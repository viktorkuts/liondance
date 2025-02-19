import { Event } from "./Event";

export interface Address {
  streetAddress: string;
  city: string;
  state: string;
  zip: string;
}

export enum Role {
  STUDENT = "STUDENT",
  CLIENT = "CLIENT",
  ADMIN = "ADMIN",
  STAFF = "STAFF",
}

export enum RegistrationStatus {
  NEW = "NEW",
  PENDING = "PENDING",
  ACTIVE = "ACTIVE",
  INACTIVE = "INACTIVE",
}

export interface RegistrationStatusModel {
  registrationStatus: RegistrationStatus;
}

export interface User {
  userId?: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  dob: string;
  email: string;
  phone?: string;
  address: Address;
  roles?: Role[];
  isSubscribed?: boolean;
}

export interface Student extends User {
  joinDate?: string;
  registrationStatus?: RegistrationStatus;
  parentFirstName?: string;
  parentMiddleName?: string;
  parentLastName?: string;
  parentEmail?: string;
  parentPhone?: string;
}

export interface Client extends User {
  activeEvents?: Event[];
  pastEvents?: Event[];
}

export enum PerformerStatus {
  PENDING = "PENDING",
  ACCEPTED = "ACCEPTED",
  REJECTED = "REJECTED",
  TENTATIVE = "TENTATIVE",
}

export interface PerformerResponseModel {
  performer: User;
  status: PerformerStatus;
}

export interface PerformerStatusResponseModel {
  performerInfo: PerformerResponseModel;
  eventInfo: Event;
}

export interface PerformerStatusRequestModel {
  status: PerformerStatus;
}
