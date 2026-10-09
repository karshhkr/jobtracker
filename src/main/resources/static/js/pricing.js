requireAuth();

const btn = document.getElementById('upgradeBtn');
const note = document.getElementById('proNote');

async function refresh() {
  const s = await api('/api/subscriptions/status');
  const price = 'Rs ' + (s.pricePaise / 100) + ' / month';
  const priceEl = document.getElementById('price');
  const tag = document.getElementById('betaTag');

  if (s.betaFree) {
    priceEl.innerHTML = '<s class="muted" style="font-size:1.1rem">' + esc(price) + '</s> Free';
    tag.textContent = 'Free during beta. Pricing may change later.';
    btn.textContent = s.pro ? 'You have Pro' : 'Get Pro free (beta)';
    btn.disabled = s.pro;
  } else {
    priceEl.textContent = price;
    btn.textContent = s.pro ? 'Extend Pro by 30 days' : 'Upgrade to Pro';
  }
  if (s.pro) note.textContent = 'You are Pro until ' + s.expiresOn + '.';
}

function loadRazorpay() {
  return new Promise((resolve, reject) => {
    if (window.Razorpay) return resolve();
    const s = document.createElement('script');
    s.src = 'https://checkout.razorpay.com/v1/checkout.js';
    s.onload = resolve;
    s.onerror = () => reject(new Error('Could not load payment widget'));
    document.head.appendChild(s);
  });
}

btn.addEventListener('click', async () => {
  btn.disabled = true;
  try {
    const o = await api('/api/subscriptions/order', { method: 'POST' });

    if (o.mock) {
      await api('/api/subscriptions/verify', {
        method: 'POST',
        body: { razorpay_order_id: o.orderId, razorpay_payment_id: 'mock', razorpay_signature: 'mock' }
      });
      toast('Pro activated (free beta). Enjoy!');
      setTimeout(() => location.href = '/dashboard', 1200);
      return;
    }

    await loadRazorpay();

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