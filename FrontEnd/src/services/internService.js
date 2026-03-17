import api from './api';

export const internService = {
    async getAllInterns(params) {
        const response = await api.get('/interns', { params });
        return response.data;
    },
    async getInternById(id) {
        const response = await api.get(`/interns/${id}`);
        return response.data;
    },
    async createIntern(data) {
        const response = await api.post('/interns', data);
        return response.data;
    },
    async updateIntern(id, data) {
        const response = await api.put(`/interns/${id}`, data);
        return response.data;
    },
    async deleteIntern(id) {
        const response = await api.delete(`/interns/${id}`);
        return response.data;
    }
};

export default internService;
