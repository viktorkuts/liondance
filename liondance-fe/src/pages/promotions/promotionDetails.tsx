import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Loader } from "@mantine/core";
import { usePromotionService } from "@/services/promotionService";
import { Promotion } from "@/models/Promotion";
import { useTranslation } from "react-i18next";
import EditPromotionForm from "./editPromotionForm";
import "./promotions.css";

const PromotionDetails: React.FC = () => {
  const { t } = useTranslation();
  const promotionService = usePromotionService();
  const { promotionId } = useParams<{ promotionId: string }>();
  const [promotion, setPromotion] = useState<Promotion | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);

  const fetchPromotion = async () => {
    try {
      const data = await promotionService.getPromotionById(promotionId!);
      setPromotion(data);
    } catch (err) {
      setError(
        t("Failed to load promotion details. Please try again later.") + " " + err
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPromotion();
  }, [promotionId, promotionService, t]);

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleEditCancel = () => {
    setIsEditing(false);
  };

  const handleEditSuccess = () => {
    setIsEditing(false);
    fetchPromotion(); 

  };

  if (loading) return <Loader />;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="promotions-container">
      <h1 className="promotions-title">
        {isEditing ? t("Edit Promotion") : t("Promotion Details")}
      </h1>
      {promotion && !isEditing && (
        <div className="promotion-card">
          <div className="promotion-info">
            <div className="promotion-header">{t(promotion.promotionName)}</div>
            <div className="promotion-details">
              <div className="promotion-detail">
                <strong>{t("Discount Rate")}:</strong> {promotion.discountRate * 100}%
              </div>
              <div className="promotion-detail">
                <strong>{t("Start Date")}:</strong>{' '}
                {new Date(promotion.startDate).toLocaleDateString('en-CA')}
              </div>
              <div className="promotion-detail">
                <strong>{t("End Date")}:</strong>{' '}
                {new Date(promotion.endDate).toLocaleDateString('en-CA')}
              </div>
              <div className="promotion-detail promotion-status">
                <strong>{t("Status")}:</strong> {t(promotion.promotionStatus)}
              </div>
              <div className="promotion-actions">
                <Link to="/promotions" className="action-button back-button">
                  {t("Back")}
                </Link>
                <button 
                  className="action-button edit-button"
                  onClick={handleEditClick}
                >
                  {t("Edit")}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
      {promotion && isEditing && (
        <div className="promotion-card">
          <EditPromotionForm
            promotion={promotion}
            onCancel={handleEditCancel}
            onSuccess={handleEditSuccess}
          />
        </div>
      )}
    </div>
  );
};

export default PromotionDetails;