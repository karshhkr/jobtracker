if (Auth.token) location.href = '/dashboard';

const form = document.getElementById('authForm');
const errBox = document.getElementById('err');

form.addEventListener('submit', async e => {
  e.preventDefault();
  errBox.style.display = 'none';
  const btn = form.querySelector('button');
  btn.disabled = true;
  try {
    const data = Object.fromEntries(new FormData(form));
    const r = await api('/api/auth/' + form.dataset.endpoint, { method: 'POST', body: data });
    Auth.set(r.token, r.name);
    location.href = '/dashboard';
  } catch (err) {
    errBox.textContent = err.message;
    errBox.style.display = 'block';
    btn.disabled = false;
  }
});