const Auth = {
  get token() { return localStorage.getItem('jt_token'); },
  get name() { return localStorage.getItem('jt_name') || ''; },
  set(token, name) { localStorage.setItem('jt_token', token); localStorage.setItem('jt_name', name); },
  clear() { localStorage.removeItem('jt_token'); localStorage.removeItem('jt_name'); }
};

async function api(path, { method = 'GET', body } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (Auth.token) headers.Authorization = 'Bearer ' + Auth.token;
  const res = await fetch(path, { method, headers, body: body ? JSON.stringify(body) : undefined });
  if (res.status === 401 && !path.startsWith('/api/auth')) {
    Auth.clear();
    location.href = '/login';
    throw new Error('Session expired');
  }
  const data = res.status === 204 ? null : await res.json().catch(() => null);
  if (!res.ok) {
    const err = new Error((data && data.error) || 'Request failed');
    err.status = res.status;
    throw err;
  }
  return data;
}

function esc(s) {
  return String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function requireAuth() { if (!Auth.token) location.href = '/login'; }
function logout() { Auth.clear(); location.href = '/login'; }

function toast(msg) {
  let t = document.getElementById('toast');
  if (!t) { t = document.createElement('div'); t.id = 'toast'; document.body.appendChild(t); }
  t.textContent = msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 2800);
}

function currentTheme() {
  return document.documentElement.getAttribute('data-theme') ||
    (matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
}

function applyThemeIcon() {
  const b = document.getElementById('themeBtn');
  if (b) b.textContent = currentTheme() === 'dark' ? '\u2600' : '\u263E';
}

document.addEventListener('DOMContentLoaded', () => {
  const u = document.getElementById('navUser');
  if (u) u.textContent = Auth.name;
  const l = document.getElementById('logoutBtn');
  if (l) l.addEventListener('click', logout);

  const tb = document.getElementById('themeBtn');
  if (tb) {
    applyThemeIcon();
    tb.addEventListener('click', () => {
      const next = currentTheme() === 'dark' ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', next);
      localStorage.setItem('jt_theme', next);
      applyThemeIcon();
    });
  }
});