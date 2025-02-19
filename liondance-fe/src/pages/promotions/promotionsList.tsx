import React, { useEffect, useState } from "react";
import { Loader } from "@mantine/core";
import { useNavigate } from "react-router-dom";
import { usePromotionService } from "@/services/promotionService";
import { Promotion } from "@/models/Promotion";
import { useTranslation } from "react-i18next";
import "./promotions.css";
import CreatePromotionForm from "./CreatePromotionForm";

const PromotionsList: React.FC = () => {
  const { t } = useTranslation();
  const promotionService = usePromotionService();
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState<boolean>(false);
  const navigate = useNavigate();

  const fetchPromotions = async () => {
    try {
      const data = await promotionService.getAllPromotions();
      if (Array.isArray(data)) {
        setPromotions(data);
      } else {
        throw new Error("Unexpected response format");
      }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      setError(
        t("Failed to fetch promotions. Please try again later.") +
          " " +
          err.message
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPromotions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [promotionService, t]);

  const handleCreateSuccess = () => {
    setShowCreateForm(false);
    fetchPromotions();
  };

  if (loading) return <Loader />;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="promotions-container">
      <h1 className="promotions-title">{t("All Promotions")}</h1>
      <button
        className="action-button edit-button"
        onClick={() => setShowCreateForm(!showCreateForm)}
      >
        {showCreateForm ? t("Cancel") : t("Create Promotion")}
      </button>
      {showCreateForm && (
        <div className="promotion-card">
          <CreatePromotionForm
            onSuccess={handleCreateSuccess}
            onCancel={() => setShowCreateForm(false)}
          />
        </div>
      )}
      {promotions.length > 0 ? (
        promotions.map((promotion, index) => (
          <div
            key={promotion.promotionId || `promotion-${index}`}
            className="promotion-card"
            onClick={() => navigate(`/promotions/${promotion.promotionId}`)}
            style={{ cursor: "pointer" }}
          >
            <div className="promotion-info">
              <div className="promotion-header">
                {t(promotion.promotionName)}
              </div>
              <div className="promotion-details">
                <div className="promotion-detail">
                  {t("Discount")}: {promotion.discountRate * 100}%
                </div>
                <div className="promotion-detail promotion-status">
                  {t("Status")}: {t(promotion.promotionStatus)}
                </div>
              </div>
            </div>
          </div>
        ))
      ) : (
        <div className="promotions-list">{t("No promotions found.")}</div>
      )}
    </div>
  );
};

export default PromotionsList;
