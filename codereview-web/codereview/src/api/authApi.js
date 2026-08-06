import Axios from './Axios';

export const authApi = {
  login: (id, password) => Axios.post('/api/auth/login', { id, password }),
  logout: (refreshToken) => Axios.post('/api/auth/logout', { refreshToken }),
  reissue: (refreshToken) => Axios.post('/api/auth/reissue', { refreshToken }),
};
