requireAuth();

const btn = document.getElementById('upgradeBtn');
const note = document.getElementById('proNote');

async function refresh() {
  const s = await api('/api/subscriptions/status');
  document.getElementById('price').textContent = 'Rs ' + (s.pricePaise / 100) + ' / month';
  if (s.pro) {
    btn.textContent = 'Extend Pro by 30 days';
    note.textContent = 'You are Pro until ' + s.expiresOn + '.';
  }
}

btn.addEventListener('click', async () => {
  btn.disabled = true;
  try {
    const o = await api('/api/subscriptions/order', { method: 'POST' });
    const rzp = new Razorpay({
      key: o.keyId, amount: o.amount, currency: o.currency, order_id: o.orderId,
      name: 'JobTrackr Pro', description: '30 days of Pro',
      prefill: { name: o.name, email: o.email },
      theme: { color: '#4f46e5' },
      handler: async resp => {
        try {
          await api('/api/subscriptions/verify', { method: 'POST', body: resp });
          toast('Payment verified. Welcome to Pro!');
          setTimeout(() => location.href = '/dashboard', 1200);
        } catch (e) { toast(e.message); btn.disabled = false; }
      },
      modal: { ondismiss: () => { btn.disabled = false; } }
    });
    rzp.open();
  } catch (e) { toast(e.message); btn.disabled = false; }
});

refresh().catch(e => toast(e.message));