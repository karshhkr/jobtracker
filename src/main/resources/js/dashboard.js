requireAuth();

const STATUSES = ['WISHLIST', 'APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'];
const FIELDS = ['company', 'roleTitle', 'location', 'jobUrl', 'status', 'appliedDate', 'followUpDate', 'notes'];
let jobs = [];
const $ = id => document.getElementById(id);
const dlg = $('jobDialog'), jobForm = $('jobForm');

async function load() {
  try {
    const [j, stats, sub, rem] = await Promise.all([
      api('/api/jobs'), api('/api/jobs/stats'), api('/api/subscriptions/status'), api('/api/reminders')]);
    jobs = j;
    renderStats(stats); renderPlan(sub); renderReminders(rem); renderTable();
  } catch (e) { toast(e.message); }
}

function renderStats(s) {
  $('stats').innerHTML = STATUSES.map(k =>
    `<div class="card stat"><b>${s[k] ?? 0}</b><span>${k.toLowerCase()}</span></div>`).join('');
}

function renderPlan(sub) {
  const pct = sub.pro ? 100 : Math.min(100, Math.round(sub.used / sub.limit * 100));
  $('plan').innerHTML = sub.pro
    ? `<b>Pro plan</b> <span class="muted">until ${esc(sub.expiresOn)}</span>
       <div class="bar"><i style="width:100%"></i></div>
       <span class="muted">${sub.used} applications tracked. Unlimited, with email reminders.</span>`
    : `<b>Free plan</b><div class="bar"><i style="width:${pct}%"></i></div>
       <span class="muted">${sub.used} of ${sub.limit} applications used.</span>
       <div style="margin-top:10px"><a class="btn sm" href="/pricing">Upgrade to Pro</a></div>`;
}

function renderReminders(list) {
  $('reminders').innerHTML = list.length
    ? list.map(r => `<div class="reminder">
        <span><b>${esc(r.company)}</b> - ${esc(r.roleTitle)}<br>
        <span class="muted">${esc(r.message)} (due ${esc(r.remindAt)})</span></span>
        <button class="btn ghost sm" data-done="${esc(r.id)}">Done</button></div>`).join('')
    : '<div class="muted">Nothing due. You are on top of it.</div>';
}

function renderTable() {
  const q = $('search').value.toLowerCase(), f = $('filter').value;
  const rows = jobs.filter(j => (!f || j.status === f) &&
    (!q || (j.company + ' ' + j.roleTitle + ' ' + (j.location || '')).toLowerCase().includes(q)));
  $('tbody').innerHTML = rows.map(j => `<tr>
    <td><b>${esc(j.company)}</b>${j.jobUrl ? ` <a href="${esc(j.jobUrl)}" target="_blank" rel="noopener noreferrer">link</a>` : ''}</td>
    <td>${esc(j.roleTitle)}<br><span class="muted">${esc(j.location || '')}</span></td>
    <td><select data-status="${esc(j.id)}" class="badge ${j.status}" style="width:auto">
      ${STATUSES.map(s => `<option ${s === j.status ? 'selected' : ''}>${s}</option>`).join('')}</select></td>
    <td>${esc(j.appliedDate || '-')}</td><td>${esc(j.followUpDate || '-')}</td>
    <td style="white-space:nowrap">
      <button class="btn ghost sm" data-edit="${esc(j.id)}">Edit</button>
      <button class="btn danger sm" data-del="${esc(j.id)}">Delete</button></td></tr>`).join('');
  $('empty').style.display = rows.length ? 'none' : 'block';
  $('empty').textContent = jobs.length
    ? 'No applications match your filter.'
    : 'No applications yet. Click "Add application" to start tracking.';
}

function openDialog(job) {
  jobForm.reset();
  $('dlgTitle').textContent = job ? 'Edit application' : 'Add application';
  jobForm.elements['jobId'].value = job?.id ?? '';
  if (job) {
    FIELDS.forEach(k => { jobForm.elements[k].value = job[k] ?? ''; });
  } else {
    jobForm.elements['status'].value = 'APPLIED';
    jobForm.elements['appliedDate'].value = new Date().toISOString().slice(0, 10);
  }
  $('formErr').style.display = 'none';
  dlg.showModal();
}

function payload(j, overrides = {}) {
  return {
    company: j.company, roleTitle: j.roleTitle, location: j.location, jobUrl: j.jobUrl, status: j.status,
    appliedDate: j.appliedDate || null, followUpDate: j.followUpDate || null, notes: j.notes, ...overrides
  };
}

jobForm.addEventListener('submit', async e => {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(jobForm));
  const id = d.jobId; delete d.jobId;
  d.appliedDate = d.appliedDate || null;
  d.followUpDate = d.followUpDate || null;
  try {
    await api(id ? '/api/jobs/' + id : '/api/jobs', { method: id ? 'PUT' : 'POST', body: d });
    dlg.close(); toast('Saved'); load();
  } catch (err) {
    const box = $('formErr');
    box.innerHTML = esc(err.message) + (err.status === 402 ? ' <a href="/pricing">See plans</a>' : '');
    box.style.display = 'block';
  }
});

$('addBtn').addEventListener('click', () => openDialog());
$('cancelBtn').addEventListener('click', () => dlg.close());
$('search').addEventListener('input', renderTable);
$('filter').addEventListener('change', renderTable);

$('tbody').addEventListener('click', async e => {
  const edit = e.target.dataset.edit, del = e.target.dataset.del;
  if (edit) openDialog(jobs.find(j => j.id === edit));
  if (del && confirm('Delete this application?')) {
    try { await api('/api/jobs/' + del, { method: 'DELETE' }); toast('Deleted'); load(); }
    catch (err) { toast(err.message); }
  }
});

$('tbody').addEventListener('change', async e => {
  const id = e.target.dataset.status;
  if (!id) return;
  const job = jobs.find(j => j.id === id);
  try {
    await api('/api/jobs/' + id, { method: 'PUT', body: payload(job, { status: e.target.value }) });
    toast('Status updated'); load();
  } catch (err) { toast(err.message); load(); }
});

$('reminders').addEventListener('click', async e => {
  const id = e.target.dataset.done;
  if (!id) return;
  try { await api(`/api/reminders/${id}/done`, { method: 'POST' }); load(); }
  catch (err) { toast(err.message); }
});

load();