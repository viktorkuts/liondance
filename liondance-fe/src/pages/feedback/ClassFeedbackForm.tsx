import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useForm } from "@mantine/form";
import { Textarea, NumberInput, Button, Notification } from "@mantine/core";
import useClassFeedbackService from "@/services/classFeedbackService";
import { ClassFeedbackRequestModel } from "@/models/ClassFeedback";
import "./ClassFeedbackForm.css";
import { useTranslation } from "react-i18next";
const ClassFeedbackForm: React.FC  = () => {
  const { date } = useParams<{ date: string }>();
  const navigate = useNavigate();
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const feedbackService = useClassFeedbackService();
  const { t } = useTranslation;
  const form = useForm({
    initialValues: {
      classDate: date ?? "", 
      score: 0,
      comment: "",
    },

    validate: {
      score: (value) => (value >= 1 && value <= 5 ? null : t("Score must be between 1 and 5")),
    },
  });

  const handleSubmit = async (values: ClassFeedbackRequestModel) => {
    try {
      await feedbackService.addFeedback(values);
      setSuccess("Feedback submitted successfully!");
      setError(null);
      setTimeout(() => navigate("/"), 3000); 
    } catch (err) {
      setError("Failed to submit feedback. Please try again. "+err);
      setSuccess(null);
    }
  };

  return (
    <div className="feedback-form-container">
      <h1>Class Feedback Form</h1>

      {success && <Notification color="teal">{success}</Notification>}
      {error && <Notification color="red">{error}</Notification>}

      <form onSubmit={form.onSubmit(handleSubmit)}>
        <NumberInput
          label={t("Score (1-5)")}
          required
          min={1}
          max={5}
          {...form.getInputProps("score")}
        />

        <Textarea
          label={t("Comments (Optional)")}
          placeholder={t("Share your thoughts about the class")}
          {...form.getInputProps("comment")}
        />

        <Button type="submit" mt="md">
        {t("Submit Feedback")}
        </Button>
      </form>
    </div>
  );
};

export default ClassFeedbackForm;

