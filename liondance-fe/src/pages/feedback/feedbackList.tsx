import React, { useEffect, useState } from "react";
import { useFeedbackService } from "../../services/feedbackService";
import { Feedback } from "../../models/Feedback";
import "./feedbackList.css";
import { useParams, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

interface FeedbackListProps {
  eventId: string;
}

const FeedbackList: React.FC<FeedbackListProps> = () => {
  const { t } = useTranslation();
  const feedbackService = useFeedbackService();
  const { eventId } = useParams<{ eventId: string }>();
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        if (eventId) {
          const data = await feedbackService.getFeedbacksByEventId(eventId);
          setFeedbacks(data);
        } else {
          setError(t("Event ID is not defined"));
        }
      } catch {
        setError(t("Failed to fetch feedback"));
      } finally {
        setLoading(false);
      }
    };

    fetchFeedbacks();
  }, [eventId, feedbackService, t]);

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
            <p>{t("Comment")}: {feedback.feedback}</p>
            <p>{t("Rating")}: {feedback.rating}</p>
            <p>{t("Time")}: {new Date(feedback.timestamp).toLocaleString()}</p>
          </li>
        ))}
      </ul>
      <button onClick={() => navigate("/events")}>{t("Back to Events")}</button>
    </div>
  );
};

export default FeedbackList;
