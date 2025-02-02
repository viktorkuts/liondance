import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import HttpApi from 'i18next-http-backend';

i18n
  .use(HttpApi)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: 'en',
    debug: true,
    interpolation: {
      escapeValue: false,
    },
    backend: {
      loadPath: '/locales/{{lng}}/{{ns}}.json',
    },
    ns: ['translation', 'calendar'],
    defaultNS: 'translation',
  });

export default i18n;
// filepath: /c:/Users/artem/Documents/GitHub/liondance/liondance-fe/src/i18n.js
// import i18n from 'i18next';
// import { initReactI18next } from 'react-i18next';
// import LanguageDetector from 'i18next-browser-languagedetector';
// import HttpApi from 'i18next-http-backend';

// const subscriptionKey = 'B5gbg1EQYXU2n9mAt0sXoGaCe84eZZsn3JxP1EGhhQMolgLT0IsaJQQJ99BBACREanaXJ3w3AAAbACOGNAFd';
// const region = 'canadaeast';

// i18n
//   .use(HttpApi)
//   .use(LanguageDetector)
//   .use(initReactI18next)
//   .init({
//     fallbackLng: 'en',
//     debug: true,
//     interpolation: {
//       escapeValue: false,
//     },
//     backend: {
//       loadPath: 'https://api.cognitive.microsofttranslator.com',
//       requestOptions: {
//         method: 'POST',
//         headers: {
//           'Ocp-Apim-Subscription-Key': subscriptionKey,
//           'Ocp-Apim-Subscription-Region': region,
//           'Content-Type': 'application/json',
//         },
//         body: (options) => JSON.stringify([{ Text: options.key }]),
//       },
//       parse: (data) => {
//         const translations = JSON.parse(data);
//         return translations[0].translations.reduce((acc, translation) => {
//           acc[translation.text] = translation.text;
//           return acc;
//         }, {});
//       },
//     },
//     ns: ['translation', 'calendar'],
//     defaultNS: 'translation',
//   });

// export default i18n;