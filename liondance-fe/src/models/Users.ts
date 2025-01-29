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
  joinDate?: string;
  activeEvents?: string[];
  pastEvents?: string[];
}
