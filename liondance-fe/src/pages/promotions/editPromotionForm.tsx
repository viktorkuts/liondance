import React, { useState } from "react";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { Promotion } from "@/models/Promotion";
import { useTranslation } from "react-i18next";
import { Button } from "@mantine/core";

interface EditPromotionFormProps {
  promotion: Promotion;
  onCancel: () => void;
  onSuccess: () => void;
}

const EditPromotionForm: React.FC<EditPromotionFormProps> = ({
  promotion,
  onCancel,
  onSuccess,
}) => {
  const { t } = useTranslation();
  const axiosInstance = useAxiosInstance();
  const [promotionName, setPromotionName] = useState(promotion.promotionName);
  const [discountRate, setDiscountRate] = useState(promotion.discountRate);
  const [startDate, setStartDate] = useState<Date>(new Date(promotion.startDate));
  const [endDate, setEndDate] = useState<Date>(new Date(promotion.endDate));
  const [promotionStatus, setPromotionStatus] = useState(promotion.promotionStatus);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const formatDateForInput = (date: Date) => {
    return date.toISOString().split('T')[0];
  };

  const handleStartDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartDate(new Date(e.target.value));
  };

  const handleEndDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEndDate(new Date(e.target.value));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await axiosInstance.patch(`/promotions/${promotion.promotionId}`, {
        promotionName,
        discountRate,
        startDate,
        endDate,
        promotionStatus,
      });

      if (response.data) {
        onSuccess();
      }
    } catch {
      setError(t("Failed to update promotion. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="promotion-form">
      <div className="promotion-form-fields">
        <div className="form-group">
          <label>{t("Promotion Name")}</label>
          <input
            type="text"
            className="form-input"
            value={promotionName}
            onChange={(e) => setPromotionName(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("Discount Rate")}</label>
          <input
            type="number"
            className="form-input"
            value={discountRate * 100}
            onChange={(e) => setDiscountRate(parseFloat(e.target.value) / 100)}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("Start Date")}</label>
          <input
            type="date"
            className="form-input"
            value={formatDateForInput(startDate)}
            onChange={handleStartDateChange}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("End Date")}</label>
          <input
            type="date"
            className="form-input"
            value={formatDateForInput(endDate)}
            onChange={handleEndDateChange}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("Status")}</label>
          <select 
            className="form-input"
            value={promotionStatus}
            onChange={(e) => setPromotionStatus(e.target.value as Promotion["promotionStatus"])}
            required
          >
            <option value="ACTIVE">{t("Active")}</option>
            <option value="INACTIVE">{t("Inactive")}</option>
          </select>
        </div>
        {error && <div className="form-error">{error}</div>}
        <div className="promotion-actions">
          <Button type="button" onClick={onCancel} className="action-button back-button">
            {t("Cancel")}
          </Button>
          <Button type="submit" className="action-button edit-button" loading={loading}>
            {t("Save Changes")}
          </Button>
        </div>
      </div>
    </form>
  );
};

export default EditPromotionForm