import { useState, useEffect } from 'react'
import { getProducts, getCategories } from '../api'
import './ShopPage.css'

export default function ShopPage({ token, onAddToCart, addToast, setPage, setDetailProduct }) {
  const [products, setProducts]     = useState([])
  const [categories, setCategories] = useState([])
  const [activeCat, setActiveCat]   = useState('all')
  const [search, setSearch]         = useState('')
  const [loading, setLoading]       = useState(true)

  useEffect(() => {
    Promise.all([getProducts(), getCategories()])
      .then(([p, c]) => { setProducts(p); setCategories(c) })
      .finally(() => setLoading(false))
  }, [])

  const filtered = products.filter(p => {
    const matchCat    = activeCat === 'all' || p.category === activeCat
    const matchSearch = p.name.toLowerCase().includes(search.toLowerCase()) ||
                        p.description?.toLowerCase().includes(search.toLowerCase())
    return matchCat && matchSearch
  })

  const openDetail = (product) => {
    setDetailProduct(product)
    setPage('detail')
  }

  if (loading) return (
    <div className="page shop-loading">
      <div className="spinner" style={{ width: 32, height: 32 }} />
    </div>
  )

  return (
    <div className="page fade-in">

      {/* Header */}
      <div className="section-header">
        <div>
          <h1 className="section-title">Notre Collection</h1>
          <div className="divider" />
          <p className="shop-count">{filtered.length} produit{filtered.length !== 1 ? 's' : ''}</p>
        </div>
        <input
          className="input shop-search"
          placeholder="Rechercher..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      {/* Category filter */}
      <div className="cat-filter">
        <button className={`cat-btn ${activeCat === 'all' ? 'active' : ''}`} onClick={() => setActiveCat('all')}>
          Tout
        </button>
        {categories.map(c => (
          <button
            key={c.id}
            className={`cat-btn ${activeCat === c.name ? 'active' : ''}`}
            onClick={() => setActiveCat(c.name)}
          >
            {c.name}
          </button>
        ))}
      </div>

      {/* Grid */}
      {filtered.length === 0 ? (
        <div className="empty">
          <div className="empty-icon">🔍</div>
          <div className="empty-title">Aucun produit trouvé</div>
          <p>Essayez une autre catégorie ou terme de recherche</p>
        </div>
      ) : (
        <div className="products-grid">
          {filtered.map((p, i) => (
            <div
              key={p.id}
              className="product-card"
              style={{ animationDelay: `${i * 0.04}s` }}
              onClick={() => openDetail(p)}
            >
              <div className="product-category">{p.category}</div>
              <div className="product-name">{p.name}</div>
              <div className="product-price">{Number(p.price).toFixed(2)} €</div>
              <div className="product-desc">{p.description}</div>
              <div className={`product-stock ${p.stock > 0 ? 'in-stock' : 'out-stock'}`}>
                {p.stock > 0 ? `✓ ${p.stock} en stock` : '✗ Rupture de stock'}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
