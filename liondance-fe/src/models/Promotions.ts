export interface Promotion{
    promotionid: String
    promotionName: String
    discountRate: Number
    startDate: Date
    endDate: Date
    promotionStatus: promotionStatus
}

export enum promotionStatus{
ACTIVE = 'ACTIVE',
INACTIVE = 'INACTIVE'
}