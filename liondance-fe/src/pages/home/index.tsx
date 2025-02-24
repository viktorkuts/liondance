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
            <h1>{t('Montreal Youth LVH Lion Dance Wushu Team')}</h1>
            <p>
              {t('The Montreal Youth LVH Lion Dance Wushu Team is a non-profit organization dedicated to promoting Chinese martial arts and cultural heritage through Wushu, Hung Gar Kung Fu, and Lion Dance performances. With over 20 years of experience and more than 300 events, we offer dynamic performances and training opportunities. Our platform makes it easy to manage class enrollments, performance bookings, and client communication. Interested in training with us or booking a performance? Check out our upcoming events to see us in action, or visit the booking page to schedule a performance directly!')}
            </p>
              <br />
              <h3>{t('Where can you find us?')}</h3>
              <p>
              📍 945 Chemin de Chambly, Longueuil, QC, Canada
              <br />
              📧 terry.chan@myliondance.com
              </p>
            </div>
          <img src={lionImage} alt="Lion" className={classes.lionImage} />
        </div>
      </div>
    </div>
  );
}

export default Home;
