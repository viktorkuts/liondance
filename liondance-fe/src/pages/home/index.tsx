import classes from "./home.module.css";
import lionImage from "../../assets/lions.jpg";
import { useTranslation } from "react-i18next";

function Home() {
  const { t } = useTranslation();

  return (
    <div>
      <div className={classes.welcomeHero}>
        <div className={classes.content}>
          <div className={classes.textContent}>
            <h1>{t('Montrel Youth LVH Lion Dance Wushu Team')}</h1>
            <p>
              {t('The Montreal Youth LVH Lion Dance Wushu Team is a non-profit team that promotes martial arts (Wushu and Hung Gar Kung Fu) and Lion Dance. We have over 20 years and 300 events experience! Contact us if you want to train with us or book us for any event.')}
            </p>
          </div>
          <img src={lionImage} alt="Lion" className={classes.lionImage} />
        </div>
      </div>
    </div>
  );
}

export default Home;
