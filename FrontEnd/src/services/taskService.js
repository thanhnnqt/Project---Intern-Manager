import api from './api';

export const taskService = {
    async getAllTasks(params) {
        const response = await api.get('/tasks', { params });
        return response.data;
    },
    async getTaskById(id) {
        const response = await api.get(`/tasks/${id}`);
        return response.data;
    },
    async createTask(data) {
        const response = await api.post('/tasks', data);
        return response.data;
    },
    async updateTask(id, data) {
        const response = await api.put(`/tasks/${id}`, data);
        return response.data;
    },
    async deleteTask(id) {
        const response = await api.delete(`/tasks/${id}`);
        return response.data;
    }
};
