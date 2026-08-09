import Axios from './Axios';

export const aiReviewApi = {
  regenerate: (postId) => Axios.post(`/api/posts/${postId}/ai-review/regenerate`),
};
