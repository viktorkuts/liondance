import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useFeedbackService } from "../../services/feedbackService";
import { useUserContext } from "../../utils/userProvider"; 
import { useTranslation } from "react-i18next";
import classes from "./feedback.module.css"; 

interface FeedbackFormProps {
  onSuccess?: () => void;
}

const FeedbackForm: React.FC<FeedbackFormProps> = ({ onSuccess }) => {
  const { eventId } = useParams<{ eventId: string }>(); 
  const { submitFeedback } = useFeedbackService();
  const { user, isLoading } = useUserContext(); 
  const { t } = useTranslation();
  const navigate = useNavigate(); 
  const [rating, setRating] = useState<number>(0);
  const [comment, setComment] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!isLoading && !user) {
      navigate("/");
    }
  }, [user, isLoading, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    try {
      if (eventId) {
        await submitFeedback(eventId, rating, comment);
        setComment("");
        setRating(0);
        setSuccessMessage(t("Feedback submitted successfully!"));
        if (onSuccess) onSuccess();
      } else {
        setError(t("Event ID is missing."));
      }
    } catch {
      setError(t("Failed to submit feedback. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  if (isLoading) {
    return <div>{t("Loading...")}</div>;
  }

  return (
    <div className={classes.formWrapper}>
      <form onSubmit={handleSubmit} className={classes.feedbackForm}>
        {successMessage && <p className={classes.success}>{successMessage}</p>} 
  
        <label>
          {t("Rating (1-5)")}:
          <input
            type="number"
            min="1"
            max="5"
            value={rating}
            onChange={(e) => setRating(Number(e.target.value))}
            required
          />
        </label>
  
        <label>
          {t("Comment")}:
          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            required
          />
        </label>
  
        {error && <p className={classes.error}>{error}</p>}
  
        <button type="submit" disabled={loading}>
          {loading ? t("Submitting...") : t("Submit Feedback")}
        </button>
      </form>
      <button onClick={() => navigate("/my-events")} type="button" className={`${classes.backButton} ${classes.smallButton}`}>
        {t("Back to My Events")}
      </button>
    </div>
  );
};

export default FeedbackForm;