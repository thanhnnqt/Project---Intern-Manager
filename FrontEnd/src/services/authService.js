import api from './api';

export const authService = {
    async login(email, password) {
        // Backend LoginRequest uses 'username' field - we send email as the username
        const response = await api.post('/auth/login', { username: email, password });
        return response.data;
    },
    async googleLogin(idToken) {
        const response = await api.post('/auth/google-login', { idToken });
        return response.data;
    }
};

export default authService;
