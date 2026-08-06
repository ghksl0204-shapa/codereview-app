const AUTH_KEY = 'codereview_auth';

// { accessToken, refreshToken, id, nickname, role }
export function getAuthStorage() {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function setAuthStorage(auth) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

export function clearAuthStorage() {
  localStorage.removeItem(AUTH_KEY);
}
