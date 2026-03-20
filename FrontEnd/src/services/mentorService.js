import api from './api';

export const mentorService = {
    async getAllMentors(params) {
        const response = await api.get('/mentors', { params });
        return response.data;
    },
    async getMentorById(id) {
        const response = await api.get(`/mentors/${id}`);
        return response.data;
    },
    async createMentor(data) {
        const response = await api.post('/mentors', data);
        return response.data;
    },
    async updateMentor(id, data) {
        const response = await api.put(`/mentors/${id}`, data);
        return response.data;
    },
    async deleteMentor(id) {
        const response = await api.delete(`/mentors/${id}`);
        return response.data;
    }
};

export default mentorService;
