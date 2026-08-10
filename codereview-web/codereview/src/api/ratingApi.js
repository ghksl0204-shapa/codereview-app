import Axios from './Axios';

export const ratingApi = {
  create: (commentId, payload) =>
    Axios.post(`/api/comments/${commentId}/rating`, payload),
  update: (commentId, payload) =>
    Axios.patch(`/api/comments/${commentId}/rating`, payload),
};
