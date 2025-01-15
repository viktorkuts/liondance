import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Loader, Card, Button } from '@mantine/core';
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
    <div className="promotion-details">
      <h1>Promotion Details</h1>
      {promotion && (
        <Card shadow="sm" p="lg" radius="md" withBorder>
          <h2>{promotion.promotionName}</h2>
          <p><strong>Discount Rate:</strong> {promotion.discountRate*100}%</p>
          <p>
            <strong>Start Date:</strong>{' '}
            {new Date(promotion.startDate).toLocaleDateString('en-CA')}
          </p>
          <p>
            <strong>End Date:</strong>{' '}
            {new Date(promotion.endDate).toLocaleDateString('en-CA')}
          </p>
          <p><strong>Status:</strong> {promotion.promotionStatus}</p>
          <Button color='red' size='sm' radius='sm'> Edit </Button>
          <Button color='yellow' size='sm' radius='sm'component={Link} to="/promotions" >Back</Button>
        </Card>
      )}
    </div>
  );
};

export default PromotionDetails;

