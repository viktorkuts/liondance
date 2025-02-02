import React from 'react';
import { useTranslation } from 'react-i18next';
import './LanguageSwitcher.css';

const LanguageSwitcher: React.FC = () => {
  const { i18n } = useTranslation();

  const toggleLanguage = () => {
    const newLanguage = i18n.language === 'en' ? 'fr' : 'en';
    i18n.changeLanguage(newLanguage);
  };

  return (
    <div className="language-switcher">
      <button onClick={toggleLanguage}>
        {i18n.language === 'en' ? 'Fr' : 'En'}
      </button>
    </div>
  );
};

export default LanguageSwitcher;