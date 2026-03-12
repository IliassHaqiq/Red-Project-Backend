import { useMemo, useState } from 'react'
import './ProductDetailPage.css'

export default function ProductDetailPage({ product, token, onAddToCart, setPage, addToast }) {
  const [qty, setQty] = useState(1)

  const sortedImages = useMemo(() => {
    return [...(product?.images || [])].sort((a, b) => a.displayOrder - b.displayOrder)
  }, [product])

  const defaultImage =
    sortedImages.find((img) => img.primaryImage)?.imageUrl ||
    sortedImages[0]?.imageUrl ||
    '/placeholder-product.png'

  const [selectedImage, setSelectedImage] = useState(defaultImage)

  if (!product) return null

  const handleAdd = () => {
    onAddToCart(product, qty)
    addToast(`${product.name} ajouté au panier`)
  }

  return (
    <div className="product-detail-page">
      <button className="back-btn" onClick={() => setPage('shop')}>
        ← Retour
      </button>

      <div className="product-detail-layout">
        <div className="product-gallery">
          <img
            src={selectedImage}
            alt={product.name}
            className="product-main-image"
          />

          {sortedImages.length > 1 && (
            <div className="product-thumbnails">
              {sortedImages.map((img) => (
                <img
                  key={img.id || img.imageUrl}
                  src={img.imageUrl}
                  alt={product.name}
                  className={`product-thumbnail ${selectedImage === img.imageUrl ? 'active' : ''}`}
                  onClick={() => setSelectedImage(img.imageUrl)}
                />
              ))}
            </div>
          )}
        </div>

        <div className="product-info">
          <div className="product-category">{product.category}</div>
          <h1>{product.name}</h1>
          <div className="product-price">{Number(product.price).toFixed(2)} €</div>
          <p>{product.description}</p>

          <div className={`product-stock ${product.stock > 0 ? 'in-stock' : 'out-stock'}`} style={{ marginBottom: 24 }}>
            {product.stock > 0 ? `✓ ${product.stock} unités disponibles` : '✗ Rupture de stock'}
          </div>

          {product.stock > 0 && (
            <>
              <div className="qty-box">
                <span>Quantité</span>
                <div className="qty-controls">
                  <button onClick={() => setQty((q) => Math.max(1, q - 1))}>−</button>
                  <span>{qty}</span>
                  <button onClick={() => setQty((q) => Math.min(product.stock, q + 1))}>+</button>
                </div>
              </div>

              {token ? (
                <button className="btn-primary" onClick={handleAdd}>
                  Ajouter au panier
                </button>
              ) : (
                <button className="btn-primary" onClick={() => setPage('auth')}>
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