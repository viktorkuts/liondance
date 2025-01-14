import axiosInstance from "@/utils/axiosInstance";
import { Promotion } from '@/models/Promotions';

export const getAllPromotions = async (): Promise<Promotion[]> => {
  const response = await axiosInstance.get<Promotion[]>('/promotions');
  return response.data;
};

export const getPromotionById = async (promotionId: string): Promise<Promotion> => {
  const response = await axiosInstance.get<Promotion>(`/promotions/${promotionId}`);
  return response.data;
};

export default {
    getAllPromotions,
    getPromotionById
};