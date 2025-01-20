import React, { useEffect, useState } from 'react';
import { Loader } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { getAllPromotions } from '@/services/promotionService';
import { Promotion } from '@/models/Promotions';
import './promotions.css';

const PromotionsList: React.FC = () => {
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPromotions = async () => {
      try {
        const data = await getAllPromotions();
        if (Array.isArray(data)) {
          setPromotions(data);
        } else {
          throw new Error('Unexpected response format');
        }
      } catch (err) {
        setError('Failed to fetch promotions. Please try again later.' +err);
      } finally {
        setLoading(false);
      }
    };

    fetchPromotions();
  }, []);

  if (loading) return <Loader />;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="promotions-container">
     <h1 className="promotions-title">All Promotions</h1>
      {promotions.length > 0 ? promotions.map((promotion, index) => (
        <div 
          key={promotion.promotionId || `promotion-${index}`} 
          className="promotion-card"
          onClick={() => navigate(`/promotions/${promotion.promotionId}`)}
          style={{ cursor: 'pointer' }}
        >
          <div className="promotion-info">
            <div className="promotion-header">{promotion.promotionName}</div>
            <div className="promotion-details">
              <div className="promotion-detail">
                Discount: {(promotion.discountRate * 100)}%
              </div>
              <div className="promotion-detail promotion-status">
                Status: {promotion.promotionStatus}
              </div>
            </div>
          </div>
        </div>
      )) : (
        <div className="promotions-list">No promotions found.</div>
      )}
    </div>
  );
};

export default PromotionsList;

