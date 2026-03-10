import { useState } from 'react'
import { createOrder } from '../api'
import './CartPage.css'

export default function CartPage({ cart, setCart, token, addToast, setPage }) {
  const [loading, setLoading] = useState(false)

  const updateQty = (id, delta) => {
    setCart(prev =>
      prev
        .map(i => i.id === id ? { ...i, qty: i.qty + delta } : i)
        .filter(i => i.qty > 0)
    )
  }

  const total = cart.reduce((sum, i) => sum + Number(i.price) * i.qty, 0)

  const placeOrder = async () => {
    setLoading(true)
    try {
      await createOrder(
        { items: cart.map(i => ({ productId: i.id, quantity: i.qty })) },
        token
      )
      setCart([])
      addToast('Commande passée avec succès !')
      setPage('orders')
    } catch (e) {
      addToast(e.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  if (cart.length === 0) return (
    <div className="page fade-in">
      <div className="empty">
        <div className="empty-icon">🛒</div>
        <div className="empty-title">Panier vide</div>
        <p style={{ fontSize: 13, marginBottom: 24 }}>Ajoutez des produits pour continuer</p>
        <button className="btn btn-primary" onClick={() => setPage('shop')}>Voir la boutique</button>
      </div>
    </div>
  )

  return (
    <div className="page fade-in">
      <div className="section-header">
        <div>
          <h1 className="section-title">Mon Panier</h1>
          <div className="divider" />
        </div>
      </div>

      <div className="cart-layout">
        {/* Items */}
        <div>
          {cart.map(item => (
            <div key={item.id} className="cart-item">
              <div className="cart-item-info">
                <div className="cart-item-name">{item.name}</div>
                <div className="cart-item-unit">{Number(item.price).toFixed(2)} € / unité</div>
              </div>
              <div className="qty-ctrl">
                <button className="qty-btn" onClick={() => updateQty(item.id, -1)}>−</button>
                <span className="qty-value">{item.qty}</span>
                <button className="qty-btn" onClick={() => updateQty(item.id, +1)}>+</button>
              </div>
              <div className="cart-item-total">
                {(Number(item.price) * item.qty).toFixed(2)} €
              </div>
            </div>
          ))}
        </div>

        {/* Summary */}
        <div className="card cart-summary">
          <div className="cart-summary-title">Récapitulatif</div>
          <div className="cart-summary-row">
            <span>Sous-total</span>
            <span>{total.toFixed(2)} €</span>
          </div>
          <div className="cart-summary-row">
            <span>Livraison</span>
            <span>Gratuite</span>
          </div>
          <div className="cart-summary-divider" />
          <div className="cart-summary-total">
            <span>Total</span>
            <span className="cart-total-amount">{total.toFixed(2)} €</span>
          </div>
          <button
            className="btn btn-primary cart-order-btn"
            onClick={placeOrder}
            disabled={loading}
          >
            {loading ? <span className="spinner" /> : 'Commander'}
          </button>
        </div>
      </div>
    </div>
  )
}
