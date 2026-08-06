import Axios from './Axios';

export const ratingApi = {
  create: (commentId, payload) =>
    Axios.post(`/api/comments/${commentId}/rating`, payload),
  getList: (commentId) => Axios.get(`/api/comments/${commentId}/rating`),
};
