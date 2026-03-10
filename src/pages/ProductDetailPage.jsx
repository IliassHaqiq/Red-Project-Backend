import { useState } from 'react'
import './ProductDetailPage.css'

export default function ProductDetailPage({ product, token, onAddToCart, setPage, addToast }) {
  const [qty, setQty] = useState(1)

  if (!product) return null

  const handleAdd = () => {
    onAddToCart(product, qty)
    addToast(`${product.name} ajouté au panier`)
  }

  return (
    <div className="page fade-in">
      <button className="btn btn-ghost btn-sm detail-back" onClick={() => setPage('shop')}>
        ← Retour
      </button>

      <div className="product-detail">
        {/* Image placeholder */}
        <div className="detail-img">🛍️</div>

        {/* Info */}
        <div className="detail-info">
          <div className="detail-category">{product.category}</div>
          <h1 className="detail-name">{product.name}</h1>
          <div className="detail-price">{Number(product.price).toFixed(2)} €</div>

          <div className="detail-divider" />

          <p className="detail-desc">{product.description}</p>

          <div className={`product-stock ${product.stock > 0 ? 'in-stock' : 'out-stock'}`} style={{ marginBottom: 24 }}>
            {product.stock > 0 ? `✓ ${product.stock} unités disponibles` : '✗ Rupture de stock'}
          </div>

          {product.stock > 0 && (
            <>
              <div className="detail-qty-row">
                <label className="label" style={{ margin: 0 }}>Quantité</label>
                <div className="qty-ctrl">
                  <button className="qty-btn" onClick={() => setQty(q => Math.max(1, q - 1))}>−</button>
                  <span className="qty-value">{qty}</span>
                  <button className="qty-btn" onClick={() => setQty(q => Math.min(product.stock, q + 1))}>+</button>
                </div>
              </div>

              {token ? (
                <button className="btn btn-primary" onClick={handleAdd}>
                  Ajouter au panier
                </button>
              ) : (
                <button className="btn btn-ghost" onClick={() => setPage('auth')}>
                  Connectez-vous pour acheter
                </button>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  )
}
