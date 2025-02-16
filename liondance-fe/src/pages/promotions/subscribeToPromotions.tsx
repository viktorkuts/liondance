import React, { useState, useEffect } from "react";
import { User } from "@/models/Users";
import { Button, Modal, Checkbox, Notification } from "@mantine/core";
import { useAxiosInstance } from "@/utils/axiosInstance";
import { useParams, useNavigate } from "react-router-dom";
import "./promotions.css";
import { useTranslation } from "react-i18next";

interface SubscribeToPromotionsProps {
    openModal: boolean;
}

const SubscribeToPromotions: React.FC<SubscribeToPromotionsProps> = ({ openModal }) => {
    const { t } = useTranslation();
    const [opened, setOpened] = useState(openModal);
    const [isSubscribed, setIsSubscribed] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
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
            setError(t("User ID is missing."));
            return;
        }
        setLoading(true);
        setError(null);
        setSuccess(null);
        try {
            await subscribeToPromotions(userId, isSubscribed);
            setSuccess(t("Successfully subscribed to promotions."));
            setOpened(false);
            navigate("/");
        } catch {
            setError(t("Failed to subscribe to promotions. Please try again later."));
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Modal opened={opened} onClose={() => setOpened(false)} title={t("Subscribe to Promotions")}>
                <Checkbox
                    label={t("I want to receive promotional emails")}
                    checked={isSubscribed}
                    onChange={(event) => setIsSubscribed(event.currentTarget.checked)}
                />
                <div className="note">{t("*leave unchecked to stop receiving emails")}</div>
                {error && <div className="error">{error}</div>}
                {success && <div className="success">{success}</div>}
                <Button onClick={handleSubscribe} loading={loading}>
                    {t("Submit")}
                </Button>
            </Modal>
            {success && (
                <Notification onClose={() => setSuccess(null)} color="teal" title={t("Success")}>
                    {success}
                </Notification>
            )}
        </>
    );
};

export default SubscribeToPromotions;