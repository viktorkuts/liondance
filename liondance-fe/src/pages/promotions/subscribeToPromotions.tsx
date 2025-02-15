import React, { useState, useEffect } from "react";
import { User } from "@/models/Users";
import { Button, Modal, Checkbox } from "@mantine/core";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { useParams, useNavigate } from "react-router-dom";
import "./promotions.css";

interface SubscribeToPromotionsProps {
    openModal: boolean;
}

const SubscribeToPromotions: React.FC<SubscribeToPromotionsProps> = ({ openModal }) => {
    const [opened, setOpened] = useState(openModal);
    const [isSubscribed, setIsSubscribed] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const axiosInstance = useAxiosInstance();
    const { userId } = useParams<{ userId: string }>();
    const navigate = useNavigate();

    useEffect(() => {
        setOpened(openModal);
    }, [openModal]);

    const subscribeToPromotions = async (userId: string, isSubscribed: boolean): Promise<User> => {
        const response = await axiosInstance.patch<User>(`/users/${userId}/subscription`, { isSubscribed });
        return response.data;
    };

    const handleSubscribe = async () => {
        if (!userId) {
            setError("User ID is missing.");
            return;
        }
        setLoading(true);
        setError(null);
        try {
            await subscribeToPromotions(userId, isSubscribed);
            setOpened(false);
            navigate("/");
        } catch {
            setError("Failed to subscribe to promotions. Please try again later.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Modal opened={opened} onClose={() => setOpened(false)} title="Subscribe to Promotions">
                <Checkbox
                    label="I want to receive promotional emails"
                    checked={isSubscribed}
                    onChange={(event) => setIsSubscribed(event.currentTarget.checked)}
                />
                <div className="note">*leave unchecked to stop receiving emails</div>
                {error && <div className="error">{error}</div>}
                <Button onClick={handleSubscribe} loading={loading}>
                    Submit
                </Button>
            </Modal>
        </>
    );
};

export default SubscribeToPromotions;