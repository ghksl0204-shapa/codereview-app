import Axios from './Axios';

export const memberApi = {
  join: (payload) => Axios.post('/api/members', payload),
  updateNickname: (nickname) => Axios.patch('/api/members', { nickname }),
  updatePassword: (currentPassword, newPassword) =>
    Axios.patch('/api/members/password', { currentPassword, newPassword }),
  withdraw: (currentPassword) =>
    Axios.delete('/api/members', { data: { currentPassword } }),
};
