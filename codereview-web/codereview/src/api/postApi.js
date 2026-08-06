import Axios from './Axios';

export const postApi = {
  create: (payload) => Axios.post('/api/posts', payload),
  getList: ({ offset = 0, limit = 10, category, language, keyword } = {}) =>
    Axios.get('/api/posts', {
      params: { offset, limit, category, language, keyword },
    }),
  getDetail: (postId) => Axios.get(`/api/posts/${postId}`),
  update: (postId, payload) => Axios.patch(`/api/posts/${postId}`, payload),
  remove: (postId) => Axios.delete(`/api/posts/${postId}`),
};
