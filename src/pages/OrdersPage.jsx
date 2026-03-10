import { useState, useEffect } from 'react'
import { getMyOrders } from '../api'
import './OrdersPage.css'

export default function OrdersPage({ token }) {
  const [orders, setOrders]   = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMyOrders(token).then(setOrders).finally(() => setLoading(false))
  }, [token])

  if (loading) return (
    <div className="page" style={{ display: 'flex', justifyContent: 'center', paddingTop: 80 }}>
      <div className="spinner" style={{ width: 32, height: 32 }} />
    </div>
  )

  return (
    <div className="page fade-in">
      <h1 className="section-title">Mes Commandes</h1>
      <div className="divider" />

      {orders.length === 0 ? (
        <div className="empty">
          <div className="empty-icon">📦</div>
          <div className="empty-title">Aucune commande</div>
          <p style={{ fontSize: 13 }}>Vos commandes apparaîtront ici</p>
        </div>
      ) : (
        orders.map(order => (
          <div key={order.id} className="card order-card">
            <div className="order-header">
              <div>
                <div className="order-id">Commande #{order.id}</div>
                <div className="order-date">
                  {new Date(order.createdAt).toLocaleDateString('fr-FR', {
                    year: 'numeric', month: 'long', day: 'numeric'
                  })}
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span className={`status-badge status-${order.status}`}>{order.status}</span>
                <div className="order-total">{Number(order.totalAmount).toFixed(2)} €</div>
              </div>
            </div>

            <div className="order-items">
              {order.items.map((item, i) => (
                <div key={i} className="order-item-row">
                  <span>{item.productName} × {item.quantity}</span>
                  <span>{Number(item.lineTotal).toFixed(2)} €</span>
                </div>
              ))}
            </div>
          </div>
        ))
      )}
    </div>
  )
}
