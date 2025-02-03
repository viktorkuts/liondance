export interface Promotion{
    promotionId: string
    promotionName: string
    discountRate: number
    startDate: Date
    endDate: Date
    promotionStatus: promotionStatus
}

export enum promotionStatus{
ACTIVE = 'ACTIVE',
INACTIVE = 'INACTIVE'
}