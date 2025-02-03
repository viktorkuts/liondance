import { useAxiosInstance } from "@/utils/axiosInstance";
import { Promotion } from "@/models/Promotion";

export const usePromotionService = () => {
  const axiosInstance = useAxiosInstance();
  const getAllPromotions = async (): Promise<Promotion[]> => {
    const response = await axiosInstance.get<Promotion[]>("/promotions");
    return response.data;
  };

  const getPromotionById = async (promotionId: string): Promise<Promotion> => {
    const response = await axiosInstance.get<Promotion>(
      `/promotions/${promotionId}`
    );
    return response.data;
  };

  const updatePromotion = async (promotionId: string, promotionData: Partial<Promotion>): Promise<Promotion> => {
    const response = await axiosInstance.patch(`/promotions/${promotionId}`, promotionData);
    return response.data;
  };

  return {
    getAllPromotions,
    getPromotionById,
    updatePromotion
  };
};
