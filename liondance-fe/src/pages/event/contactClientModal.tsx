import React, { useState, useRef, useEffect } from 'react';
import { useAxiosInstance } from "../../utils/axiosInstance";
import { useTranslation } from "react-i18next";
import "./contactClientModal.css";

interface ContactClientModalProps {
  clientEmail: string;
  clientName: string;
  onClose: () => void;
}

const ContactClientModal: React.FC<ContactClientModalProps> = ({
  clientEmail,
  clientName,
  onClose,
}) => {
  const { t } = useTranslation();
  const [message, setMessage] = useState("");
  const [footerText, setFooterText] = useState(
    t("\n\nBest regards,\nLVH Lion Dance Team\nWebsite: www.lvhliondance.com\nEmail: info@lvhliondance.com\nPhone: (123) 456-7890")
  );  const [includeFooter, setIncludeFooter] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const [isEditingFooter, setIsEditingFooter] = useState(false);
  const axiosInstance = useAxiosInstance();
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const getDisplayMessage = () => {
    return includeFooter ? `${message}${footerText}` : message;
  };

  const adjustTextareaHeight = () => {
    const textarea = textareaRef.current;
    if (textarea) {
      textarea.style.height = 'auto';
      textarea.style.height = `${textarea.scrollHeight}px`;
    }
  };

  useEffect(() => {
    adjustTextareaHeight();
  }, [message, footerText, includeFooter]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSending(true);
    setError("");

    try {
      await axiosInstance.post("/notifications/contact-client", {
        email: clientEmail,
        message: getDisplayMessage()
      });
      onClose();
    } catch {
      setError(t("Failed to send message. Please try again."));
    } finally {
      setSending(false);
    }
  };

  const handleMessageChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const newValue = e.target.value;
    if (includeFooter) {
      const footerIndex = newValue.lastIndexOf(footerText);
      if (footerIndex !== -1) {
        if (isEditingFooter) {
          setFooterText(newValue.substring(footerIndex));
        } else {
          setMessage(newValue.substring(0, footerIndex));
        }
      } else {
        if (isEditingFooter) {
          setFooterText(newValue.substring(message.length));
        } else {
          setMessage(newValue);
        }
      }
    } else {
      setMessage(newValue);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (includeFooter) {
      const cursorPosition = e.currentTarget.selectionStart;
      const footerPosition = getDisplayMessage().lastIndexOf(footerText);
      
      setIsEditingFooter(cursorPosition > footerPosition);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>{t("Contact")} {clientName}</h2>
        <form onSubmit={handleSubmit}>
          <textarea
            ref={textareaRef}
            value={getDisplayMessage()}
            onChange={handleMessageChange}
            onKeyDown={handleKeyDown}
            placeholder={t("Enter your message here...")}
            required
          />
          <div className="footer-option">
            <label>
              <input
                type="checkbox"
                checked={includeFooter}
                onChange={(e) => setIncludeFooter(e.target.checked)}
              />
              {t("Include contact information footer")}
            </label>
          </div>
          {error && <p className="error-message">{error}</p>}
          <div className="modal-buttons">
            <button type="button" onClick={onClose} disabled={sending}>
              {t("Cancel")}
            </button>
            <button type="submit" disabled={sending}>
              {sending ? t("Sending...") : t("Send")}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ContactClientModal; 