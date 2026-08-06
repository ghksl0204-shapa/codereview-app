import axios from 'axios';
import { getAuthStorage, setAuthStorage, clearAuthStorage } from '../utils/authStorage';

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

const Axios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: localStorage의 accessToken을 헤더에 부착
Axios.interceptors.request.use((config) => {
  const auth = getAuthStorage();
  if (auth?.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`;
  }
  return config;
});

// 401 재발급(reissue) 동시 요청 방지용 상태
let isReissuing = false;
let pendingQueue = [];

function resolveQueue(error, accessToken) {
  pendingQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else resolve(accessToken);
  });
  pendingQueue = [];
}

// 응답 인터셉터: ApiResponse 객체를 그대로 반환한다(언랩하지 않음).
// 컴포넌트는 res.data.data / res.data.code / res.data.message로 접근한다.
Axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const { config, response } = error;

    // 서버가 응답조차 못한 경우(네트워크 오류 등)는 그대로 반환
    if (!response) return Promise.reject(error);

    const isAuthEndpoint = config.url?.includes('/api/auth/');
    const isReissueEndpoint = config.url?.includes('/api/auth/reissue');

    if (response.status !== 401 || isReissueEndpoint || config._retry) {
      return Promise.reject(error);
    }

    const auth = getAuthStorage();
    if (!auth?.refreshToken || isAuthEndpoint) {
      clearAuthStorage();
      return Promise.reject(error);
    }

    config._retry = true;

    if (isReissuing) {
      // 이미 재발급 진행 중이면 큐에 대기했다가 새 토큰으로 재요청
      return new Promise((resolve, reject) => {
        pendingQueue.push({ resolve, reject });
      }).then((accessToken) => {
        config.headers.Authorization = `Bearer ${accessToken}`;
        return Axios(config);
      });
    }

    isReissuing = true;
    try {
      const { data } = await Axios.post('/api/auth/reissue', {
        refreshToken: auth.refreshToken,
      });
      const { accessToken, refreshToken } = data.data;

      setAuthStorage({ ...auth, accessToken, refreshToken });
      resolveQueue(null, accessToken);

      config.headers.Authorization = `Bearer ${accessToken}`;
      return Axios(config);
    } catch (reissueError) {
      resolveQueue(reissueError, null);
      clearAuthStorage();
      window.location.href = '/login';
      return Promise.reject(reissueError);
    } finally {
      isReissuing = false;
    }
  },
);

export default Axios;
