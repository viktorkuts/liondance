import React, { useEffect, useState } from "react";
import feedbackService from "../../services/feedbackService";
import { Feedback } from "../../models/Feedback";
import './feedbackList.css';
import { useParams, useNavigate } from "react-router-dom";

interface FeedbackListProps {
  eventId: string;
}

const FeedbackList: React.FC<FeedbackListProps> = () => {
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
          setError("Event ID is not defined");
        }
      } catch {
        setError("Failed to fetch feedback");
      } finally {
        setLoading(false);
      }
    };

    fetchFeedbacks();
  }, [eventId]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="feedback-list">
      <h2>Feedback for Event</h2>
      <ul>
        {feedbacks.map((feedback) => (
          <li key={feedback.feedbackId}>
            <p>Comment: {feedback.feedback}</p>
            <p>Rating: {feedback.rating}</p>
            <p>Time: {new Date(feedback.timestamp).toLocaleString()}</p>
          </li>
        ))}
      </ul>
      <button onClick={() => navigate("/events")}>Back to Events</button>
    </div>
  );
};

export default FeedbackList;