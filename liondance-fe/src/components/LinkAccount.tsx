import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useUserService } from '../services/userService';
import styles from './LinkAccount.module.css';

export const LinkAccount = () => {
    const { loginWithRedirect, isAuthenticated } = useAuth0();
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const userId = searchParams.get('userId');
    const userService = useUserService();

    useEffect(() => {
        const linkAccount = async () => {
            if (!userId) {
                setError('Invalid link. Please contact support.');
                return;
            }

            if (isAuthenticated) {
                try {
                    await userService.linkUserAccount(userId);
                    navigate('/dashboard', { 
                        replace: true,
                        state: { message: 'Account successfully linked! You can now access Lion Dance.' }
                    });
                } catch (err: Error | unknown) {
                    setError((err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Failed to link account. Please try again.');
                }
            } else {
                loginWithRedirect({
                    appState: { returnTo: `/link-account?userId=${userId}` },
                    authorizationParams: { connection: 'google-oauth2' }
                });
            }
        };

        linkAccount();
    }, [userId, isAuthenticated, loginWithRedirect, navigate, userService]);

    if (error) {
        return (
            <div className={styles.container}>
                <div className={styles.card}>
                    <h2 className={styles.title}>Error</h2>
                    <p className={styles.message}>{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <h2 className={styles.title}>Linking your account...</h2>
                <p className={styles.message}>Please wait while we complete the process.</p>
            </div>
        </div>
    );
}; 