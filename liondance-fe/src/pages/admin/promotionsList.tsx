import React, { useEffect, useState } from 'react';
import { Loader, Card } from '@mantine/core';
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
    <div className="promotions-list">
      <h1>Promotions</h1>
      {promotions.length === 0 ? (
        <p>No promotions available.</p>
      ) : (
        promotions.map((promotion) => (
          <Card
            key={promotion.promotionId}
            shadow="sm"
            p="lg"
            radius="md"
            withBorder
            onClick={() => navigate(`/promotions/${promotion.promotionId}`)}
          >
            <h2>{promotion.promotionName}</h2>
            <p>
              <strong>Discount:</strong> {promotion.discountRate * 100}%
            </p>
            <p>
              <strong>Status:</strong> {promotion.promotionStatus}
            </p>
          </Card>
        ))
      )}
    </div>
  );
};

export default PromotionsList;

