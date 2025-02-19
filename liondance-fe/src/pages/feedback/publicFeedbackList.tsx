import React, { useEffect, useState } from "react";
import { useFeedbackService } from "../../services/feedbackService";
import { Feedback } from "../../models/Feedback";
import "./feedbackList.css";
import { useTranslation } from "react-i18next";
import StarRating from "@/components/rating";

const PublicFeedbackList: React.FC = () => {
  const { t } = useTranslation();
  const feedbackService = useFeedbackService();
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchFeedbacks = async () => {
    try {
      const data = await feedbackService.getPublicFeedbacks();
      setFeedbacks(data);
    } catch {
      setError(t("Failed to fetch feedback"));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setLoading(false);
    fetchFeedbacks();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) {
    return <div>{t("Loading...")}</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="feedback-list">
      <h2>{t("Reviews")}</h2>
      <ul>
        {feedbacks.length == 0 && (
          <h2>{t("no-public-reviews-at-the-moment")}</h2>
        )}
        {feedbacks.map((feedback) => (
          <li key={feedback.feedbackId}>
            <p>
              {t("Comment")}: {feedback.feedback}
            </p>
            <p>
              {t("Rating")}:{" "}
              <StarRating currentRating={feedback.rating} viewOnly={true} />
            </p>
            <p>
              {t("Time")}: {new Date(feedback.timestamp).toLocaleString()}
            </p>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default PublicFeedbackList;
