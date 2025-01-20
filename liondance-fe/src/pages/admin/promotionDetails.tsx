import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Loader } from '@mantine/core';
import { getPromotionById } from '@/services/promotionService';
import { Promotion } from '@/models/Promotions';
import './promotions.css';

const PromotionDetails: React.FC = () => {
  const { promotionId } = useParams<{ promotionId: string }>();
  const [promotion, setPromotion] = useState<Promotion | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPromotion = async () => {
      try {
        const data = await getPromotionById(promotionId!);
        setPromotion(data);
      } catch (err) {
        setError('Failed to load promotion details. Please try again later. '+err);
      } finally {
        setLoading(false);
      }
    };

    fetchPromotion();
  }, [promotionId]);

  if (loading) return <Loader />;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="promotions-container">
      <h1 className="promotions-title">Promotion Details</h1>
      {promotion && (
        <div className="promotion-card">
          <div className="promotion-info">
            <div className="promotion-header">{promotion.promotionName}</div>
            <div className="promotion-details">
              <div className="promotion-detail">
                <strong>Discount Rate:</strong> {promotion.discountRate * 100}%
              </div>
              <div className="promotion-detail">
                <strong>Start Date:</strong>{' '}
                {new Date(promotion.startDate).toLocaleDateString('en-CA')}
              </div>
              <div className="promotion-detail">
                <strong>End Date:</strong>{' '}
                {new Date(promotion.endDate).toLocaleDateString('en-CA')}
              </div>
              <div className="promotion-detail promotion-status">
                <strong>Status:</strong> {promotion.promotionStatus}
              </div>
              <div className="promotion-actions">
                <button 
                  className="action-button edit-button"
                  onClick={() => console.log('Edit clicked')}
                >
                  Edit
                </button>
                <Link to="/promotions" className="action-button back-button">
                  Back
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PromotionDetails;

