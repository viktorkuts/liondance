import { Button, Container, Group, Text, Title } from "@mantine/core";
import { Illustration } from "./Illustration";
import classes from "./index.module.css";
import { useTranslation } from "react-i18next";

export function NotFound() {
  const { t } = useTranslation();

  return (
    <Container className={classes.root}>
      <div className={classes.inner}>
        <Illustration className={classes.image} />
        <div className={classes.content}>
          <Title className={classes.title}>{t('Nothing to see here')}</Title>
          <Text size="lg" ta="center" className={classes.description}>
            {t('Page you are trying to open does not exist. You may have mistyped the address, or the page has been moved to another URL. If you think this is an error contact support.')}
          </Text>
          <Group justify="center">
            <Button size="md" component="a" href="/">
              {t('Take me back to home page')}
            </Button>
          </Group>
        </div>
      </div>
    </Container>
  );
}
