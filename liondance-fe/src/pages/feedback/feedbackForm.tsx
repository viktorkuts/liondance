import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom"; // Import useParams and useNavigate
import { useFeedbackService } from "../../services/feedbackService";
import { useUserContext } from "../../utils/userProvider"; // Import useUserContext
import classes from "./feedback.module.css"; // Import CSS module

interface FeedbackFormProps {
  onSuccess?: () => void;
}

const FeedbackForm: React.FC<FeedbackFormProps> = ({ onSuccess }) => {
  const { eventId } = useParams<{ eventId: string }>(); // Extract eventId from URL
  const { submitFeedback } = useFeedbackService();
  const { user, isLoading } = useUserContext(); // Use the user context
  const navigate = useNavigate(); // Initialize navigate function
  const [rating, setRating] = useState<number>(0);
  const [comment, setComment] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isLoading && !user) {
      navigate("/login"); // Redirect to login if user is not authenticated
    }
  }, [user, isLoading, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (eventId) {
        await submitFeedback(eventId, rating, comment);
        setComment("");
        setRating(0);
        if (onSuccess) onSuccess();
      } else {
        setError("Event ID is missing.");
      }
    } catch {
      setError("Failed to submit feedback. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  if (isLoading) {
    return <div>Loading...</div>; // Show loading state while checking authentication
  }

  return (
    <form onSubmit={handleSubmit} className={classes.feedbackForm}>
      <label>
        Rating (1-5):
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
        Comment:
        <textarea
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          required
        />
      </label>

      {error && <p className={classes.error}>{error}</p>}

      <button type="submit" disabled={loading}>
        {loading ? "Submitting..." : "Submit Feedback"}
      </button>
    </form>
  );
};

export default FeedbackForm;