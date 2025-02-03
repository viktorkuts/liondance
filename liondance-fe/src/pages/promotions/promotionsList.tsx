import React, { useEffect, useState } from "react";
import { Loader } from "@mantine/core";
import { useNavigate } from "react-router-dom";
import { usePromotionService } from "@/services/promotionService";
import { Promotion } from "@/models/Promotion";
import { useTranslation } from "react-i18next";
import "./promotions.css";

const PromotionsList: React.FC = () => {
  const { t } = useTranslation();
  const promotionService = usePromotionService();
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPromotions = async () => {
      try {
        const data = await promotionService.getAllPromotions();
        if (Array.isArray(data)) {
          setPromotions(data);
        } else {
          throw new Error("Unexpected response format");
        }
      } catch (err) {
        setError(t("Failed to fetch promotions. Please try again later.") + err);
      } finally {
        setLoading(false);
      }
    };

    fetchPromotions();
  }, [promotionService, t]);

  if (loading) return <Loader />;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="promotions-container">
      <h1 className="promotions-title">{t("All Promotions")}</h1>
      {promotions.length > 0 ? promotions.map((promotion, index) => (
        <div 
          key={promotion.promotionId || `promotion-${index}`} 
          className="promotion-card"
          onClick={() => navigate(`/promotions/${promotion.promotionId}`)}
          style={{ cursor: 'pointer' }}
        >
          <div className="promotion-info">
            <div className="promotion-header">{t(promotion.promotionName)}</div>
            <div className="promotion-details">
              <div className="promotion-detail">
                {t("Discount")}: {(promotion.discountRate * 100)}%
              </div>
              <div className="promotion-detail promotion-status">
                {t("Status")}: {t(promotion.promotionStatus)}
              </div>
            </div>
          </div>
        </div>
      )) : (
        <div className="promotions-list">{t("No promotions found.")}</div>
      )}
    </div>
  );
};

export default PromotionsList;
