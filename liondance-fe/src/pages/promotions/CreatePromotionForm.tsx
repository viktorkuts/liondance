/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from "react";
import { usePromotionService } from "@/services/promotionService";
import { promotionStatus } from "@/models/Promotion";
import { Button } from "@mantine/core";
import { useTranslation } from "react-i18next";
import "./promotions.css";

interface CreatePromotionFormProps {
  onSuccess: () => void;
  onCancel: () => void;
}

const CreatePromotionForm: React.FC<CreatePromotionFormProps> = ({ onSuccess, onCancel }) => {
  const { t } = useTranslation();
  const { createPromotion } = usePromotionService();
  const [promotionName, setPromotionName] = useState("");
  const [discountRate, setDiscountRate] = useState<number>(0);
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [status, setStatus] = useState<promotionStatus>(promotionStatus.ACTIVE);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await createPromotion({
        promotionName,
        discountRate: discountRate / 100, 
        startDate: new Date(startDate),
        endDate: new Date(endDate),
        promotionStatus: status,
      });
      onSuccess();
    } catch (err: any) {
      setError(t("Failed to create promotion. Please try again.") + " " + err.message);
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
          <label>{t("Discount Rate (%)")}</label>
          <input
            type="number"
            className="form-input"
            value={discountRate}
            onChange={(e) => setDiscountRate(parseFloat(e.target.value))}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("Start Date")}</label>
          <input
            type="date"
            className="form-input"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("End Date")}</label>
          <input
            type="date"
            className="form-input"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("Status")}</label>
          <select
            className="form-input"
            value={status}
            onChange={(e) => setStatus(e.target.value as promotionStatus)}
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
            {t("Create Promotion")}
          </Button>
        </div>
      </div>
    </form>
  );
};

export default CreatePromotionForm;
