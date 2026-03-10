import { useState, useEffect } from 'react'
import { getAllOrders, updateOrderStatus } from '../../api'

const STATUSES = ['EN_COURS', 'VALIDEE', 'EXPEDIEE', 'LIVREE', 'ANNULEE']

export default function AdminOrders({ token, addToast }) {
  const [orders, setOrders]   = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    getAllOrders(token).then(setOrders).finally(() => setLoading(false))
  }

  useEffect(load, [token])

  const changeStatus = async (id, status) => {
    await updateOrderStatus(id, status, token)
    addToast('Statut mis à jour')
    load()
  }

  return (
    <div>
      <div className="section-header">
        <div>
          <h2 className="section-title">Commandes</h2>
          <div className="divider" />
        </div>
        <div style={{ color: 'var(--muted)', fontSize: 13 }}>
          {orders.length} commande{orders.length !== 1 ? 's' : ''}
        </div>
      </div>

      {loading ? <div className="spinner" /> : (
        <div className="card">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th><th>Client</th><th>Total</th><th>Date</th><th>Statut</th><th>Changer statut</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(o => (
                <tr key={o.id}>
                  <td style={{ color: 'var(--muted)' }}>#{o.id}</td>
                  <td>{o.customerEmail}</td>
                  <td style={{ fontFamily: "'Cormorant Garamond', serif", fontSize: 16, color: 'var(--red)' }}>
                    {Number(o.totalAmount).toFixed(2)} €
                  </td>
                  <td style={{ color: 'var(--muted)', fontSize: 12 }}>
                    {new Date(o.createdAt).toLocaleDateString('fr-FR')}
                  </td>
                  <td>
                    <span className={`status-badge status-${o.status}`}>{o.status}</span>
                  </td>
                  <td>
                    <select
                      className="input"
                      style={{ width: 'auto', padding: '6px 10px', fontSize: 12 }}
                      value={o.status}
                      onChange={e => changeStatus(o.id, e.target.value)}
                    >
                      {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
