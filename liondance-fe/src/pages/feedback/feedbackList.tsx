import React, { useEffect, useState } from "react";
import { useFeedbackService } from "../../services/feedbackService";
import { Feedback, Visibility } from "../../models/Feedback";
import "./feedbackList.css";
import { useParams, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Button } from "@mantine/core";

const FeedbackList: React.FC = () => {
  const { t } = useTranslation();
  const { eventId } = useParams<{ eventId: string }>();
  const feedbackService = useFeedbackService();
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchFeedbacks = async () => {
    if (!eventId) return;
    try {
      const data = await feedbackService.getFeedbacksByEventId(eventId);
      setFeedbacks(data);
    } catch {
      setError(t("Failed to fetch feedback"));
    } finally {
      setLoading(false);
    }
  };

  const updateFeedbackStatus = async (
    feedbackId: string,
    status: Visibility
  ) => {
    try {
      await feedbackService.updateFeedbackStatus(feedbackId, status);
    } catch {
      setError(t("Failed to update status for a feedback"));
    } finally {
      fetchFeedbacks();
    }
  };

  useEffect(() => {
    if (!eventId) {
      setError(t("Event ID is not defined"));
      setLoading(false);
      return;
    }

    fetchFeedbacks();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [eventId]);

  if (loading) {
    return <div>{t("Loading...")}</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="feedback-list">
      <h2>{t("Feedback for Event")}</h2>
      <ul>
        {feedbacks.map((feedback) => (
          <li key={feedback.feedbackId}>
            <p>
              {t("Comment")}: {feedback.feedback}
            </p>
            <p>
              {t("Rating")}: {feedback.rating}
            </p>
            <p>
              {t("Time")}: {new Date(feedback.timestamp).toLocaleString()}
            </p>
            <Button
              onClick={() => {
                if (!feedback.feedbackId) return;
                updateFeedbackStatus(
                  feedback.feedbackId,
                  feedback.visibility == Visibility.PUBLIC
                    ? Visibility.PRIVATE
                    : Visibility.PUBLIC
                );
              }}
            >
              Make{" "}
              {feedback.visibility == Visibility.PUBLIC ? "Private" : "Public"}
            </Button>
          </li>
        ))}
      </ul>
      <button onClick={() => navigate("/events")}>{t("Back to Events")}</button>
    </div>
  );
};

export default FeedbackList;
