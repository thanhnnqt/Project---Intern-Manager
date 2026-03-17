import api from './api';

export const commentService = {
    async getCommentsByTaskId(taskId) {
        const response = await api.get('/comments', { params: { taskId } });
        return response.data;
    },
    async addComment(data) {
        const response = await api.post('/comments', data);
        return response.data;
    },
    async deleteComment(id) {
        const response = await api.delete(`/comments/${id}`);
        return response.data;
    }
};
