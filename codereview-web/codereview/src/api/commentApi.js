import Axios from './Axios';

export const commentApi = {
  create: ({ postId, content, parentCommentId }) =>
    Axios.post('/api/comments', { postId, content, parentCommentId }),
  getList: ({ postId, offset = 0, limit = 50 }) =>
    Axios.get('/api/comments', { params: { postId, offset, limit } }),
  update: (commentId, content) =>
    Axios.patch(`/api/comments/${commentId}`, { content }),
  remove: (commentId) => Axios.delete(`/api/comments/${commentId}`),
};
